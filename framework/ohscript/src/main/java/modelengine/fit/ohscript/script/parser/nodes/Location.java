/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import java.io.Serializable;

/**
 * 记录node在源码中的位置
 *
 * @since 1.0
 */
public class Location implements Serializable {
    private static final long serialVersionUID = -8804467929479718489L;

    private final int startLine;

    private final int endLine;

    private final int startPosition;

    private final int endPosition;

    /**
     * 构造函数
     *
     * @param startLine 开始行
     * @param endLine 结束行
     * @param startPosition 开始位置
     * @param endPosition 结束位置
     */
    public Location(int startLine, int endLine, int startPosition, int endPosition) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * 获取开始行
     *
     * @return 开始行
     */
    public int startLine() {
        return this.startLine;
    }

    /**
     * 获取结束行
     *
     * @return 结束行
     */
    public int endLine() {
        return this.endLine;
    }

    /**
     * 获取开始位置
     *
     * @return 开始位置
     */
    public int startPosition() {
        return this.startPosition;
    }

    /**
     * 获取结束位置
     *
     * @return 结束位置
     */
    public int endPosition() {
        return this.endPosition;
    }
}
