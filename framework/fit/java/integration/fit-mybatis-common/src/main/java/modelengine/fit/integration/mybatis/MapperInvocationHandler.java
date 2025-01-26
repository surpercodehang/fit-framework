/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.mybatis;

import modelengine.fit.integration.mybatis.util.ByteBuddyHelper;
import modelengine.fit.integration.mybatis.util.InvocationHandlerHelper;
import modelengine.fitframework.transaction.DataAccessException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 为 {@code Mapper} 提供动态代理的执行处理程序。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-08-02
 */
public final class MapperInvocationHandler implements InvocationHandler {
    private final SqlSessionFactory sessionFactory;
    private final Class<?> mapperClass;

    MapperInvocationHandler(SqlSessionFactory sessionFactory, Class<?> mapperClass) {
        this.sessionFactory = sessionFactory;
        this.mapperClass = mapperClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session = null;
        try {
            session = this.sessionFactory.openSession(false);
            Object mapper = session.getMapper(this.mapperClass);
            Object result = method.invoke(mapper, args);
            session.commit();
            return result;
        } catch (PersistenceException e) {
            if (session != null) {
                session.rollback();
            }
            throw new DataAccessException(e);
        } catch (InvocationTargetException e) {
            session.rollback();
            Throwable cause = e.getCause();
            if (cause instanceof PersistenceException) {
                throw new DataAccessException(cause);
            } else {
                throw cause;
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    static <M> M proxy(SqlSessionFactory sessionFactory, Class<M> mapperClass, boolean shouldUseByteBuddy) {
        MapperInvocationHandler handler = new MapperInvocationHandler(sessionFactory, mapperClass);
        if (shouldUseByteBuddy && ByteBuddyHelper.isByteBuddyAvailable()) {
            // 通过 ByteBuddy 生成的代理为非 final，允许测试过程中进行 Mock/Spy 操作。
            return InvocationHandlerHelper.proxyByByteBuddy(mapperClass, handler);
        }
        return InvocationHandlerHelper.proxyByJdk(mapperClass, handler);
    }
}
