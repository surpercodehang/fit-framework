/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.Interpreter;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.IdentifierEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.SymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.UnknownSymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.NodeType;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fitframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 语法树中的终结节点
 * means it is a leaf node
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public class TerminalNode extends SyntaxNode implements Serializable {
    private static final Map<String, Interpreter> interpreterMap = new ConcurrentHashMap<>();

    private static final long serialVersionUID = 2942191759739959915L;

    private static final String MEMBER_FLAG = ".";

    /**
     * 位置信息
     */
    protected Location location;

    private final Terminal nodeType;

    private Token token;

    /**
     * 构造函数
     *
     * @param nodeType 终结节点类型
     */
    public TerminalNode(Terminal nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * 构造一个unit节点
     *
     * @return TerminalNode
     */
    public static TerminalNode unit() {
        return new TerminalNode(Terminal.UNIT);
    }

    /**
     * 创建一个模拟的ID类型的终结节点
     *
     * @param idName ID名称
     * @return 模拟的ID类型的终结节点
     */
    public static TerminalNode mockId(String idName) {
        return new TerminalNode(Terminal.ID).setToken(new Token(Terminal.ID, idName, 0, 0, 0));
    }

    /**
     * 创建一个模拟的终结节点
     *
     * @param terminal 终结节点类型
     * @return 模拟的终结节点
     */
    public static TerminalNode mock(Terminal terminal) {
        return new TerminalNode(terminal).setToken(new Token(terminal, terminal.text(), 0, 0, 0));
    }

    /**
     * 设置token
     *
     * @param token token
     * @return 当前对象
     */
    public TerminalNode setToken(Token token) {
        this.token = token;
        this.location = new Location(token.line(), token.line(), token.start(), token.end());
        return this;
    }

    /**
     * 获取token
     *
     * @return token
     */
    public Token token() {
        return this.token;
    }

    @Override
    public Location location() {
        return this.location == null ? new Location(1, 1, 1, 1) : this.location;
    }

    @Override
    public Terminal nodeType() {
        return this.nodeType;
    }

    /**
     * 判断该符号是否为私有的
     *
     * @return 是否为私有
     */
    public boolean isPrivate() {
        return this.token().lexeme().substring(0, 1).equalsIgnoreCase(Constants.UNDER_LINE) || Objects.equals(
                this.lexeme(), Constants.BASE);
    }

    @Override
    public boolean typeInferIgnored() {
        if ((this.parent() instanceof ImportNode)) {
            ImportNode parent = (ImportNode) this.parent();
            // check module
            if (parent.source() == this) {
                return true;
            }
            // check import variable
            return !parent.symbols().stream().anyMatch(p -> p.second() == this);
        }
        return false;
    }

    @Override
    public void initTypeExpr(TypeExpr expr) {
        if (this.nodeType == Terminal.ID) {
            if (this.typeInferIgnored()) {
                return;
            }
            SymbolEntry entry = this.symbolEntry();
            if (entry.typeExpr() == expr) {
                return;
            }
            if (entry instanceof IdentifierEntry) {
                ((IdentifierEntry) entry).setTypeExpr(expr);
            }
        }
    }

    @Override
    public TypeExpr typeExpr() {
        if (this.typeExpr == null) {
            switch (this.nodeType) {
                case NUMBER:
                    this.typeExpr = TypeExprFactory.createNumber(this);
                    break;
                case STRING:
                    this.typeExpr = TypeExprFactory.createString(this);
                    break;
                case TRUE:
                case FALSE:
                    this.typeExpr = TypeExprFactory.createBool(this);
                    break;
                case UNIT:
                    this.typeExpr = TypeExprFactory.createUnit();
                    break;
                default:
            }
        }
        if (this.isSymbol(this.nodeType)) {
            this.typeExpr = this.symbolEntry().typeExpr();
        }
        return super.typeExpr();
    }

    /**
     * 判断给定的节点类型是否为符号
     *
     * @param nodeType 节点类型
     * @return 是否为符号
     */
    private boolean isSymbol(NodeType nodeType) {
        if (nodeType == Terminal.ID) {
            return true;
        }
        if (nodeType instanceof Terminal) {
            String text = ((Terminal) nodeType).text();
            return text.startsWith(Constants.UNDER_LINE) && text.endsWith(Constants.UNDER_LINE);
        }
        return false;
    }

    /**
     * only working when the terminal is an id reference
     * like variable, function, class ....
     * indicate the variable declare terminal node of this variable reference
     *
     * @return declare terminal node id
     */
    public SymbolEntry symbolEntry() {
        if (StringUtils.equals(this.lexeme(), Constants.THIS)) {
            return getThisSymbolEntry();
        }
        if (this.parent() != null && this.parent().nodeType() == NonTerminal.SYS_METHOD
                && this.parent().child(1) == this) {
            return new UnknownSymbolEntry(this);
        }

        SymbolEntry symbolEntry = this.ast().symbolTable().getSymbol(this.lexeme(), this.scope());
        if (symbolEntry == null) {
            symbolEntry = this.ast().symbolTable().getSymbol(MEMBER_FLAG + this.lexeme(), this.scope());
            if (symbolEntry == null) {
                this.panic(SyntaxError.VARIABLE_NOT_DEFINED);
                return new UnknownSymbolEntry(this);
            }
        }

        return symbolEntry;
    }

    /**
     * 获取this关键字对应的符号表项
     *
     * @return this关键字对应的符号表项
     */
    public SymbolEntry getThisSymbolEntry() {
        return this.tryGetThisSymbolEntry(true);
    }

    /**
     * 尝试获取this关键字对应的符号表项
     *
     * @param enablePanic 是否启用报错机制
     * @return this关键字对应的符号表项
     */
    public SymbolEntry tryGetThisSymbolEntry(boolean enablePanic) {
        SyntaxNode parent = this.parent;
        EntityBodyNode body = null;
        while (parent != null) {
            if (parent instanceof EntityBodyNode) {
                body = (EntityBodyNode) parent;
                break;
            }
            parent = parent.parent();
        }
        if (body == null) {
            if (enablePanic) {
                this.panic(SyntaxError.ENTITY_NOT_FOUND);
            }
            return new UnknownSymbolEntry(this);
        } else {
            if (body.host() == null) {
                return body.parent().declaredName().symbolEntry();
            } else {
                return body.declaredName().symbolEntry();
            }
        }
    }

    @Override
    public String lexeme() {
        if (this.token() == null) {
            return this.nodeType().text();
        } else {
            return this.token().lexeme();
        }
    }

    @Override
    public TerminalNode declaredName() {
        return this;
    }

    @Override
    public ReturnValue interpret(ASTEnv env, ActivationContext current) throws OhPanic {
        try {
            return this.getInterpreter(this.nodeType.name()).interpret(this, env, current);
        } catch (OhPanic p) {
            throw p;
        } catch (Exception e) {
            return ReturnValue.IGNORE;
        }
    }

    /**
     * 通过名称获取解释器，增加了缓存
     *
     * @param name 解释器名称
     * @return 解释器
     */
    private Interpreter getInterpreter(String name) {
        Interpreter interpreter = interpreterMap.get(name);
        if (interpreter == null) {
            Interpreter result;
            try {
                result = Interpreter.valueOf(name);
            } catch (IllegalArgumentException e) {
                result = Interpreter.ERROR_NOT_FOUND_IGNORE;
            }
            interpreter = result;
            interpreterMap.put(name, interpreter);
        }
        return interpreter;
    }
}
