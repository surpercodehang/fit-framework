/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Locale;

import javax.validation.MessageInterpolator;

/**
 * 地区消息插值器。
 * <p>
 * 作为 Jakarta 消息插值器的代理类，提供地区设置能力。
 * </p>
 *
 * @author 阮睿
 * @since 2025-08-18
 */
public class LocaleMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator target;

    private Locale locale;

    /**
     * 构造函数，使用指定的目标消息插值器初始化实例。
     *
     * @param target 表示目标消息插值器的 {@link MessageInterpolator}。
     */
    public LocaleMessageInterpolator(MessageInterpolator target) {
        this.target = target;
        this.locale = Locale.getDefault();
    }

    /**
     * 构造函数，使用指定的地区初始化实例。
     *
     * @param locale 表示指定地区的 {@link Locale}。
     */
    public LocaleMessageInterpolator(Locale locale) {
        this.locale = locale;
        this.target = new ParameterMessageInterpolator();
    }

    /**
     * 构造函数，使用指定的目标消息插值器和地区初始化实例。
     *
     * @param target 表示被代理的目标消息插值器的 {@link MessageInterpolator}。
     * @param locale 表示当前消息插值器要使用语言的相关地区的 {@link Locale}。
     */
    public LocaleMessageInterpolator(MessageInterpolator target, Locale locale) {
        this.target = target;
        this.locale = locale;
    }

    /**
     * 构造函数，使用默认地区初始化实例。
     */
    public LocaleMessageInterpolator() {
        this.locale = Locale.getDefault();
        this.target = new ParameterMessageInterpolator();
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return this.target.interpolate(messageTemplate, context, this.locale);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return this.target.interpolate(messageTemplate, context, locale);
    }

    /**
     * 设置地区。
     *
     * @param locale 表示当前消息插值器要使用语言的相关地区的 {@link Locale}。
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
