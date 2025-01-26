/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.aop.proxy.FitProxy;

/**
 * FIT 生成的代理的工具类。
 *
 * @author 季聿阶
 * @since 2022-11-30
 */
public class FitProxyUtils {
    /**
     * 获取代理对象的实际类型。
     *
     * @param object 表示代理对象的 {@link Object}。
     * @return 表示代理对象的实际类型的 {@link Class}{@code <?>}
     */
    public static Class<?> getTargetClass(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof FitProxy) {
            FitProxy fitProxy = ObjectUtils.cast(object);
            return fitProxy.$fit$getActualClass();
        }
        return object.getClass();
    }
}
