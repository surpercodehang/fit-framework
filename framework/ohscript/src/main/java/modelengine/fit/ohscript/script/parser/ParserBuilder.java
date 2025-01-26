/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser;

import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.parser.codereader.CodeReader;
import modelengine.fit.ohscript.script.parser.codereader.OhFileReader;
import modelengine.fit.ohscript.script.parser.codereader.StringCodeReader;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.OhUtil;

import java.io.IOException;

/**
 * AST 解析器
 * 构建所有的 grammar->production的grammar集合
 * 该grammar集合通过grammar builder建立并验证所有grammar之间关系的正确性
 * parser通过LL(1)模式进行解析
 * 尽量转换left recursion，以确保top down解析方式有效
 * 部分复杂的有left recursion和FIST-FIRST Conflict的grammar单独处理，比如LIST类型：STATEMENT_LIST, VAR_LIST
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public class ParserBuilder {
    private static Grammars defaultGrammars = null;

    private static Lexer defaultLexer = null;

    private static PredictTable defaultPredictTable = null;

    private final Grammars grammars;

    private final Lexer lexer;

    private final PredictTable predictTable;

    private ASF asf;

    private boolean inTransaction;

    /**
     * AST 解析器的无参构造方法。
     */
    public ParserBuilder() {
        if (defaultGrammars == null) {
            initBuilder();
        }
        this.grammars = defaultGrammars;
        this.predictTable = defaultPredictTable;
        this.lexer = defaultLexer;
        this.asf = new ASF();
    }

    /**
     * AST 解析器的构造方法。
     *
     * @param grammarBuilder 表示给定的语法构建器的 {@link GrammarBuilder}。
     * @param lexer 表示给定的词法分析器的 {@link Lexer}。
     */
    public ParserBuilder(GrammarBuilder grammarBuilder, Lexer lexer) {
        initProductions(grammarBuilder);
        this.grammars = grammarBuilder.build();
        this.predictTable = new PredictTable(this.grammars);
        this.lexer = lexer;
        this.asf = new ASF();
    }

    private static synchronized void initBuilder() {
        if (defaultGrammars != null) {
            return;
        }
        GrammarBuilder grammarBuilder = new GrammarBuilder();
        defaultLexer = new Lexer();
        initProductions(grammarBuilder);
        Grammars grammarValue = grammarBuilder.build();
        defaultPredictTable = new PredictTable(grammarValue);
        defaultGrammars = grammarValue;
    }

    private static void initProductions(GrammarBuilder grammarBuilder) {
        addCoreGrammar(grammarBuilder);
        addExportGrammar(grammarBuilder);
        addImportDeclareGrammar(grammarBuilder);
        addKeywordGrammar(grammarBuilder);
        addStatementsGrammar(grammarBuilder);
        addSystemMethodGrammar(grammarBuilder);
        addExtensionGrammar(grammarBuilder);
        addLoopControlGrammar(grammarBuilder);
        addCommentGrammar(grammarBuilder);
        addVarLetGrammar(grammarBuilder);
        addExpressionOrAssignmentGrammar(grammarBuilder);
        addComplexGrammar(grammarBuilder);
        addMapDeclareGrammar(grammarBuilder);
        addArrayDeclareGrammar(grammarBuilder);
        addFunctionDeclareGrammar(grammarBuilder);
        addReturnGrammar(grammarBuilder);
        addMatchGrammar(grammarBuilder);
        addIfGrammar(grammarBuilder);
        addDoGrammar(grammarBuilder);
        addWhileGrammar(grammarBuilder);
        addEachGrammar(grammarBuilder);
        addForGrammar(grammarBuilder);
        addExpressionGrammar(grammarBuilder);
        addEntityGrammar(grammarBuilder);
        addPipeGrammar(grammarBuilder);
        addOhCallGrammar(grammarBuilder);
        addEntityCallGrammar(grammarBuilder);
        addArrayAndFunctionCallGrammar(grammarBuilder);
        addArrayAccessGrammar(grammarBuilder);
        addFunctionCallGrammar(grammarBuilder);
        addTernaryExpressionGrammar(grammarBuilder);
        addConditionExpressionGrammar(grammarBuilder);
    }

    private static void addCoreGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("SCRIPT->IMPORT_DECLARES STATEMENTS EXPORT_DECLARE");
    }

    private static void addExportGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("EXPORT_DECLARE->ε|export ID EXPORT_MORE'"); // not verified
        grammarBuilder.appendProductions("EXPORT_MORE'->; EXPORT_DECLARE|,ID EXPORT_MORE'");
    }

    private static void addImportDeclareGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("IMPORT_DECLARES-> ε|IMPORT_DECLARE IMPORT_DECLARES");
        grammarBuilder.appendProductions("IMPORT_DECLARE-> import VARS' from ID;");
        grammarBuilder.appendProductions("VARS'-> * AS'|IDS'");
        grammarBuilder.appendProductions("IDS'-> ID AS' IMPORT_MORE'");
        grammarBuilder.appendProductions("IMPORT_MORE'-> ε|,ID AS' IMPORT_MORE'"); // id should be from, but if it is
    }

    private static void addKeywordGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("AS'-> ε|as ID");
    }

    private static void addStatementsGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("STATEMENTS->ε|STATEMENT STATEMENTS");
        grammarBuilder.appendProductions(
                "STATEMENT->SYSTEM_EXTENSION|VAR_STATEMENT|LET_STATEMENT|RETURN_STATEMENT|IF_STATEMENT"
                        + "|DO_STATEMENT|WHILE_STATEMENT"
                        + "|FOR_STATEMENT|EACH_STATEMENT|EXPRESSION_STATEMENT|LOOP_CONTROL|COMMENT_STATEMENT"
                        + "|MATCH_STATEMENT");
    }

    private static void addSystemMethodGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("SYS_METHOD->~ID ID'~");
        grammarBuilder.appendProductions("ID'->ε|ID ID'");
    }

    private static void addExtensionGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("SYSTEM_EXTENSION->EXTENDED' ENTITY_BODY");
        grammarBuilder.appendProductions(
                "EXTENDED'-> STRING_TYPE|NUMBER_TYPE|BOOL_TYPE|ARRAY_TYPE|MAP_TYPE|FUNCTION_TYPE|OBJECT_TYPE");
    }

    private static void addLoopControlGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("LOOP_CONTROL->BREAK;|CONTINUE;");
    }

    private static void addCommentGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("COMMENT_STATEMENT->COMMENT COMMENT'");
        grammarBuilder.appendProductions("COMMENT'->ε|COMMENT_STATEMENT");
    }

    private static void addVarLetGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("LET_STATEMENT->LET ASSIGNMENT_STATEMENT");
        grammarBuilder.appendProductions("VAR_STATEMENT->VAR ASSIGNMENT_STATEMENT");
        grammarBuilder.appendProductions("ASSIGNMENT_STATEMENT->INITIAL_ASSIGNMENT ASSIGN'");
        grammarBuilder.appendProductions("ASSIGN'->;|,ASSIGNMENT_STATEMENT");
        grammarBuilder.appendProductions("INITIAL_ASSIGNMENT->PIPE_FORWARD ASSIGN_EXPR'");
        grammarBuilder.appendProductions("ASSIGN_EXPR'->ε| ASSIGN_OP' EXPRESSION");
    }

    private static void addExpressionOrAssignmentGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions(
                "EXPRESSION_STATEMENT->COMPLEX_DECLARE' END_OR_NOT'|THREE_ADDRESS_EXPRESSION' END_OR_NOT'");
        grammarBuilder.appendProductions(
                "THREE_ADDRESS_EXPRESSION'->CONDITION_EXPRESSION TERNARY_EXPRESSION ASSIGN_EXPR''");
        grammarBuilder.appendProductions("ASSIGN_EXPR''->ε|VAR_ASSIGNMENT");
        grammarBuilder.appendProductions("VAR_ASSIGNMENT->ASSIGN_OP' EXPRESSION");
        grammarBuilder.appendProductions("END_OR_NOT'->ε|;");
        grammarBuilder.appendProductions("ASSIGN_OP'->PLUS_EQUAL|MINUS_EQUAL|STAR_EQUAL|SLASH_EQUAL|=");
    }

    private static void addComplexGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("COMPLEX_DECLARE'->ENTITY_DECLARE|ARRAY_MAP_DECLARE'|FUNC_DECLARE");
        grammarBuilder.appendProductions("ARRAY_MAP_DECLARE'->MAP_DECLARE|ARRAY_DECLARE|ARRAY_MAP_DECLARE");
        grammarBuilder.appendProductions("ARRAY_MAP_DECLARE->[ITEMS']");
        grammarBuilder.appendProductions("ITEMS'->MAP_ITEMS'|ARRAY_ITEMS'");
    }

    private static void addMapDeclareGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("MAP_DECLARE->MAP");
        grammarBuilder.appendProductions("MAP_ITEMS'->STRING_COLON EXPRESSION MAP_ITEM'");
        grammarBuilder.appendProductions("MAP_ITEM'->ε|,STRING_COLON EXPRESSION MAP_ITEM'");
    }

    private static void addArrayDeclareGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("ARRAY_DECLARE->ARRAY");
        grammarBuilder.appendProductions("ARRAY_ITEMS'->EXPRESSION ARRAY_ITEM'");
        grammarBuilder.appendProductions("ARRAY_ITEM'->ε|,EXPRESSION ARRAY_ITEM'");
    }

    private static void addFunctionDeclareGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("FUNC_DECLARE->NORMAL_FUNC'|LAMBDA'");
        grammarBuilder.appendProductions("LAMBDA'->LAMBDA_START LAMBDA_BLOCK'");
        grammarBuilder.appendProductions("LAMBDA_BLOCK'->ARRAY_MAP_DECLARE'|FUNC_DECLARE|THREE_ADDRESS_EXPRESSION'");
        grammarBuilder.appendProductions("NORMAL_FUNC'->func NAME_OR_NOT'(ARGUMENTS) BLOCK'");
        grammarBuilder.appendProductions("BLOCK'->BLOCK_STATEMENT|EQUAL_GREATER LAMBDA_EXPRESSION");
        grammarBuilder.appendProductions("LAMBDA_EXPRESSION->COMPLEX_DECLARE'|THREE_ADDRESS_EXPRESSION'");
        grammarBuilder.appendProductions("NAME_OR_NOT'->ε|ID");
        grammarBuilder.appendProductions("ARGUMENTS->ε|ARGUMENT ARGUMENT'");
        grammarBuilder.appendProductions("ARGUMENT->ID");
        grammarBuilder.appendProductions("ARGUMENT'->ε|,ARGUMENTS");
    }

    private static void addReturnGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("RETURN_STATEMENT->RETURN'|SYS_METHOD");
        grammarBuilder.appendProductions("RETURN'->RETURN EXPRESSION;");
    }

    private static void addMatchGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions(
                "MATCH_STATEMENT->match ID{MATCH_BRANCH MATCH_MORE_BRANCHES' MATCH_ELSE_BRANCH'}");
        grammarBuilder.appendProductions("MATCH_BRANCH->OR MATCH_VAR MATCH_WHEN => MATCH_BLOCK");
        grammarBuilder.appendProductions("MATCH_MORE_BRANCHES'->ε|MATCH_MORE_BRANCH'");
        grammarBuilder.appendProductions("MATCH_MORE_BRANCH'->MATCH_BRANCH MATCH_MORE_BRANCHES'");
        grammarBuilder.appendProductions("MATCH_ELSE_BRANCH'->ε|MATCH_ELSE_BRANCH");
        grammarBuilder.appendProductions("MATCH_ELSE_BRANCH->MATCH_ELSE MATCH_BLOCK");
        grammarBuilder.appendProductions("MATCH_VAR->ID|TUPLE_UNPACKER|STRING|NUMBER");
        grammarBuilder.appendProductions("MATCH_WHEN->ε|if(RELATIONAL_CONDITION)");
        grammarBuilder.appendProductions("MATCH_BLOCK->EXPRESSION_STATEMENT");
    }

    private static void addIfGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("IF_STATEMENT->if(RELATIONAL_CONDITION) BLOCK_STATEMENT ELSE_STATEMENT");
        grammarBuilder.appendProductions("BLOCK_STATEMENT->{STATEMENTS}");
        grammarBuilder.appendProductions("ELSE_STATEMENT->ε|else ELSE_IF'");
        grammarBuilder.appendProductions("ELSE_IF'->BLOCK_STATEMENT|IF_STATEMENT");
    }

    private static void addDoGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("DO_STATEMENT->DO BLOCK_STATEMENT while(CONDITION_EXPRESSION)");
    }

    private static void addWhileGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("WHILE_STATEMENT->while(CONDITION_EXPRESSION) BLOCK_STATEMENT");
    }

    private static void addEachGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("EACH_STATEMENT->each TUPLE_UNPACKER in EXPRESSION BLOCK_STATEMENT");
    }

    private static void addForGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions(
                "FOR_STATEMENT->for(VAR_STATEMENT CONDITION_EXPRESSION;EXPRESSION_STATEMENT) BLOCK_STATEMENT");
    }

    private static void addExpressionGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("EXPRESSION->CONDITION_EXPRESSION TERNARY_EXPRESSION|COMPLEX_DECLARE'");
    }

    private static void addEntityGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("ENTITY_DECLARE->ENTITY_HEAD' ENTITY_BODY");
        grammarBuilder.appendProductions("ENTITY_HEAD'-> ε|ENTITY");
        grammarBuilder.appendProductions("ENTITY_BODY->ENTITY_BODY_BEGIN INITIAL_ASSIGNMENT FIELD_END' ENTITY_BODY'}");
        grammarBuilder.appendProductions("ENTITY_BODY'->ε|DOT INITIAL_ASSIGNMENT FIELD_END' ENTITY_BODY'");
        grammarBuilder.appendProductions("FIELD_END'->ε|;");

        grammarBuilder.appendProductions("ENTITY_EXTENSION->EXTEND JSON_ENTITY_BODY }");
        grammarBuilder.appendProductions("JAVA_CLASS'->UPPER_ID JAVA_NEW_OR_CALL'");
        grammarBuilder.appendProductions("JAVA_NEW_OR_CALL'->JAVA_NEW|JAVA_STATIC_CALL");
        grammarBuilder.appendProductions("JAVA_NEW->{ JSON_ENTITY_BODY }");
        grammarBuilder.appendProductions("JAVA_STATIC_CALL->MEMBER'");
    }

    private static void addPipeGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("PIPE_FORWARD->PIPE_START' PIPE'");
        grammarBuilder.appendProductions("PIPE_START'->STRING|NUMBER|true|false|OH_CALL");
        grammarBuilder.appendProductions("PIPE'->ε|>> OH_CALL PIPE'");
    }

    private static void addOhCallGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("OH_CALL->OH' ENTITY_CALL");
        grammarBuilder.appendProductions("OH'->ε|OH");
    }

    private static void addEntityCallGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("ENTITY_CALL->COMPLEX_CALL' MEMBER'");
        grammarBuilder.appendProductions("MEMBER'->ε|.ENTITY_TUPLE_CALL'");
        grammarBuilder.appendProductions("ENTITY_TUPLE_CALL'->ENTITY_CALL|NUMBER MEMBER'");
    }

    private static void addArrayAndFunctionCallGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions(
                "COMPLEX_CALL'->JAVA_CLASS'|ID POST_COMPLEX_CALL' | TUPLE_DECLARE POST_COMPLEX_CALL'");
        grammarBuilder.appendProductions("POST_COMPLEX_CALL'->ε|ARRAY_ACCESS|FUNC_CALL|ENTITY_EXTENSION|ENTITY_BODY");
    }

    private static void addArrayAccessGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("ARRAY_ACCESS->[EXPRESSION] POST_COMPLEX_CALL'");
    }

    private static void addFunctionCallGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("FUNC_CALL->(ARGS') POST_COMPLEX_CALL'");
        grammarBuilder.appendProductions("ARGS'->ε|ARGS_ID1' ");
        grammarBuilder.appendProductions("ARGS_ID1'->ARG' ARGS_ID2'");
        grammarBuilder.appendProductions("ARGS_ID2'->ε|,ARG' ARGS_ID2'");
        grammarBuilder.appendProductions("ARG'->EXPRESSION");
    }

    private static void addTernaryExpressionGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("TERNARY_EXPRESSION->ε|? EXPRESSION : EXPRESSION");
    }

    private static void addConditionExpressionGrammar(GrammarBuilder grammarBuilder) {
        grammarBuilder.appendProductions("CONDITION_EXPRESSION->RELATIONAL_CONDITION AND_OR_CONDITION'");
        grammarBuilder.appendProductions("AND_OR_CONDITION'->ε|AND_OR' RELATIONAL_CONDITION AND_OR_CONDITION'");
        grammarBuilder.appendProductions("AND_OR'->&&|OR_OR");
        grammarBuilder.appendProductions("RELATIONAL_CONDITION->NEGATION REL_CONDITION'");
        grammarBuilder.appendProductions("REL_CONDITION'->ε|RELATIONAL' NEGATION REL_CONDITION'");
        grammarBuilder.appendProductions("RELATIONAL'->==|!=|>|>=|<|<=|<:|=:");
        grammarBuilder.appendProductions("NEGATION->NUMERIC_EXPRESSION | !NUMERIC_EXPRESSION");
        grammarBuilder.appendProductions("NUMERIC_EXPRESSION->TERM_EXPRESSION NUMERIC_EXPR'");
        grammarBuilder.appendProductions("NUMERIC_EXPR'->ε|NUMERIC_OP' NUMERIC_EXPRESSION");
        grammarBuilder.appendProductions("NUMERIC_OP'->+|-");
        grammarBuilder.appendProductions("TERM_EXPRESSION->UNARY_EXPRESSION TERM_EXPR'");
        grammarBuilder.appendProductions("TERM_EXPR'->ε|TERM_OP' TERM_EXPRESSION");
        grammarBuilder.appendProductions("TERM_OP'->*|/|%|^");
        grammarBuilder.appendProductions(
                "UNARY_EXPRESSION->UNARY_LEFT_OP' FACTOR_EXPRESSION |FACTOR_EXPRESSION UNARY_RIGHT_OP'");
        grammarBuilder.appendProductions("UNARY_LEFT_OP'->++|--|-");
        grammarBuilder.appendProductions("UNARY_RIGHT_OP'->ε|++|--");
        grammarBuilder.appendProductions("FACTOR_EXPRESSION->PIPE_FORWARD|BLOCK_OR_ENTITY'|LOCK_ASYNC_SAFE_BLOCK'");
        grammarBuilder.appendProductions("LOCK_ASYNC_SAFE_BLOCK'->LOCK_BLOCK|ASYNC_BLOCK|SAFE_BLOCK");
        grammarBuilder.appendProductions("LOCK_BLOCK->LOCK BLOCK_STATEMENT");
        grammarBuilder.appendProductions("ASYNC_BLOCK->ASYNC BLOCK_STATEMENT");
        grammarBuilder.appendProductions("SAFE_BLOCK->SAFE BLOCK_STATEMENT");
        grammarBuilder.appendProductions("BLOCK_OR_ENTITY'->{BLOCK_OR_ENTITY_BODY'}");
        grammarBuilder.appendProductions("BLOCK_OR_ENTITY_BODY'->EXPRESS_BLOCK_STATEMENT|JSON_ENTITY_BODY");
        grammarBuilder.appendProductions("EXPRESS_BLOCK_STATEMENT->STATEMENTS");
        grammarBuilder.appendProductions("JSON_ENTITY_BODY->JSON_ITEM JSON_ITEMS'");
        grammarBuilder.appendProductions("JSON_ITEM->ID_COLON EXPRESSION");
        grammarBuilder.appendProductions("JSON_ITEMS'->ε|,JSON_ITEMS''");
        grammarBuilder.appendProductions("JSON_ITEMS''->ε|JSON_ITEM JSON_ITEMS'");

        grammarBuilder.appendProductions("TUPLE_DECLARE->(TUPLE_EXPRESSION' TUPLE')");
        grammarBuilder.appendProductions("TUPLE'->ε|,TUPLE_EXPRESSION' TUPLE'");
        grammarBuilder.appendProductions("TUPLE_EXPRESSION'->..|EXPRESSION");
        grammarBuilder.appendProductions("TUPLE_UNPACKER->(TUPLE_ID',TUPLE_ID' UNPACKER')");
        grammarBuilder.appendProductions("UNPACKER'->ε|,TUPLE_ID' UNPACKER'");
        grammarBuilder.appendProductions("TUPLE_ID'->..|STRING|NUMBER|ID|TUPLE_UNPACKER");
    }

    private void loadSystemCode() {
        this.addExternalOh("util", new OhUtil());
        String utilCode = "let log=ext::util.logPanic(\"log\"), warning=ext::util.logPanic(\"warning\"), "
                + "error=ext::util.logPanic(\"error\"),panic= ext::util.panic, sleep=ext::util.sleep; "
                + "export log, warning, error, panic, sleep;";
        this.parseString(Constants.SYSTEM_UTIL, utilCode);
        String imports = "import log, warning, error, panic, sleep from " + Constants.SYSTEM_UTIL + ";";
        String arrayCode = "_array_{" + ".size=func(){~array_size~}" + ".insert=func(i,v){~array_insert i v~}"
                + ".remove=func(i){~array_remove i~}" + ".push=func(v){this.insert(this.size(),v)}" + "}";

        String mapCode = "";
        String strCode = "";
        String numCode = "";
        String extCode = imports + arrayCode + mapCode + strCode + numCode;
        this.parseString(Constants.SYSTEM_EXTENSION, extCode);
    }

    /**
     * 获取当前的语法集合
     *
     * @return 返回当前的语法集合
     */
    public Grammars grammars() {
        return this.grammars;
    }

    /**
     * 为指定的源代码创建一个解析器
     *
     * @param source 源码
     * @return 解析器
     */
    public Parser create(String source) {
        return new Parser(source, this.grammars, this.lexer, this.predictTable, this.asf);
    }

    private AST parse(String source, CodeReader codeReader) {
        boolean isSingle = false;
        if (!this.inTransaction) {
            this.begin();
            isSingle = true;
        }
        Parser parser = this.create(source);
        parser.parse(codeReader);
        AST ast = parser.done(this.inTransaction);
        if (isSingle) {
            this.done();
        }
        return ast;
    }

    /**
     * 解析指定的源代码字符串
     *
     * @param source 源码
     * @param codeSegment 代码段
     * @return 解析后的AST对象
     */
    public AST parseString(String source, String codeSegment) {
        return parse(source, new StringCodeReader(codeSegment));
    }

    /**
     * 解析指定的文件路径的源代码
     *
     * @param source 源码
     * @param filePath 文件路径
     * @return 解析后的AST对象
     * @throws IOException 如果读取文件时出现IO错误
     */
    public AST parseFile(String source, String filePath) throws IOException {
        OhFileReader codeReader = new OhFileReader(filePath);
        try {
            return parse(source, codeReader);
        } finally {
            codeReader.close();
        }
    }

    /**
     * 获取当前的ASF对象
     *
     * @return 返回当前的ASF对象
     */
    public ASF asf() {
        return this.asf;
    }

    /**
     * 添加外部的Oh方法
     *
     * @param key 表示外部方法的键值
     * @param oh 表示外部方法的对象
     */
    public void addExternalOh(String key, Object oh) {
        this.asf().addExternalOh(key, oh);
    }

    /**
     * 添加外部的Oh方法
     *
     * @param alias 表示外部方法的别名
     * @param fitGenericableId 表示外部方法的FitGenericableId
     * @param argNum 表示外部方法的参数数量
     */
    public void addFitOh(String alias, String fitGenericableId, Integer argNum) {
        this.asf().addFitOh(alias, fitGenericableId, argNum);
    }

    /**
     * 添加外部的Http方法
     *
     * @param alias 表示外部方法的别名
     * @param method 表示Http请求的方法，如get, post等
     * @param url 表示Http请求的url
     */
    public void addHttpOh(String alias, String method, String url) {
        this.asf().addHttpOh(alias, url, method);
    }

    /**
     * 结束一个事务，对AST列表中的每个AST进行符号化和语义检查
     *
     * @return 返回当前的ASF对象
     */
    public ASF done() {
        this.asf.asts().forEach(a -> {
            a.symbolize(a.start(), false, true);
            a.semanticCheck(a.start());
        });
        this.inTransaction = false;
        return this.asf();
    }

    /**
     * 开始一个事务，清空当前的AST列表，并加载系统代码
     */
    public void begin() {
        this.asf.asts().clear();
        this.inTransaction = true;
        this.loadSystemCode();
    }

    /**
     * 重置ASF对象
     * 重置ASF对象，创建一个新的ASF对象
     */
    public void reset() {
        this.asf = new ASF();
    }

    /**
     * 添加外部的类
     *
     * @param key 表示外部类的键值
     * @param clazz 表示外部类的Class对象
     */
    public void addExternalClass(String key, Class<?> clazz) {
        this.asf().addExternalClass(key, clazz);
    }
}
