/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.mybatis.util;

/**
 * 表示 ByteBuddy 类库的工具类。
 * <p>该工具类必须独立存放，因为该工具类的目的是判断类路径中是否存在 ByteBuddy，因此不能受其他方法及引用的干扰。</p>
 *
 * @author 季聿阶
 * @since 2024-08-02
 */
public class ByteBuddyHelper {
    private static final String BYTE_BUDDY_CLASSNAME = "net.bytebuddy.ByteBuddy";

    /**
     * 检查 ByteBuddy 是否可用。
     *
     * @return 如果 ByteBuddy 可用，则返回 {@code true}；否则返回 {@code false}。
     */
    public static boolean isByteBuddyAvailable() {
        try {
            Class.forName(BYTE_BUDDY_CLASSNAME, false, ByteBuddyHelper.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
