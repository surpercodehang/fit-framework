/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 为 {@link RangedResultSet} 提供单元测试。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public class RangedResultSetTest {
    /** 表示偏移量。 */
    private static final int OFFSET = 50;

    /** 表示限定长度。 */
    private static final int LIMIT = 3;

    /** 表示结果总数量。 */
    private static final int TOTAL = 100;

    /** 表示限定结果集。 */
    private static final List<Integer> RESULTS =
            ObjectUtils.mapIfNotNull(new Integer[] {OFFSET, LIMIT, TOTAL}, java.util.Arrays::asList);

    /**
     * 目标方法：{@link RangedResultSet#create(List, int, int, int)}
     * <p>返回的限定结果集符合预期。</p>
     */
    @Test
    public void should_return_ranged_result_set() {
        RangedResultSet<Integer> result = RangedResultSet.create(RESULTS, OFFSET, LIMIT, TOTAL);
        assertResult(result);
    }

    /**
     * 目标方法：{@link RangedResultSet#create(List, Range, int)}
     * <p>返回的限定结果集符合预期。</p>
     */
    @Test
    public void should_return_ranged_result_set_by_original_range() {
        Range range = Range.create(OFFSET, LIMIT);
        RangedResultSet<Integer> result = RangedResultSet.create(RESULTS, range, TOTAL);
        assertResult(result);
    }

    private static void assertResult(RangedResultSet<Integer> result) {
        assertNotNull(result);
        assertResults(result.getResults());
        assertRange(result.getRange());
    }

    private static void assertResults(List<Integer> results) {
        assertNotNull(results);
        assertEquals(results.size(), LIMIT);
        int index = 0;
        assertEquals(results.get(index++), OFFSET);
        assertEquals(results.get(index++), LIMIT);
        assertEquals(results.get(index), TOTAL);
    }

    private static void assertRange(RangeResult range) {
        assertNotNull(range);
        assertEquals(range.getOffset(), OFFSET);
        assertEquals(range.getLimit(), LIMIT);
        assertEquals(range.getTotal(), TOTAL);
    }
}
