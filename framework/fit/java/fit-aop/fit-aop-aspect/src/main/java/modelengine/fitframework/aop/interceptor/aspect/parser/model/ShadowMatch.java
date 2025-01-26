/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.model;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析结果信息。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public class ShadowMatch {
    private final PointcutParameter[] parameters;
    private final Map<String, Integer> paramMapping;
    private final List<ExpressionParser.Result> bindingResults;

    public ShadowMatch(PointcutParameter[] parameters, Map<String, Integer> paramMapping,
            List<ExpressionParser.Result> bindingResults) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.paramMapping = nullIf(paramMapping, new HashMap<>());
        this.bindingResults = nullIf(bindingResults, new ArrayList<>());
    }

    /**
     * 获取 args(..) 括号内参数名对应目标对象方法参数的位置。
     * <ul>
     *     <li>输入：args(*, ageNum)，public void hello(String name, int age)，输出：Map的 key = ageNum，value = 1</></li>
     *     <li>输入：args(nameStr, ..)，public void hello(String name, int age)，输出：Map的 key = nameStr，value = 0</></li>
     * </ul>
     *
     * @return 表示方法名对应参数位置的 {@link Map}。
     */
    public Map<String, Integer> getArgsNameIndex() {
        return Collections.unmodifiableMap(this.paramMapping);
    }

    /**
     * 获取切入点参数数组。
     *
     * @return 表示切入点参数的 {@link PointcutParameter}{@code []}。
     */
    public PointcutParameter[] getPointcutParameters() {
        return this.parameters;
    }

    /**
     * 获取参数绑定的列表。
     *
     * @return 表示参数绑定的 {@link List<ExpressionParser.Result>}。
     */
    public List<ExpressionParser.Result> getBindingResults() {
        return Collections.unmodifiableList(this.bindingResults);
    }
}
