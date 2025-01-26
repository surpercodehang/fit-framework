/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.support;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.client.RequestContext;
import modelengine.fitframework.broker.CommunicationType;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link RequestContext} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public class DefaultRequestContext implements RequestContext {
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final CommunicationType communicationType;
    private final Map<String, String> extensions;

    public DefaultRequestContext(long timeout, TimeUnit timeoutUnit, CommunicationType communicationType,
            Map<String, String> extensions) {
        this.timeout = greaterThan(timeout, 0, "The timeout must be positive. [timeout={0}]", timeout);
        this.timeoutUnit = nullIf(timeoutUnit, TimeUnit.MILLISECONDS);
        this.communicationType = communicationType;
        this.extensions = getIfNull(extensions, Collections::emptyMap);
    }

    @Override
    public long timeout() {
        return this.timeout;
    }

    @Override
    public TimeUnit timeoutUnit() {
        return this.timeoutUnit;
    }

    @Override
    public CommunicationType communicationType() {
        return this.communicationType;
    }

    @Override
    public String timeoutValue() {
        return this.timeout() + " " + this.timeoutUnit().toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public Map<String, String> extensions() {
        return Collections.unmodifiableMap(this.extensions);
    }
}
