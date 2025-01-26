/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.mybatis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.transaction.DataAccessException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link MapperInvocationHandler} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2024-08-01
 */
@DisplayName("测试 MapperInvocationHandler")
public class MapperInvocationHandlerTest {
    @Test
    @DisplayName("生成代理，返回正确的调用结果")
    void shouldInvokeSuccessfully() {
        SqlSessionFactory sqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession session = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(eq(false))).thenReturn(session);
        Mapper actual = new MapperStub();
        when(session.getMapper(eq(Mapper.class))).thenReturn(actual);
        Mapper proxiedMapper = MapperInvocationHandler.proxy(sqlSessionFactory, Mapper.class, true);
        int count = proxiedMapper.count(10);
        assertThat(count).isEqualTo(0);
        count = proxiedMapper.count(10);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("当打开会话时出错，抛出数据访问异常")
    void shouldThrowDataAccessExceptionWhenOpenSessionFailed() {
        SqlSessionFactory sqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession session = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(eq(false))).thenReturn(session);
        when(session.getMapper(eq(Mapper.class))).thenThrow(new PersistenceException());
        Mapper proxiedMapper = MapperInvocationHandler.proxy(sqlSessionFactory, Mapper.class, true);
        DataAccessException exception = catchThrowableOfType(() -> proxiedMapper.count(0), DataAccessException.class);
        assertThat(exception).isNotNull();
    }

    @Test
    @DisplayName("当调用时出现数据访问错误，抛出数据访问异常")
    void shouldThrowDataAccessExceptionWhenInvokeDatabaseFailed() {
        SqlSessionFactory sqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession session = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(eq(false))).thenReturn(session);
        Mapper actual = new MapperStub();
        when(session.getMapper(eq(Mapper.class))).thenReturn(actual);
        Mapper proxiedMapper = MapperInvocationHandler.proxy(sqlSessionFactory, Mapper.class, true);
        DataAccessException exception = catchThrowableOfType(() -> proxiedMapper.count(-1), DataAccessException.class);
        assertThat(exception).isNotNull().getCause().isNotNull().hasMessage("DataAccessError");
    }

    @Test
    @DisplayName("当调用时出现其他错误，抛出其他异常")
    void shouldThrowExceptionWhenInvokeFailed() {
        SqlSessionFactory sqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession session = mock(SqlSession.class);
        when(sqlSessionFactory.openSession(eq(false))).thenReturn(session);
        Mapper actual = new MapperStub();
        when(session.getMapper(eq(Mapper.class))).thenReturn(actual);
        Mapper proxiedMapper = MapperInvocationHandler.proxy(sqlSessionFactory, Mapper.class, true);
        IllegalStateException exception =
                catchThrowableOfType(() -> proxiedMapper.count(-2), IllegalStateException.class);
        assertThat(exception).isNotNull().hasMessage("IllegalStateException");
    }

    /**
     * 表示测试的 Mapper 对象。
     */
    interface Mapper {
        /**
         * 获取计数值。
         *
         * @param param 表示参数的 {@code int}。
         * @return 表示计数值的 {@code int}。
         */
        int count(int param);
    }

    private static class MapperStub implements Mapper {
        private int count = 0;

        @Override
        public int count(int param) {
            if (param >= 0) {
                return this.count++;
            } else if (param == -1) {
                throw new PersistenceException("DataAccessError");
            } else {
                throw new IllegalStateException("IllegalStateException");
            }
        }
    }
}
