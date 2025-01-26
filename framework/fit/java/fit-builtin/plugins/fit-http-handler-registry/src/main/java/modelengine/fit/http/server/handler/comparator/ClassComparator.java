/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.comparator;

import java.util.Comparator;

/**
 * 表示类比较器。
 * <p>比较器确保子类优先于父类在迭代器中输出。</p>
 *
 * @author 何嘉斌
 * @since 2024-10-23
 */
public class ClassComparator implements Comparator<Class<Throwable>> {
    /**
     * 表示比较器的单例。
     */
    public static final Comparator<Class<Throwable>> INSTANCE = new ClassComparator();

    private ClassComparator() {}

    @Override
    public int compare(Class<Throwable> class1, Class<Throwable> class2) {
        if (class1.equals(class2)) {
            return 0;
        }
        return class1.isAssignableFrom(class2)
                ? 1
                : class2.isAssignableFrom(class1) ? -1 : class1.getName().compareTo(class2.getName());
    }
}