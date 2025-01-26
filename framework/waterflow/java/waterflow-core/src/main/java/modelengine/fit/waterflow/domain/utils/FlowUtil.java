/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.utils;

import modelengine.fit.waterflow.domain.common.Constants;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流程定义中变量处理工具类
 *
 * @author 高诗意
 * @since 1.0
 */
public final class FlowUtil {
    private static final Logger LOG = Logger.get(FlowUtil.class);

    private static final Pattern LEGACY_RULE_KEY_PATTERN = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");

    private static final Pattern LEGACY_RULE_KEY_PATTERN_WITH_QUOTES = Pattern.compile("'\\{\\{([^\\}]+)\\}\\}'");

    /**
     * 获取流程引擎变量原始值
     * 输入{{var}}，返回var
     *
     * @param variable 流程引擎变量
     * @return 变量原始值列表
     */
    public static List<String> originalVariable(String variable) {
        if (StringUtils.isEmpty(variable)) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        Matcher matcher = LEGACY_RULE_KEY_PATTERN.matcher(variable);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    /**
     * 替换流程引擎变量的值
     * 输入{{var}}，返回var的真实值，从valueMap中获取
     *
     * @param variable 流程引擎变量
     * @param valueMap 流程引擎变量的值
     * @return 替换后的值
     */
    public static Object replace(String variable, Map<String, Object> valueMap) {
        LOG.debug("[FlowUtil] replace variables for variable: [{}]", variable);
        if (StringUtils.isEmpty(variable)) {
            return variable;
        }
        String replaced = variable;
        Matcher matcher = LEGACY_RULE_KEY_PATTERN.matcher(replaced);
        while (matcher.find()) {
            String formatVariable = matcher.group(0);
            String originalVariable = matcher.group(1);
            String value = ObjectUtils.cast(valueMap.getOrDefault(originalVariable, ""));
            replaced = replaced.replace(formatVariable, value);
        }
        return replaced;
    }

    /**
     * 格式化条件规则： 输入{{var}},返回 businessData.var; 输入'{{var}}' == '1', 返回businessData.var == "1"
     *
     * @param conditionRule 条件规则
     * @return 格式化后的条件规则
     */
    public static String formatConditionRule(String conditionRule) {
        LOG.debug("[FlowUtil] format condition rule: [{}]", conditionRule);
        if (StringUtils.isEmpty(conditionRule)) {
            return conditionRule;
        }
        String replacedConditionRule = conditionRule;
        Matcher matcherWithQuotes = LEGACY_RULE_KEY_PATTERN_WITH_QUOTES.matcher(replacedConditionRule);
        while (matcherWithQuotes.find()) {
            replacedConditionRule = replaceFormattedVariable(replacedConditionRule, matcherWithQuotes);
        }
        Matcher matcher = LEGACY_RULE_KEY_PATTERN.matcher(replacedConditionRule);
        while (matcher.find()) {
            replacedConditionRule = replaceFormattedVariable(replacedConditionRule, matcher);
        }
        replacedConditionRule = replacedConditionRule.replace("'", "\"");
        return replacedConditionRule;
    }

    private static String replaceFormattedVariable(String conditionRule, Matcher matcher) {
        String formatVariable = matcher.group(0);
        String originalVariable = matcher.group(1);
        if (!originalVariable.startsWith(Constants.BUSINESS_DATA_KEY)
                || !originalVariable.startsWith(Constants.PASS_DATA)) {
            originalVariable = Constants.BUSINESS_DATA_KEY + "." + originalVariable;
        }
        return conditionRule.replace(formatVariable, originalVariable);
    }

    /**
     * 合并两个map
     * 通key值如果不是map，则直接覆盖，如果是map则递归合并
     *
     * @param input 原始map
     * @param target 需要被合并的map
     * @return 合并后的map
     */
    public static Map<String, Object> mergeMaps(Map<String, Object> input, Map<String, Object> target) {
        LOG.info("[FlowUtil] mergeMaps for input: [{}], target: [{}]", input, target);
        Map<String, Object> mergedMap = new HashMap<>(input);
        target.forEach((targetKey, targetValue) -> {
            if (!mergedMap.containsKey(targetKey)) {
                mergedMap.put(targetKey, targetValue);
                return;
            }
            Object mergingValue = mergedMap.get(targetKey);
            if (!(mergingValue instanceof Map) || !(targetValue instanceof Map)) {
                mergedMap.put(targetKey, targetValue);
                return;
            }
            mergedMap.put(targetKey, mergeMaps((Map<String, Object>) mergingValue, (Map<String, Object>) targetValue));
        });
        return mergedMap;
    }
}
