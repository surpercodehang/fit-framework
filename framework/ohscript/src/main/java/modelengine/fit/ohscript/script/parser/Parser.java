/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import static modelengine.fit.ohscript.script.parser.NonTerminal.SCRIPT;

import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.codereader.CodeReader;
import modelengine.fit.ohscript.script.parser.nodes.EndNode;
import modelengine.fit.ohscript.script.parser.nodes.NonTerminalNode;
import modelengine.fit.ohscript.script.parser.nodes.ScriptNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * parse a script segment, can be regarded as a script file
 * in this segment, only have one script start node
 * by the end of this file, parser will build an AST object to represent a whole script structure
 * this AST will be used in script executing
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public class Parser {
    private static final List<String> imports = new ArrayList<>();

    static {
        imports.add("import log, warning, error, panic, sleep from " + Constants.SYSTEM_UTIL + ";");
        imports.add("import * from " + Constants.SYSTEM_EXTENSION + ";");
    }

    private final Grammars grammars;

    private final PredictTable predictTable;

    private final Lexer lexer;

    private final Stack<SyntaxNode> stack;

    private final AST ast;

    private final boolean isStartState = true;

    /**
     * 构造函数
     *
     * @param source 源码
     * @param grammars 语法规则
     * @param lexer 词法分析器
     * @param predictTable 预测表
     * @param asf 语法分析器的工厂
     */
    public Parser(String source, Grammars grammars, Lexer lexer, PredictTable predictTable, ASF asf) {
        this.grammars = grammars;
        this.lexer = lexer;
        this.stack = new Stack<>();
        this.stack.push(new EndNode());

        NonTerminalNode start = SCRIPT.parse();
        (ObjectUtils.<ScriptNode>cast(start)).setSource(source);
        this.stack.push(start);
        this.predictTable = predictTable;
        this.ast = new AST(start);
        asf.add(ast);
    }

    /**
     * parser will parse input line by line
     *
     * @param codeReader reader for parsing line
     */
    public void parseReader(CodeReader codeReader) {
        if (stack.empty()) {
            Tool.warn("script parsing has completed");
            return;
        }
        String line = codeReader.readLine();

        if (parseLine(line)) {
            return;
        }
        parseReader(codeReader);
    }

    private boolean parseLine(String line) {
        List<Token> tokens = this.lexer.scan(line);
        int cursor = 0;
        while (!stack.empty()) {
            Token token = tokens.get(cursor);
            SyntaxNode node = this.stack.pop();
            if (node instanceof NonTerminalNode) {
                matchNonTerminalToken((NonTerminalNode) node, token, line);
                continue;
            } else {
                matchTerminalToken(ObjectUtils.cast(node), token, line);
            }
            if (token.tokenType() == Terminal.END) {
                return true;
            }
            cursor++;
            if (tokens.get(cursor).tokenType() == Terminal.EOL) {
                cursor++;
            }
            if (cursor == tokens.size()) {
                break;
            }
        }
        return false;
    }

    /**
     * if the production symbol on the top of stack is a non-terminal, then find out the grammar corresponding to the
     * non terminal
     * via predictTable(FIRST combination) to match the production with the input token
     * if match nothing, throw grammar error exception which is raised from predictTable.match()
     * if match some production, create non-terminal corresponding Node
     * and pop the non-terminal from stack
     * and then push the matched production symbols into stack for the next token match
     *
     * @param node non-terminal on the top of stack
     * @param token on the top of input
     * @param line line
     */
    private void matchNonTerminalToken(NonTerminalNode node, Token token, String line) {
        Grammar grammar = grammars.get(node.name());
        Production production = this.predictTable.match(token, grammar, line);

        List<Symbol> symbols = production.symbols();
        // 反向遍历该non terminal node type命中production的所有symbol
        // 这些symbol创建SyntaxNode加入到该non terminal node下面
        // 并且推入栈，替代node的位置，开启下一层次的匹配
        for (int i = symbols.size() - 1; i >= 0; i--) {
            Symbol symbol = symbols.get(i);
            if (symbol.symbol() == Terminal.EPSILON) {
                continue;
            }
            SyntaxNode newNode = symbol.symbol().parse();
            newNode.setName(symbol.name());
            node.addChild(newNode, 0);
            this.stack.push(newNode);
        }
    }

    /**
     * if the production symbol on the top of stack is a terminal, then the token in the top of input should exactly
     * match the terminal
     * like the symbol on the top of stack is IF,then the token from the top of input should be IF, corresponding the
     * input characters "if"
     *
     * @param node terminal on the top of stack
     * @param token on the top of input
     * @param line line
     */
    private void matchTerminalToken(TerminalNode node, Token token, String line) {
        if (token.tokenType() == node.nodeType()) {
            // 如果terminal匹配正确，将实际token放入该terminal node。一般对变量型node有价值，比如ID
            node.setToken(token);
        } else {
            Tool.grammarError(
                    "unexpected token: " + token.lexeme() + ", expected token in " + node.parent().name() + " is "
                            + node.nodeType().name() + ", line: " + token.line() + " start: " + token.start() + " end: "
                            + token.end() + System.lineSeparator() + line);
        }
    }

    /**
     * build AST object
     * the tree before AST build is a raw tree
     * the raw tree has duplicated and redundant information created by complicated recursive productions
     * like 'NAMESPACE->ID IDS', 'IDS'->ε|.NAMESPACE' will create layered structure instead of pure ID.ID.ID format
     * non terminal will polish() the complex structure to right format
     *
     * @param inTransaction whether in transaction
     * @return AST object with polished tree node structure
     */
    public AST done(boolean inTransaction) {
        if (!this.stack.isEmpty()) {
            Tool.grammarError("the parser is not finished with correct input");
        }
        ast.init(inTransaction);
        return ast;
    }

    /**
     * 解析源码
     *
     * @param reader 源码的reader
     */
    public void parse(CodeReader reader) {
        if (!this.ast.source().contains("sys_")) {
            for (String anImport : imports) {
                parseLine(anImport);
            }
        }
        this.parseReader(reader);
    }
}
