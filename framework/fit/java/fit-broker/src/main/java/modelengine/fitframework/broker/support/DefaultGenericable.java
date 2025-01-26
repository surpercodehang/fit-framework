/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.broker.ConfigurableGenericable;
import modelengine.fitframework.broker.DynamicRouter;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.GenericableExecutor;
import modelengine.fitframework.broker.GenericableMethod;
import modelengine.fitframework.broker.GenericableType;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.Route;
import modelengine.fitframework.broker.Tags;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.UniqueGenericableId;
import modelengine.fitframework.broker.client.FitableNotFoundException;
import modelengine.fitframework.broker.client.TooManyFitablesException;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表示 {@link Genericable} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-10
 */
public class DefaultGenericable implements ConfigurableGenericable {
    private final DynamicRouter dynamicRouter;
    private final GenericableExecutor retryableExecutor;
    private final GenericableExecutor degradableExecutor;

    private final String id;
    private final String version;
    private String name;
    private GenericableType type;
    private final ConfigurableGenericableMethod method;
    private final ConfigurableRoute route;
    private final ConfigurableTags tags;
    private final Map<UniqueFitableId, Fitable> fitables = new ConcurrentHashMap<>();
    private final UniqueGenericableId uniqueId;

    DefaultGenericable(DynamicRouter dynamicRouter, String id, String version) {
        this.dynamicRouter = dynamicRouter;
        this.retryableExecutor = new RetryableGenericableExecutor(new UnicastGenericableExecutor());
        this.degradableExecutor = new DegradableGenericableExecutor(this.retryableExecutor);

        this.id = notBlank(id, "The genericable id cannot be blank.");
        this.version = notBlank(version, "The genericable version cannot be blank.");
        this.type = GenericableType.DEFAULT;
        this.method = new ConfigurableGenericableMethod();
        this.route = new ConfigurableRoute();
        this.tags = new ConfigurableTags();
        this.uniqueId = UniqueGenericableId.create(this.id, this.version);
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public GenericableType type() {
        return this.type;
    }

    @Override
    public GenericableMethod method() {
        return this.method;
    }

    @Override
    public Route route() {
        return this.route;
    }

    @Override
    public Tags tags() {
        return this.tags;
    }

    @Override
    public List<Fitable> fitables() {
        return Collections.unmodifiableList(new ArrayList<>(this.fitables.values()));
    }

    @Override
    public Fitable fitable(String fitableId, String fitableVersion) {
        return this.fitables()
                .stream()
                .filter(fitable -> Objects.equals(fitable.id(), fitableId) && Objects.equals(fitable.version(),
                        fitableVersion))
                .findFirst()
                .orElseThrow(() -> new FitableNotFoundException(StringUtils.format("No matched fitables. "
                                + "[genericableId={0}, genericableVersion={1}, fitableId={2}, fitableVersion={3}]",
                        this.id(),
                        this.version(),
                        fitableId,
                        fitableVersion)));
    }

    @Override
    public UniqueGenericableId toUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Object execute(InvocationContext context, Object[] args) {
        List<Fitable> routedFitables = this.dynamicRouter.route(this, context, args);
        if (CollectionUtils.isEmpty(routedFitables)) {
            FitableNotFoundException exception = new FitableNotFoundException(StringUtils.format(
                    "No matched fitables left after routing filter. "
                            + "[genericableId={0}, genericableVersion={1}, toFilterFitables={2}, filter={3}]",
                    this.id,
                    this.version,
                    this.fitables(),
                    context.routingFilter()));
            exception.associateGenericable(this.id);
            throw exception;
        }
        if (!context.isMulticast() && routedFitables.size() > 1) {
            List<String> fitableIds = routedFitables.stream().map(Fitable::id).collect(Collectors.toList());
            TooManyFitablesException exception = new TooManyFitablesException(StringUtils.format(
                    "More than 1 fitables left before unicast invoking. "
                            + "[genericableId={0}, genericableVersion={1}, fitableIds={2}]",
                    this.id,
                    this.version,
                    fitableIds));
            exception.associateGenericable(this.id);
            throw exception;
        }
        if (context.isMulticast()) {
            GenericableExecutor multicastExecutor = context.withDegradation()
                    ? new MulticastGenericableExecutor(this.degradableExecutor)
                    : new MulticastGenericableExecutor(this.retryableExecutor);
            return execute(multicastExecutor, this, routedFitables, context, args);
        }
        return context.withDegradation()
                ? execute(this.degradableExecutor, this, routedFitables, context, args)
                : execute(this.retryableExecutor, this, routedFitables, context, args);
    }

    private static Object execute(GenericableExecutor executor, Genericable genericable, List<Fitable> fitables,
            InvocationContext context, Object[] args) {
        try {
            return executor.execute(fitables, context, args);
        } catch (Throwable e) {
            throw FitException.wrap(e, genericable.id());
        }
    }

    @Override
    public ConfigurableGenericable name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ConfigurableGenericable type(GenericableType type) {
        this.type = type;
        return this;
    }

    @Override
    public ConfigurableGenericable method(Method method) {
        if (method != null) {
            this.method.method(method);
        }
        return this;
    }

    @Override
    public ConfigurableGenericable route(String defaultFitableId) {
        this.route.defaultFitable(defaultFitableId);
        return this;
    }

    @Override
    public ConfigurableGenericable tags(Set<String> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.set(tags);
        }
        return this;
    }

    @Override
    public ConfigurableGenericable appendTag(String tag) {
        this.tags.append(tag);
        return this;
    }

    @Override
    public ConfigurableGenericable removeTag(String tag) {
        this.tags.remove(tag);
        return this;
    }

    @Override
    public ConfigurableGenericable clearTags() {
        this.tags.clear();
        return this;
    }

    @Override
    public ConfigurableGenericable fitables(List<Fitable> fitables) {
        this.fitables.clear();
        if (fitables != null) {
            fitables.stream()
                    .filter(Objects::nonNull)
                    .forEach(fitable -> this.fitables.put(fitable.toUniqueId(), fitable));
        }
        return this;
    }

    @Override
    public ConfigurableGenericable appendFitable(Fitable fitable) {
        this.fitables.put(fitable.toUniqueId(), fitable);
        return this;
    }

    @Override
    public ConfigurableGenericable clearFitables() {
        this.fitables.clear();
        return this;
    }

    @Override
    public String toString() {
        return "{\"id\": \"" + this.id + '\"' + ", \"version\": \"" + this.version + '\"' + ", \"name\": \"" + this.name
                + '\"' + ", \"fitables\": " + this.fitables.values() + '}';
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        DefaultGenericable that = (DefaultGenericable) another;
        return Objects.equals(this.id, that.id) && Objects.equals(this.version, that.version)
                && Objects.equals(this.name, that.name) && Objects.equals(this.fitables, that.fitables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.version, this.name, this.fitables);
    }
}
