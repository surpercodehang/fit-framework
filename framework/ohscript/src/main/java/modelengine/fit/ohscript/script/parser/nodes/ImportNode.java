/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * import节点
 *
 * @since 1.0
 */
public class ImportNode extends NonTerminalNode {
    private final List<Pair<TerminalNode, TerminalNode>> symbols = new ArrayList<>();

    private TerminalNode source;

    public ImportNode() {
        super(NonTerminal.IMPORT_DECLARE);
    }

    /**
     * 获取符号列表
     * first is imported ast variable
     * second is this ast variable
     *
     * @return 符号列表
     */
    public List<Pair<TerminalNode, TerminalNode>> symbols() {
        return this.symbols;
    }

    @Override
    public void optimizeGama() {
        this.source = ObjectUtils.cast(this.child(this.childCount() - 2));
        int index = 1;
        // import one by one
        while (index < this.childCount() - 3) {
            TerminalNode var = ObjectUtils.cast(this.child(index++));
            TerminalNode as = ObjectUtils.cast(this.child(index++));
            if (as.nodeType() == Terminal.AS) {
                this.symbols.add(new Pair<>(var, ObjectUtils.cast(this.child(index++))));
                index++;
            } else {
                this.symbols.add(new Pair<>(var, var));
            }
        }
    }

    /**
     * 获取源节点
     *
     * @return 源节点
     */
    public TerminalNode source() {
        return this.source;
    }

    /**
     * 获取命名空间节点
     *
     * @return 命名空间节点
     */
    public NamespaceNode namespace() {
        return null;
    }
}
