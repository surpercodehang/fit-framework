/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.tlv;

/**
 * 表示 {@link modelengine.fitframework.serialization.TagLengthValues} 的标签常量值。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
public class Tags extends TagValuesChecker {
    /** 表示进程唯一标识的标签值。 */
    private static final int WORKER_ID_TAG = 0x00;

    /** 表示进程实例唯一标识的标签值。 */
    private static final int WORKER_INSTANCE_ID_TAG = 0x01;

    /** 表示异常属性的标签值。 */
    private static final int EXCEPTION_PROPERTIES_TAG = 0x10;

    static {
        // 校验标签值，确保所有标签值不冲突。
        validate(Tags.class);
    }

    /**
     * 获取进程唯一标识的标签值。
     *
     * @return 表示进程唯一标识的标签值 {@code int}。
     */
    public static int getWorkerIdTag() {
        return WORKER_ID_TAG;
    }

    /**
     * 获取进程实例唯一标识的标签值。
     *
     * @return 表示进程实例唯一标识的标签值的 {@code int}。
     */
    public static int getWorkerInstanceIdTag() {
        return WORKER_INSTANCE_ID_TAG;
    }

    /**
     * 获取异常属性的标签值。
     *
     * @return 表示异常属性的标签值的 {@code int}。
     */
    public static int getExceptionPropertiesTag() {
        return EXCEPTION_PROPERTIES_TAG;
    }
}
