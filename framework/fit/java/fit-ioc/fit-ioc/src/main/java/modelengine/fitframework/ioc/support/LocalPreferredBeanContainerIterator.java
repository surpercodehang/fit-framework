/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * 表示本地优先的 Bean 容器的迭代器。
 *
 * @author 梁济时
 * @since 2023-07-01
 */
class LocalPreferredBeanContainerIterator implements Iterator<BeanContainer> {
    private BeanContainer current;
    private Iterator<BeanContainer> iterator;
    private boolean hasNext;
    private BeanContainer next;

    LocalPreferredBeanContainerIterator(BeanContainer container) {
        this.current = container;
        this.iterator = new Bfs(this.current);
        this.hasNext = true;
        this.next = this.iterator.next();
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public BeanContainer next() {
        if (this.hasNext) {
            BeanContainer beanContainer = this.next;
            this.moveNext();
            return beanContainer;
        } else {
            throw new NoSuchElementException(StringUtils.format("No more bean containers to iterate."));
        }
    }

    private void moveNext() {
        if (this.iterator.hasNext()) {
            this.hasNext = true;
            this.next = this.iterator.next();
        } else if (this.current.plugin().parent() == null) {
            this.hasNext = false;
            this.next = null;
        } else {
            BeanContainer exclusion = this.current;
            this.current = this.current.plugin().parent().container();
            this.iterator = new Exclusion(this.current, exclusion);
            this.hasNext = true;
            this.next = this.iterator.next();
        }
    }

    private static class Bfs implements Iterator<BeanContainer> {
        private final Queue<BeanContainer> containers;

        Bfs(BeanContainer container) {
            this.containers = new LinkedList<>();
            this.containers.add(container);
        }

        @Override
        public boolean hasNext() {
            return !this.containers.isEmpty();
        }

        @Override
        public BeanContainer next() {
            BeanContainer container = this.containers.poll();
            if (container == null) {
                throw new NoSuchElementException("No more containers to iterate.");
            } else {
                for (Plugin childPlugin : container.plugin().children()) {
                    this.containers.add(childPlugin.container());
                }
            }
            return container;
        }
    }

    private static class Exclusion implements Iterator<BeanContainer> {
        private final List<Iterator<BeanContainer>> iterators;
        private int index;
        private Iterator<BeanContainer> current;

        private Exclusion(BeanContainer current, BeanContainer exclusion) {
            this.iterators = new LinkedList<>();
            for (Plugin childPlugin : current.plugin().children()) {
                BeanContainer container = childPlugin.container();
                if (container == exclusion) {
                    continue;
                }
                this.iterators.add(new Bfs(container));
            }
            this.index = 0;
            this.current = Collections.singleton(current).iterator();
        }

        @Override
        public boolean hasNext() {
            while (!this.current.hasNext() && this.index < this.iterators.size()) {
                this.current = this.iterators.get(this.index);
                this.index++;
            }
            return this.current.hasNext();
        }

        @Override
        public BeanContainer next() {
            return this.current.next();
        }
    }
}
