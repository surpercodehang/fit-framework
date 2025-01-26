/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.inspection;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 可以为 {@code null} 标记的注解。
 *
 * @author 季聿阶
 * @since 2022-05-27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {}
