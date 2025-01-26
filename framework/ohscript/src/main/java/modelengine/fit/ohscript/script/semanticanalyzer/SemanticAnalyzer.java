/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.nodes.ArgumentNode;
import modelengine.fit.ohscript.script.parser.nodes.ArrayAccessNode;
import modelengine.fit.ohscript.script.parser.nodes.ArrayDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EachNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityBodyNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityCallNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.EntityExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.ImportNode;
import modelengine.fit.ohscript.script.parser.nodes.InitialAssignmentNode;
import modelengine.fit.ohscript.script.parser.nodes.MapDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.parser.nodes.TupleUnPackerNode;
import modelengine.fit.ohscript.script.parser.nodes.VarStatementNode;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.IdentifierEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.SymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.symbolentries.UnknownSymbolEntry;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.ExprTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericFunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.AbstractTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.Projectable;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.BoolTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.DoubleFunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.EntityTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.FunctionTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.IgnoreTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.NumberTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.StringTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.TupleTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnknownTypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * 语义分析器枚举
 *
 * @since 1.0
 */
public enum SemanticAnalyzer implements SemanticAble {
    IMPORT_DECLARE {
        @Override
        public void symbolize(SyntaxNode node) {
            ImportNode importNode = ObjectUtils.cast(node);
            for (Pair<TerminalNode, TerminalNode> symbol : importNode.symbols()) {
                node.ast().symbolTable().getScope(node.ast().start().scope()).addIdentifier(symbol.second(), false);
            }
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            ImportNode importNode = ObjectUtils.cast(node);
            Optional<AST> ast = node.ast().asf().ast(importNode.source().lexeme());
            if (ast.isPresent()) {
                Map<String, TypeExpr> exports = new HashMap<>();
                ast.get().exports().forEach(e -> exports.put(e.lexeme(), e.typeExpr()));
                for (Pair<TerminalNode, TerminalNode> symbol : importNode.symbols()) {
                    String key = symbol.first().lexeme();
                    if (Objects.equals(key, Constants.STAR)) {
                        continue;
                    }
                    if (exports.containsKey(key)) {
                        (ObjectUtils.<IdentifierEntry>cast(node.ast()
                                .symbolTable()
                                .getSymbol(symbol.second().lexeme(), node.scope()))).setTypeExpr(exports.get(key));
                    } else {
                        symbol.second().panic(SyntaxError.IMPORT_ERROR_ID);
                    }
                }
            } else {
                node.panic(SyntaxError.IMPORT_ERROR_SOURCE);
            }
            return super.typeInfer(node);
        }
    },
    VAR_STATEMENT {
        @Override
        public void symbolize(SyntaxNode node) {
            VarStatementNode me = ObjectUtils.cast(node);
            List<SyntaxNode> assignments = me.children();
            assignments.removeIf(c -> c instanceof TerminalNode || c.nodeType() != NonTerminal.INITIAL_ASSIGNMENT);
            assignments.forEach(assignment -> {
                List<TerminalNode> lefts = this.fetchIds(assignment.child(0));
                for (TerminalNode left : lefts) {
                    me.ast().symbolTable().getScope(node.scope()).addIdentifier(left, me.mutable());
                }
                if (assignment.childCount() == 1) {
                    if (!me.mutable()) {
                        assignment.child(0).panic(SyntaxError.CONST_NOT_INITIALIZED);
                    }
                }
            });
        }
    },
    LET_STATEMENT {
        @Override
        public void symbolize(SyntaxNode node) {
            VAR_STATEMENT.symbolize(node);
        }
    },
    INITIAL_ASSIGNMENT {
        @Override
        public void symbolize(SyntaxNode node) {
            List<TerminalNode> lefts = this.fetchIds(node.child(0));
            for (TerminalNode left : lefts) {
                SymbolEntry entry = left.symbolEntry();

                if (entry == null) {
                    left.panic(SyntaxError.VARIABLE_NOT_DEFINED);
                }
            }
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            SyntaxNode left = node.child(0);
            SymbolEntry leftEntry = (ObjectUtils.<TerminalNode>cast(left)).symbolEntry();
            TypeExpr leftType = leftEntry.typeExpr();
            if (node.childCount() == 1) {
                if (leftEntry instanceof IdentifierEntry && !((IdentifierEntry) leftEntry).mutable()
                        && (leftType instanceof UnknownTypeExpr)) {
                    left.panic(SyntaxError.CONST_NOT_INITIALIZED);
                }
                return super.typeInfer(node);
            }
            SyntaxNode right = node.child(2);
            TypeExpr rightType = right.typeExpr();
            if (leftType instanceof UnknownTypeExpr) { // left must be an id
                (ObjectUtils.<IdentifierEntry>cast(leftEntry)).setTypeExpr(
                        rightType == null ? TypeExprFactory.createUnknown() : rightType); // for base will return null?
            } else if (left instanceof TupleUnPackerNode) {
                tupleUnpackerAssignInfer(ObjectUtils.cast(left), ObjectUtils.cast(rightType));
            } else {
                normalAssignInfer(left, leftType, rightType);
            }
            return super.typeInfer(node);
        }

        private void tupleUnpackerAssignInfer(TupleUnPackerNode unpacker, EntityTypeExpr rightType) {
            if (!(rightType instanceof EntityTypeExpr)) {
                unpacker.panic(SyntaxError.TYPE_MISMATCH);
            }
            List<SyntaxNode> members = unpacker.items();
            Map<String, TypeExpr> all = rightType.members();
            Map<String, TypeExpr> needs = new HashMap<>();
            for (String key : all.keySet()) {
                if (key.indexOf(".") != 0) {
                    continue;
                }
                needs.put(key, all.get(key));
            }
            if (rightType instanceof TupleTypeExpr) { // tuple
                int offset = 0;
                for (int i = 0; i < members.size(); i++) {
                    SyntaxNode member = members.get(i);
                    String id = "." + (i + offset);
                    if (member.nodeType() == Terminal.DOT_DOT) { // match the tail elements
                        offset = needs.size() - members.size();
                        continue;
                    }
                    if (member instanceof TerminalNode) {
                        normalAssignInfer(member, TypeExprFactory.createUnknown(), needs.get(id));
                    }
                    if (member instanceof TupleUnPackerNode) {
                        tupleUnpackerAssignInfer(ObjectUtils.cast(member), ObjectUtils.cast(needs.get(id)));
                    }
                }
            } else { // entity
                for (SyntaxNode member : members) {
                    if (member instanceof TerminalNode) {
                        normalAssignInfer(member, TypeExprFactory.createUnknown(), needs.get(member.lexeme()));
                    }
                    if (member instanceof TupleUnPackerNode) {
                        tupleUnpackerAssignInfer(ObjectUtils.cast(member),
                                ObjectUtils.cast(needs.get(member.lexeme())));
                    }
                }
            }
        }

        private void normalAssignInfer(SyntaxNode left, TypeExpr leftType, TypeExpr rightType) {
            if (leftType instanceof UnknownTypeExpr) {
                (ObjectUtils.<IdentifierEntry>cast((ObjectUtils.<TerminalNode>cast(left)).symbolEntry())).setTypeExpr(
                        rightType);
                return;
            }
            if (!rightType.is(leftType)) {
                left.panic(SyntaxError.TYPE_MISMATCH);
            }
        }
    },
    VAR_ASSIGNMENT {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            if (node.child(1).nodeType() == Terminal.EQUAL) {
                return INITIAL_ASSIGNMENT.typeInfer(node);
            } else {
                return NUMERIC_EXPRESSION.typeInfer(node);
            }
        }

        private void inferNumberOrString(TypeExpr expr) {
            NumberTypeExpr num = TypeExprFactory.createNumber(expr.node());
            StringTypeExpr str = TypeExprFactory.createString(expr.node());
            if (num.is(expr) && str.is(expr)) {
                if (expr instanceof AbstractTypeExpr) {
                    ((AbstractTypeExpr) expr).addSupposedToBe(num);
                    ((AbstractTypeExpr) expr).addSupposedToBe(str);
                }
            } else {
                expr.node().panic(SyntaxError.TYPE_MISMATCH);
            }
        }

        private void inferNumber(TypeExpr node) {
            NumberTypeExpr num = TypeExprFactory.createNumber(node.node());
            if (node.is(num)) {
                if (node instanceof AbstractTypeExpr) {
                    ((AbstractTypeExpr) node).addSupposedToBe(num);
                }
            } else {
                node.node().panic(SyntaxError.TYPE_MISMATCH);
            }
        }
    },
    ARGUMENT {
        @Override
        public void symbolize(SyntaxNode node) {
            ArgumentNode me = ObjectUtils.cast(node);
            node.ast().symbolTable().getScope(node.scope()).addArgument(me.argument());
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return ID.typeInfer(node);
        }
    },
    SYSTEM_EXTENSION {
        @Override
        public void symbolize(SyntaxNode node) {
            Terminal type = node.declaredName().nodeType();
            switch (type) {
                case ARRAY_TYPE:
                    if (node.ast().symbolTable().getSymbol(type.text(), Constants.ROOT_SCOPE) == null) {
                        node.ast().symbolTable().getScope(Constants.ROOT_SCOPE).addArray(node.declaredName());
                    }
                    break;
                default:
                    break;
            }
            for (InitialAssignmentNode member : node.members()) {
                TerminalNode id = ObjectUtils.cast(member.child(0));
                id.ast().symbolTable().getScope(member.scope()).addIdentifier(id, true, node);
            }
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return node.declaredName().typeExpr();
        }
    },
    ENTITY_DECLARE {
        @Override
        public void symbolize(SyntaxNode node) {
            entitySymbolize(node, scope -> scope.addEntity(ObjectUtils.cast(node)));
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            EntityDeclareNode me = ObjectUtils.cast(node);
            return me.declaredName().typeExpr();
        }
    },
    ENTITY_EXTENSION {
        @Override
        public void symbolize(SyntaxNode node) {
            entitySymbolize(node, scope -> scope.addEntityExtension(ObjectUtils.cast(node)));
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            SyntaxNode host = node;
            while (host instanceof EntityExtensionNode) {
                TypeExpr baseTypExpr = ((EntityExtensionNode) host).host().typeExpr();
                (ObjectUtils.<IdentifierEntry>cast(
                        host.ast().symbolTable().getSymbol(Constants.DOT + Constants.BASE, node.scope()))).setTypeExpr(
                        baseTypExpr);
                host = ((EntityExtensionNode) host).host();
            }
            return ENTITY_DECLARE.typeInfer(node);
        }
    },
    ENTITY_BODY {
        @Override
        public void symbolize(SyntaxNode node) {
            if (node.declaredName() == null) {
                return;
            }
            // add extended id into symbol table
            SymbolEntry entry = node.ast()
                    .symbolTable()
                    .getScope(node.parentScope())
                    .addIdentifier(node.declaredName(), false, null);
            if (entry instanceof UnknownSymbolEntry) {
                return;
            }
            // add new members into symbol table
            for (InitialAssignmentNode member : node.members()) {
                TerminalNode id = ObjectUtils.cast(member.child(0));
                node.ast().symbolTable().getScope(node.scope()).addIdentifier(id, true, node.declaredName());
            }
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            EntityBodyNode me = ObjectUtils.cast(node);
            if (me.host() == null) {
                return null;
            }
            TypeExpr hostExpr = me.host().typeExpr();
            // clone new extended entity type
            TypeExpr extendExpr = hostExpr.extend(me.declaredName());
            TypeExpr myExpr = me.declaredName().symbolEntry().typeExpr();
            extendExpr.myMembers().putAll(myExpr.myMembers());
            // set the extended id expression to cloned one
            (ObjectUtils.<IdentifierEntry>cast(me.declaredName().symbolEntry())).setTypeExpr(extendExpr);
            // set the .base to original entity
            (ObjectUtils.<IdentifierEntry>cast(
                    node.ast().symbolTable().getSymbol(Constants.DOT + Constants.BASE, node.scope()))).setTypeExpr(
                    hostExpr);
            return extendExpr;
        }
    },
    TUPLE_DECLARE {
        @Override
        public void symbolize(SyntaxNode node) {
            ENTITY_DECLARE.symbolize(node);
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return ENTITY_DECLARE.typeInfer(node);
        }
    },
    FUNC_DECLARE {
        @Override
        public void symbolize(SyntaxNode node) {
            FunctionDeclareNode function = ObjectUtils.cast(node);
            function.ast()
                    .symbolTable()
                    .getScope(function.parentScope())
                    .addFunction(function); // create function symbol, but arg type and return type is not confirmed
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            FunctionDeclareNode function = ObjectUtils.cast(node);
            TypeExpr expr = function.functionName().typeExpr();
            if (!(expr instanceof FunctionTypeExpr)) {
                return TypeExprFactory.createUnknown();
            }
            FunctionTypeExpr funcExpr = ObjectUtils.cast(expr);
            funcExpr.setArgumentType(function.argument().typeExpr());
            if (function.body().returns().size() == 0) {
                funcExpr.setReturnType(TypeExprFactory.createUnit());
            } else {
                funcExpr.setReturnType(function.body().returns().get(0).typeExpr());
            }
            return funcExpr;
        }
    },
    ARRAY_DECLARE {
        @Override
        public void symbolize(SyntaxNode node) {
            ArrayDeclareNode array = ObjectUtils.cast(node);
            array.ast().symbolTable().getScope(array.scope()).addArray(array);
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            ArrayDeclareNode array = ObjectUtils.cast(node);
            ArrayTypeExpr expr = ObjectUtils.cast(array.declaredName().typeExpr());
            SymbolEntry meta = node.ast().symbolTable().getSymbol(Terminal.ARRAY_TYPE.text(), Constants.ROOT_SCOPE);
            if (meta != null) {
                (ObjectUtils.<ArrayTypeExpr>cast(meta.typeExpr())).clearProjection();
                TypeExpr itemExpr = (ObjectUtils.<ArrayTypeExpr>cast(meta.typeExpr())).itemTypeExpr();
                expr.syncItemTypeExpr(itemExpr);
                expr.members().putAll(meta.typeExpr().members());
            }
            for (SyntaxNode item : array.items()) {
                expr.setItemTypeExpr(item.typeExpr());
            }
            return expr;
        }
    },
    MAP_DECLARE {
        @Override
        public void symbolize(SyntaxNode node) {
            MapDeclareNode map = ObjectUtils.cast(node);
            map.ast().symbolTable().getScope(map.scope()).addMap(map);
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            MapDeclareNode map = ObjectUtils.cast(node);
            // like array
            return map.declaredName().typeExpr();
        }
    },
    RETURN_STATEMENT {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return node.child(0).typeExpr();
        }
    },
    FUNC_CALL {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            FunctionCallNode function = ObjectUtils.cast(node);

            TypeExpr expr = function.functionName().typeExpr(); // 注释：.exactBe();
            // node是一个外部函数
            if (expr instanceof DoubleFunctionTypeExpr) {
                return inferDoubleFunction(function, ObjectUtils.cast(expr));
            }
            // node是一个实体函数
            if (expr instanceof FunctionTypeExpr) {
                return inferFunction(function, ObjectUtils.cast(expr));
            }
            // node是一个函数的传入参数，那么node should be一个函数
            if (expr instanceof GenericTypeExpr) {
                return inferGeneric(function, ObjectUtils.cast(expr));
            }
            node.panic(SyntaxError.FUNCTION_NOT_DEFINED, "the expected type of " + node.lexeme() + " is a function");
            return TypeExprFactory.createUnknown();
        }

        private TypeExpr inferDoubleFunction(FunctionCallNode function, DoubleFunctionTypeExpr expr) {
            FunctionTypeExpr real = expr.project(function);
            if (!function.child(0).typeExpr().exactBe().is(real.argumentType().exactBe())) {
                function.child(0)
                        .panic(SyntaxError.TYPE_MISMATCH,
                                "argument type is " + function.child(0).typeExpr().exactBe() + ", the expected type is "
                                        + real.argumentType().exactBe());
            }
            return real.returnType();
        }

        private TypeExpr inferGeneric(FunctionCallNode function, GenericTypeExpr expr) {
            GenericTypeExpr genericExpr = expr;
            for (int i = 0; i < function.childCount() - 1; i++) {
                // 这是个lambda函数，此时传入函数还没有，需要造一个generic的函数代表要传入函数的type expression
                GenericFunctionTypeExpr shouldBe = TypeExprFactory.createGenericFunction(function, function.child(i));
                // expr是函数参数，设置这个参数应该是一个lambda
                genericExpr.addShouldBe(shouldBe);
                // 新建的lambda参数也为generic
                GenericTypeExpr argOrigin = ObjectUtils.cast(shouldBe.argumentType());
                // 此时是反向依赖，此时的输入类型就是该参数必须支持的类型,所以将本次调用输入的参数设置为arg_origin的supposed type expression
                TypeExpr argIn = function.child(i).typeExpr().exactBe();
                argOrigin.addSupposedToBe(argIn);
                genericExpr = ObjectUtils.cast(shouldBe.returnType());
            }
            return genericExpr;
        }

        private TypeExpr inferFunction(FunctionCallNode function, FunctionTypeExpr funcExpr) {
            TypeExpr expr = funcExpr;
            funcExpr.clearProjection();
            for (int i = 0; i < function.childCount() - 1; i++) {
                if (!(expr instanceof FunctionTypeExpr)) {
                    function.panic(SyntaxError.ARGUMENT_NOT_EXIST,
                            "argument " + function.child(i).lexeme() + " is not expected");
                    break;
                }
                TypeExpr argOrigin = ((FunctionTypeExpr) expr).argumentType();
                TypeExpr argProjection = function.child(i).typeExpr(); // 注释：.exactBe();
                if (argProjection instanceof Projectable) {
                    ((Projectable) argProjection).clearProjection();
                }
                try {
                    if (argProjection instanceof AbstractTypeExpr) {
                        argProjection.invalidate();
                    }
                    expr = ((FunctionTypeExpr) expr).project(argOrigin, argProjection);
                } catch (SyntaxException e) {
                    function.child(i).panic(e.syntaxError(), e.getMessage());
                }
                expr = ((FunctionTypeExpr) expr).returnType(); // 注释：.exactBe();
            }
            funcExpr.clearProjection();
            return expr;
        }
    },
    ASYNC_BLOCK {
        @Override
        public void symbolize(SyntaxNode node) {
            long rootScope = 0L;
            if (node.ast().symbolTable().getSymbol(Constants.ASYNC, rootScope) == null) {
                SymbolScope scope = node.ast().symbolTable().getScope(rootScope);
                EntityDeclareNode fake = new EntityDeclareNode() {
                    @Override
                    public TerminalNode declaredName() {
                        return TerminalNode.mockId(Constants.ASYNC);
                    }
                };
                scope.addEntity(fake);
            }
        }

        private void createPromise(SymbolScope scope, EntityDeclareNode fake) {
            IdentifierEntry await = scope.addIdentifier(TerminalNode.mockId(".await"), true, fake);
            await.setTypeExpr(TypeExprFactory.createFunction());
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return node.ast().symbolTable().getSymbol(Constants.ASYNC, 0).typeExpr();
        }
    },
    ENTITY_CALL {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            try {
                EntityCallNode call = ObjectUtils.cast(node);
                if (call.member().isPrivate() && !StringUtils.equals(call.entity().lexeme(), Constants.THIS)
                        && !StringUtils.equals(call.entity().lexeme(), Constants.BASE)) {
                    call.member().panic(SyntaxError.ENTITY_MEMBER_ACCESS_DENIED);
                }
                TypeExpr hostExpr = call.entity().typeExpr();
                if (hostExpr == null) {
                    node.panic(SyntaxError.ENTITY_NOT_FOUND);
                    return TypeExprFactory.createUnknown();
                }
                return this.inferByHost(hostExpr, call);
            } catch (Exception e) {
                return TypeExprFactory.createUnknown();
            }
        }

        private TypeExpr inferByHost(TypeExpr hostExpr, EntityCallNode call) {
            TypeExpr expr = hostExpr.allMembers().get(call.member().lexeme());
            // if expr is not found, make sure the member doesn't exist or not inferred yet
            if (expr != null) {
                return expr;
            }
            if (!(hostExpr.node() instanceof EntityDeclareNode)) {
                call.member().panic(SyntaxError.SYSTEM_MEMBER_NOT_FOUND, call.member().lexeme() + " is not found ");
            } else {
                EntityDeclareNode entity = (EntityDeclareNode) hostExpr.node();
                Optional<SyntaxNode> member = entity.members()
                        .stream()
                        .filter(m -> m.child(0).lexeme().equals(call.member().lexeme()))
                        .map(m -> m.child(0))
                        .findFirst();
                if (!member.isPresent()) {
                    call.member().panic(SyntaxError.ENTITY_MEMBER_NOT_DEFINED);
                }
            }
            expr = TypeExprFactory.createUnknown();
            return expr;
        }
    },
    ARRAY_ACCESS {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            ArrayAccessNode arrAccessor = ObjectUtils.cast(node);
            TypeExpr expr = arrAccessor.array().typeExpr(); // 注释：.exactBe();
            // 找到了实体数组
            if (expr instanceof ArrayTypeExpr) {
                return ((ArrayTypeExpr) expr).itemTypeExpr();
            }
            // 本次调用是函数传入参数，也就是参数should be数组
            if (expr instanceof GenericTypeExpr) {
                return inferGeneric(arrAccessor, (GenericTypeExpr) expr);
            }
            node.panic(SyntaxError.TYPE_MISMATCH, "node type is " + expr + ", the expected type is array");
            return expr;
        }

        private TypeExpr inferGeneric(ArrayAccessNode accessor, GenericTypeExpr expr) {
            // 建一个虚拟lambda数组
            ArrayTypeExpr shouldBe = TypeExprFactory.createArray(accessor);
            // 将函数参数设置为 should be这个虚拟lambda数组
            expr.addShouldBe(shouldBe);
            return shouldBe.itemTypeExpr();
        }
    },
    RELATIONAL_CONDITION {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TypeExprFactory.createBool(node);
        }
    },
    NEGATION {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            TypeExpr expr = node.child(1).typeExpr().exactBe();
            if (expr instanceof AbstractTypeExpr) {
                ((AbstractTypeExpr) expr).addSupposedToBe(TypeExprFactory.createBool(node));
            } else {
                if (!(expr instanceof BoolTypeExpr || expr instanceof IgnoreTypeExpr)) {
                    node.child(1).panic(SyntaxError.TYPE_MISMATCH, "node type should be boolean");
                }
            }
            return TypeExprFactory.createBool(node);
        }
    },
    NUMERIC_EXPRESSION {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            boolean isAllowStr = node.children().stream().noneMatch(this::isAllow);
            NumberTypeExpr num = TypeExprFactory.createNumber(node);
            StringTypeExpr str = TypeExprFactory.createString(node);
            Set<TypeExpr> possible = new HashSet<>();
            for (SyntaxNode child : node.children()) {
                this.checkChild(child, num, possible, isAllowStr, str);
            }
            if (possible.size() > 1) {
                ExprTypeExpr expr = new ExprTypeExpr(node);
                possible.forEach(expr::addSupposedToBe);
                return expr;
            }
            return possible.stream().findFirst().get();
        }

        private void checkChild(SyntaxNode child, NumberTypeExpr num, Set<TypeExpr> possible, boolean isAllowStr,
                StringTypeExpr str) {
            if (child.typeExpr() == null) {
                return;
            }
            TypeExpr expr = child.typeExpr().exactBe();
            if (expr instanceof AbstractTypeExpr) {
                ((AbstractTypeExpr) expr).addSupposedToBe(num);
                possible.add(num);
                if (isAllowStr) {
                    ((AbstractTypeExpr) expr).addSupposedToBe(str);
                    possible.add(str);
                }
            } else {
                if (expr instanceof StringTypeExpr) {
                    if (isAllowStr) {
                        possible.clear();
                        possible.add(str);
                    } else {
                        child.panic(SyntaxError.TYPE_MISMATCH, "node type should be number");
                    }
                } else if (expr instanceof NumberTypeExpr) {
                    if (possible.isEmpty()) {
                        possible.add(num);
                    }
                } else {
                    child.panic(SyntaxError.TYPE_MISMATCH, "node type should be string or number");
                }
            }
        }

        private boolean isAllow(SyntaxNode c) {
            return c.nodeType() == Terminal.MINUS || c.nodeType() == Terminal.STAR || c.nodeType() == Terminal.SLASH
                    || c.nodeType() == Terminal.MINUS_EQUAL || c.nodeType() == Terminal.STAR_EQUAL
                    || c.nodeType() == Terminal.SLASH_EQUAL;
        }
    },
    TERM_EXPRESSION {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            for (SyntaxNode child : node.children()) {
                TypeExpr type = child.typeExpr();
                if (type == null) {
                    continue;
                }
                TypeExpr expr = type.exactBe();
                if (expr instanceof AbstractTypeExpr) {
                    ((AbstractTypeExpr) expr).addSupposedToBe(TypeExprFactory.createNumber(child));
                } else {
                    if (!(expr instanceof NumberTypeExpr || expr instanceof IgnoreTypeExpr)) {
                        child.panic(SyntaxError.TYPE_MISMATCH, "node type should be number");
                    }
                }
            }
            return TypeExprFactory.createNumber(node);
        }
    },
    UNARY_EXPRESSION {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TERM_EXPRESSION.typeInfer(node);
        }
    },
    EACH_STATEMENT {
        @Override
        public void symbolize(SyntaxNode node) {
            EachNode forNode = ObjectUtils.cast(node);
            node.ast().symbolTable().getScope(forNode.scope()).addIdentifier(forNode.item(), true, null);
            node.ast()
                    .symbolTable()
                    .getScope(forNode.scope())
                    .addIdentifier(forNode.index(), true, null)
                    .setTypeExpr(TypeExprFactory.createNumber(forNode.index()));
        }

        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            EachNode eachNode = ObjectUtils.cast(node);
            if (!(eachNode.array().typeExpr() instanceof ArrayTypeExpr)) {
                eachNode.array()
                        .panic(SyntaxError.TYPE_MISMATCH,
                                eachNode.array().lexeme() + " type is " + eachNode.array().typeExpr()
                                        + ", the expected type should be array");
            }
            IdentifierEntry item = ObjectUtils.cast(
                    node.ast().symbolTable().getSymbol(eachNode.item().lexeme(), node.scope()));
            item.setTypeExpr(((ArrayTypeExpr) eachNode.array().typeExpr()).itemTypeExpr());
            return TypeExprFactory.createIgnore();
        }
    },
    ID {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            SymbolEntry symbolEntry = (ObjectUtils.<TerminalNode>cast(node)).symbolEntry();
            return symbolEntry.typeExpr();
        }
    },
    NUMBER {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TypeExprFactory.createNumber(node);
        }
    },
    TRUE {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TypeExprFactory.createBool(node);
        }
    },
    FALSE {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TRUE.typeInfer(node);
        }
    },
    STRING {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TypeExprFactory.createString(node);
        }
    },
    UNIT {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TypeExprFactory.createUnit();
        }
    },
    SYS_METHOD {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return SystemMethodInfer.infer(node);
        }
    },
    EXTERNAL_DATA {
        @Override
        public TypeExpr typeInfer(SyntaxNode node) {
            return TypeExprFactory.createExternal(node);
        }
    };

    /**
     * 尝试对给定的语法节点进行类型推断。如果节点的类型已经被忽略，那么将返回一个IgnoreTypeExpr。
     * 如果节点的类型是AbstractTypeExpr，那么将清除其所有的投影。
     * 最后，根据节点的名称，调用相应的类型推断方法。
     *
     * @param node 需要进行类型推断的语法节点
     * @return 推断出的类型表达式
     */
    public static TypeExpr tryTypeInfer(SyntaxNode node) {
        try {
            if (node.typeInferIgnored()) {
                return TypeExprFactory.createIgnore();
            }
            TypeExpr expr = node.typeExpr();
            if (expr instanceof AbstractTypeExpr) {
                ((AbstractTypeExpr) expr).clearProjection();
            }
            return valueOf(node.name()).typeInfer(node);
        } catch (Exception ex) {
            return TypeExprFactory.createUnknown();
        }
    }

    /**
     * 用于符号解析过程，即将程序中的标识符（如变量名、函数名等）与其定义进行关联。
     * 这个过程涉及符号表的管理和查找，以确保在使用标识符时，它们已经被正确声明
     *
     * @param node 待符号化的节点
     */
    public static void trySymbolize(SyntaxNode node) {
        try {
            SemanticAnalyzer analyzer = valueOf(node.nodeType().name());
            analyzer.symbolize(node);
        } catch (IllegalArgumentException ex) {
            // ignore
        }
    }

    private static void entitySymbolize(SyntaxNode node, Function<SymbolScope, SymbolEntry> symbolCreator) {
        EntityDeclareNode me = ObjectUtils.cast(node);
        SymbolEntry entry = me.declaredName().symbolEntry();
        if (!(entry instanceof UnknownSymbolEntry)) {
            return;
        }
        // add entity symbol in parent symbol table, the entity is accessible in parent scope
        entry = symbolCreator.apply(me.ast().symbolTable().getScope(me.parentScope()));
        if (entry instanceof UnknownSymbolEntry) {
            return;
        }
        // add member symbol in entity symbol table
        // the symbol is a placeholder with dummy type expression
        // at this moment, the exact type expression is unknown since the children haven't been symbolized
        // it will be realized after children symbolized
        for (InitialAssignmentNode member : me.members()) {
            TerminalNode id = ObjectUtils.cast(member.child(0));
            id.ast().symbolTable().getScope(member.scope()).addIdentifier(id, true, me);
        }
    }

    /**
     * 对给定的语法节点进行符号化处理。
     * 这个过程涉及符号表的管理和查找，以确保在使用标识符时，它们已经被正确声明。
     * 在这个方法中，我们通常会将标识符添加到符号表中，并为其分配一个类型表达式。
     *
     * @param node 待符号化的节点
     */
    public void symbolize(SyntaxNode node) {
    }

    /**
     * 类型推断接口，用于在语义分析阶段确定节点的类型。
     * 在这个过程中，我们会根据节点的类型和子节点的类型来推断出该节点的类型。
     * 例如，对于一个数值表达式，我们会检查其所有子节点，并确保它们都是数值类型。
     * 如果有任何一个子节点不是数值类型，我们就会抛出一个类型错误。
     *
     * @param node 待推断类型的节点
     * @return 返回节点的类型表达式
     */
    public TypeExpr typeInfer(SyntaxNode node) {
        return TypeExprFactory.createIgnore();
    }

    /**
     * 从给定的节点中获取所有的标识符节点。
     * 如果给定的节点是一个终端节点，那么直接将其添加到结果列表中。
     * 如果给定的节点是一个元组解包节点，那么将其中的所有标识符节点都添加到结果列表中。
     *
     * @param node 需要获取标识符的节点
     * @return 返回包含所有标识符节点的列表
     */
    protected List<TerminalNode> fetchIds(SyntaxNode node) {
        List<TerminalNode> nodes = new ArrayList<>();
        if (node instanceof TerminalNode) {
            nodes.add((TerminalNode) node);
        }
        if (node instanceof TupleUnPackerNode) {
            nodes.addAll(((TupleUnPackerNode) node).all());
        }
        return nodes;
    }
}
