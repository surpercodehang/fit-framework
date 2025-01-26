/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

/**
 * 表示可降级的异常，所有需要降级的情况都需要抛出该异常或者该异常的子类。
 *
 * @author 季聿阶
 * @since 2021-05-15
 */
@ErrorCode(DegradableException.CODE)
public class DegradableException extends FitException {
    /** 表示降级异常的根异常码。 */
    public static final int CODE = 0x7F000001;

    /** 表示降级信息的键。 */
    private static final String DEGRADATION_KEY = "degradation";

    /**
     * 通过异常信息来实例化 {@link DegradableException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public DegradableException(String message) {
        super(message);
    }

    /**
     * 通过异常码、异常原因来实例化 {@link DegradableException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public DegradableException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常码、异常信息和异常原因来实例化 {@link DegradableException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public DegradableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 获取降级的条件信息。
     * <p>默认为 {@code null}，如果不为 {@code null}，则可以根据条件信息有选择的进行降级。</p>
     *
     * @return 表示降级的条件信息的 {@link String}。
     */
    public String degradationKey() {
        return this.getProperties().get(DEGRADATION_KEY);
    }

    /**
     * 设置降级的条件信息。
     *
     * @param key 表示待设置的降级的条件信息的 {@link String}。
     */
    public void degradationKey(String key) {
        this.setProperty(DEGRADATION_KEY, key);
    }
}
