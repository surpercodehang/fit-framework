/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.assists;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * constraints just for generic type expression
 * add supposed to be which is predefined in function declaration
 * huizi 2023
 *
 * @since 1.0
 */
public class GenericConstraints extends Constraints {
    public GenericConstraints(SyntaxNode node) {
        super(node);
    }
}
