/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.data;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.constraints.Range;
import modelengine.fitframework.validation.group.StudentGroup;
import modelengine.fitframework.validation.group.TeacherGroup;

/**
 * 学生的校验类。
 *
 * @author 邬涨财
 * @since 2023-05-19
 */
@Validated(StudentGroup.class)
public class StudentValidate {
    public void validateStudent(
            @Range(min = 7, max = 20, message = "范围要在7~20之内", groups = {StudentGroup.class}) int age) {
    }

    public void validateTeacher(
            @Range(min = 30, max = 70, message = "范围要在30~70之内", groups = {TeacherGroup.class}) int age) {
    }
}
