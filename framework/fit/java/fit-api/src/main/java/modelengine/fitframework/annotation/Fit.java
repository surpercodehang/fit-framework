/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 用于调用 {@link Fitable 泛服务实现}。
 *
 * @author 张群辉
 * @author 季聿阶
 * @see Fitable
 * @since 2020-01-18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface Fit {
    /** 表示默认路由策略。 */
    String POLICY_DEFAULT = "default";

    /** 表示别名路由策略。 */
    String POLICY_ALIAS = "alias";

    /** 表示规则路由策略。 */
    String POLICY_RULE = "rule";

    /**
     * 表示调用的泛服务实现的别名。
     * <p>当为空字符串时表示不指定目标泛服务实现。</p>
     * <p><b>默认值为空字符串。</b></p>
     *
     * @return 表示调用的泛服务实现的别名的 {@link String}。
     */
    String alias() default "";

    /**
     * 表示调用的泛服务实现的策略。
     * <p><b>注意：当用户设置别名且非空后，该值会被忽略。</b></p>
     * <p>可选值范围如下：
     * <ul>
     *     <li>默认路由策略：{@code DEFAULT}，<b>该值为默认值</b>，即用户没有指定任何特定策略。
     *         <p>该策略会优先尝试寻找调用的泛服务的规则进行路由，没有规则的情况下再尝试默认路由，二者二选一。</p>
     *         <ol>
     *             <li>如果有规则配置，则会使用规则进行路由，如规则路由失败，则报错。</li>
     *             <li>否则，会使用默认路由，如无默认路由配置，则报错。</li>
     *         </ol>
     *         <p><b>注意：该策略生效的条件是用户没有指定别名，即别名必须是默认的空字符串。</b></p></p>
     *     </li>
     *     <li>别名路由策略：{@code ALIAS}。
     *         <p>该策略只会寻找用户指定的泛服务别名进行路由。</p>
     *     </li>
     *     <li>规则路由策略：{@code RULE}。
     *         <p>该策略只会寻找调用泛服务的规则进行路由。</p>
     *     </li>
     * </ul>
     * </p>
     *
     * @return 表示调用的泛服务实现的策略的 {@link String}。
     * @see #POLICY_DEFAULT
     * @see #POLICY_ALIAS
     * @see #POLICY_RULE
     */
    String policy() default POLICY_DEFAULT;

    /**
     * 表示调用的泛服务实现的重试次数。
     * <p>只有远程调用发生网络错误时，才会进行重试。</p>
     * <p><b>默认值为 {@code 0}。</b></p>
     *
     * @return 表示调用的泛服务实现的重试次数的 {@code int}。
     */
    int retry() default 0;

    /**
     * 表示调用的泛服务实现的超时时间，单位默认为毫秒，可使用 {@link Fit#timeunit()} 指定。
     * <p><b>默认值为 {@code 3000}。</b></p>
     *
     * @return 表示调用的泛服务实现的超时时间的 {@code int}。
     */
    int timeout() default 3000;

    /**
     * 表示调用的泛服务实现的超时时间的单位。
     * <p><b>默认值为 {@link TimeUnit#MILLISECONDS}。</b></p>
     *
     * @return 表示调用的泛服务实现的超时时间的单位的 {@link TimeUnit}。
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    /**
     * 表示指定的通讯协议。
     * <p>默认为 {@link CommunicationProtocol#UNKNOWN}，表示不指定。</p>
     *
     * @return 表示指定的通讯协议的 {@link CommunicationProtocol}。
     */
    CommunicationProtocol protocol() default CommunicationProtocol.UNKNOWN;

    /**
     * 表示指定的序列化方式。
     * <p>默认为 {@link SerializationFormat#UNKNOWN}，表示不指定。</p>
     *
     * @return 表示指定的序列化方式的 {@link SerializationFormat}。
     */
    SerializationFormat format() default SerializationFormat.UNKNOWN;
}
