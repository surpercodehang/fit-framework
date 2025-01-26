/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.log.Loggers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link Logger} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2022-12-29
 */
@DisplayName("测试 Logger 工具类")
class ConsoleLoggerTest {
    private Logger logger;

    @BeforeEach
    void setup() {
        Config config = mock(Config.class);
        when(config.get("logging.level", String.class)).thenReturn("INFO");
        Loggers.initialize(config, ConsoleLoggerTest.class.getClassLoader());
        this.logger = Logger.get(ConsoleLoggerTest.class);
    }

    @AfterEach
    void teardown() {
        this.logger = null;
        Loggers.destroy();
    }

    @Test
    @DisplayName("判断日志级别是否为跟踪级别")
    void isTraceEnabled() {
        assertThat(this.logger).returns(false, Logger::isTraceEnabled);
    }

    @Test
    @DisplayName("判断日志级别是否为调试级别")
    void isDebugEnabled() {
        assertThat(this.logger).returns(false, Logger::isDebugEnabled);
    }

    @Test
    @DisplayName("判断日志级别是否为信息级别")
    void isInfoEnabled() {
        assertThat(this.logger).returns(true, Logger::isInfoEnabled);
    }

    @Test
    @DisplayName("判断日志级别是否为错误级别")
    void isErrorEnabled() {
        assertThat(this.logger).returns(true, Logger::isErrorEnabled);
    }

    @Nested
    @DisplayName("当把 'modelengine.fitframework.log' 包下的所有日志级别调整为错误级别之后")
    class AfterSetParentLevelsToError {
        @BeforeEach
        void setup() {
            Loggers.getFactory().setLevels("modelengine.fitframework.log", Logger.Level.ERROR);
        }

        @Test
        @DisplayName("判断日志级别是否为跟踪级别")
        void isTraceEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isTraceEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为调试级别")
        void isDebugEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isDebugEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为信息级别")
        void isInfoEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isInfoEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为错误级别")
        void isErrorEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(true, Logger::isErrorEnabled);
        }
    }

    @Nested
    @DisplayName("当把 'modelengine.fitframework.log.console' 包下的所有日志级别调整为错误级别之后")
    class AfterSetCurrentLevelsToError {
        @BeforeEach
        void setup() {
            Loggers.getFactory().setLevels("modelengine.fitframework.log.console", Logger.Level.ERROR);
        }

        @Test
        @DisplayName("判断日志级别是否为跟踪级别")
        void isTraceEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isTraceEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为调试级别")
        void isDebugEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isDebugEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为信息级别")
        void isInfoEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isInfoEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为错误级别")
        void isErrorEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(true, Logger::isErrorEnabled);
        }
    }

    @Nested
    @DisplayName("当把 'modelengine.fitframework.log.console.sample' 包下的所有日志级别调整为错误级别之后")
    class AfterSetChildLevelsToError {
        @BeforeEach
        void setup() {
            Loggers.getFactory().setLevels("modelengine.fitframework.log.console.sample", Logger.Level.ERROR);
        }

        @Test
        @DisplayName("判断日志级别是否为跟踪级别")
        void isTraceEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isTraceEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为调试级别")
        void isDebugEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isDebugEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为信息级别")
        void isInfoEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(true, Logger::isInfoEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为错误级别")
        void isErrorEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(true, Logger::isErrorEnabled);
        }
    }

    @Nested
    @DisplayName("当把全局日志级别调整为错误级别之后")
    class AfterSetGlobalLevelToError {
        @BeforeEach
        void setup() {
            Loggers.getFactory().setGlobalLevel(Logger.Level.ERROR);
        }

        @Test
        @DisplayName("判断日志级别是否为跟踪级别")
        void isTraceEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isTraceEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为调试级别")
        void isDebugEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isDebugEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为信息级别")
        void isInfoEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(false, Logger::isInfoEnabled);
        }

        @Test
        @DisplayName("判断日志级别是否为错误级别")
        void isErrorEnabled() {
            assertThat(ConsoleLoggerTest.this.logger).returns(true, Logger::isErrorEnabled);
        }
    }
}
