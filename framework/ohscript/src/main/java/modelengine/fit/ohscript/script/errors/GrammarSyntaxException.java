/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.errors;

/**
 * This class is used to throw an exception when the grammar syntax is incorrect.
 *
 * @since 1.0
 */
public class GrammarSyntaxException extends IllegalArgumentException {
    public GrammarSyntaxException(String info) {
        super(info);
    }
}
