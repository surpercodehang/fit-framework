/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.TestContext;
import modelengine.fitframework.util.IoUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 用于执行 SQL 脚本。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class SqlExecuteListener implements TestListener {
    private static final ClassLoader CLASS_LOADER = TestListener.class.getClassLoader();

    private Sql globalSql;

    @Override
    public void beforeTestClass(TestContext context) {
        Class<?> testClass = context.testClass();
        this.globalSql = testClass.getAnnotation(Sql.class);
    }

    @Override
    public void beforeTestMethod(TestContext context) {
        execSql(globalSql, context);
        execMethodSql(context);
    }

    private static void execMethodSql(TestContext context) {
        Method method = context.testMethod();
        Sql sql = method.getAnnotation(Sql.class);
        execSql(sql, context);
    }

    private static void execSql(Sql sql, TestContext context) {
        if (sql == null) {
            return;
        }
        DataSource dataSource = context.plugin().container().beans().get(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            for (String script : sql.scripts()) {
                connection.createStatement().execute(IoUtils.content(CLASS_LOADER, script));
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Fail to execute sql.", e);
        }
    }
}