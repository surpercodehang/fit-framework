/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.Condition;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;

/**
 * 为 {@link IfClassExist} 提供判定条件。
 *
 * @author 梁济时
 * @since 2023-05-18
 */
public class IfClassExistCondition implements Condition {
    @Override
    public boolean match(BeanContainer container, AnnotationMetadata annotations) {
        IfClassExist annotation = annotations.getAnnotation(IfClassExist.class);
        if (annotation == null) {
            return true;
        }
        String[] classNames = annotation.value();
        for (String className : classNames) {
            try {
                container.plugin().pluginClassLoader().loadClass(className);
            } catch (ClassNotFoundException ignored) {
                return false;
            }
        }
        return true;
    }
}
