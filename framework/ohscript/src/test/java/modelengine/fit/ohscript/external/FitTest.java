/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.Oh;
import modelengine.fit.ohscript.script.lexer.Lexer;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.GrammarBuilder;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.ohscript.util.OhScriptReader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于对外发送 FIT 请求的单元测试。
 *
 * @author 季聿阶
 * @since 2023-10-23
 */
@Disabled
@DisplayName("测试对外 FIT 调用")
public class FitTest {
    private ParserBuilder parserBuilder;

    @BeforeAll
    static void setupAll() {
        TestFitRuntime.INSTANCE.start();
    }

    @BeforeEach
    void setup() {
        GrammarBuilder grammarBuilder = new GrammarBuilder();
        Lexer lexer = new Lexer();
        this.parserBuilder = new ParserBuilder(grammarBuilder, lexer);
    }

    @AfterEach
    void teardown() {
        this.parserBuilder = null;
    }

    @Test
    @DisplayName("使用基本参数，调用 FIT，调用成功")
    void shouldReturnSuccessfullyWhenParamsArePrimitive() throws OhPanic {
        this.parserBuilder.addFitOh("method1", "m1", 5);
        String ohScript = OhScriptReader.read("fit/m1.oh");
        AST ast = this.parserBuilder.parseString("", ohScript);
        ASTEnv env = new ASTEnv(ast);
        env.setBrokerClient(TestFitRuntime.INSTANCE.getContainer(), TestFitRuntime.INSTANCE.getBrokerClient());
        String result = cast(env.execute());
        assertThat(result).isEqualTo("Hello:1:2:3.14");
    }

    @Test
    @DisplayName("无参数无返回值，调用 FIT，调用成功")
    void shouldReturnSuccessfullyWhenNoParamsNorReturnValue() throws OhPanic {
        this.parserBuilder.addFitOh("method2", "m2", 1);
        String ohScript = OhScriptReader.read("fit/m2.oh");
        AST ast = this.parserBuilder.parseString("", ohScript);
        ASTEnv env = new ASTEnv(ast);
        env.setBrokerClient(TestFitRuntime.INSTANCE.getContainer(), TestFitRuntime.INSTANCE.getBrokerClient());
        Object result = env.execute();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("使用映射列表参数，调用 FIT，调用成功")
    void shouldReturnSuccessfullyWhenParamsAreMapAndList() throws OhPanic {
        this.parserBuilder.addFitOh("method3", "m3", 3);
        String ohScript = OhScriptReader.read("fit/m3.oh");
        AST ast = this.parserBuilder.parseString("", ohScript);
        ASTEnv env = new ASTEnv(ast);
        env.setBrokerClient(TestFitRuntime.INSTANCE.getContainer(), TestFitRuntime.INSTANCE.getBrokerClient());
        Oh result = ObjectUtils.cast(env.execute());
        Map<String, Object> map = cast(result.get("map"));
        assertThat(map).containsEntry("k", "hello");
        List<String> list = cast(result.get("list"));
        assertThat(list).containsExactly("red", "yellow", "blue");
    }

    @Test
    @DisplayName("连续调用 FIT，调用成功")
    void shouldReturnSuccessfullyWhenInvokeFitOneByOne() throws OhPanic {
        this.parserBuilder.addFitOh("method", "m", 2);
        Map<String, Object> map = MapBuilder.<String, Object>get().put("k", 1).build();
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        this.parserBuilder.addExternalOh("context", list);
        String ohScript = OhScriptReader.read("fit/continuouslyInvoke.oh");
        AST ast = this.parserBuilder.parseString("", ohScript);
        ASTEnv env = new ASTEnv(ast);
        env.setBrokerClient(TestFitRuntime.INSTANCE.getContainer(), TestFitRuntime.INSTANCE.getBrokerClient());
        Oh listResult = ObjectUtils.cast(env.execute());
        assertEquals(1, listResult.size());
        Map<String, Object> mapResult = (Map<String, Object>) listResult.get(0);
        assertThat(mapResult).containsEntry("k", 1).containsEntry("k1", "v1").containsEntry("k2", "v2");
    }

    @Test
    @DisplayName("连续传参调用 FIT，调用成功")
    void shouldReturnSuccessfullyWhenInvokeFitByArgs() throws OhPanic {
        this.parserBuilder.addFitOh("method", "m", 2);
        Map<String, Object> map = MapBuilder.<String, Object>get().put("k", 1).build();
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        String ohScript = OhScriptReader.read("fit/invokeWithArgs.oh");
        AST ast = this.parserBuilder.parseString("", ohScript);
        ASTEnv env = new ASTEnv(ast);
        env.setBrokerClient(TestFitRuntime.INSTANCE.getContainer(), TestFitRuntime.INSTANCE.getBrokerClient());
        Oh listResult = ObjectUtils.cast(env.execute(list));
        assertEquals(1, listResult.size());
        Map<String, Object> mapResult = (Map<String, Object>) listResult.get(0);
        assertThat(mapResult).containsEntry("k", 1).containsEntry("k1", "v1").containsEntry("k2", "v2");
    }
}
