/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.nodes.ScriptNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * oh的工具
 *
 * @since 1.0
 */
public class OhUtil implements Serializable {
    /**
     * 获取AST环境，线程变量，用于处理部分静态方法无法获得当前的astEnv情况
     */
    public static final ThreadLocal<ASTEnv> AST_ENV_THREAD_LOCAL = new ThreadLocal<>();

    private static final long serialVersionUID = 7995775932631312854L;

    private static final Logger LOG = Logger.get(OhUtil.class);

    private static final String RESET = "\033[0m";  // 重置

    private static final String RED = "\033[0;31m";    // 红色

    private static final String YELLOW = "\033[0;33m"; // 黄色

    /**
     * 创建一个新的映射
     *
     * @return 新的映射
     */
    public Map newMap() {
        return new HashMap();
    }

    /**
     * 创建一个新的列表
     *
     * @return 新的列表
     */
    public List newList() {
        return new ArrayList();
    }

    /**
     * 日志信息
     *
     * @param message 日志信息
     */
    public void log(String message) {
    }

    /**
     * 警告日志
     *
     * @param message 警告信息
     */
    public void warning(String message) {
        // 待对接日志
    }

    /**
     * 错误日志
     *
     * @param message 错误信息
     */
    public void error(String message) {
        // 待对接日志
    }

    /**
     * 抛出不可恢复错误
     *
     * @param code 错误码
     * @throws OhPanic 不可恢复异常
     */
    public void panic(Integer code) throws OhPanic {
        throw new OhPanic("ohscript exception with code: " + code, code);
    }

    /**
     * 记录不可恢复的错误
     *
     * @param type 日志类型
     * @param message 日志信息
     */
    public void logPanic(String type, String message) {
        if ("log".equals(type)) {
            LOG.info(message);
            return;
        }
        if ("warning".equals(type)) {
            LOG.warn(message);
            return;
        }
        LOG.error(message);
    }

    /**
     * 将字符串转换为JSON对象
     *
     * @param string 字符串
     * @return JSON对象
     */
    public JSONObject stringToJson(String string) {
        return JSON.parseObject(string);
    }

    /**
     * 将JSON对象转换为字符串
     *
     * @param json JSON对象
     * @return 字符串
     */
    public String jsonToString(JSONObject json) {
        return json.toString();
    }

    /**
     * 将JSON对象转换为实体对象
     *
     * @param json JSON对象
     * @return 实体对象
     */
    public Object jsonToEntity(JSONObject json) {
        Map<String, Object> entity = new HashMap<>();
        ScriptNode start = null;
        ActivationContext current = null;
        ASTEnv astEnv = AST_ENV_THREAD_LOCAL.get();
        if (astEnv != null) {
            start = ObjectUtils.cast(Optional.ofNullable(astEnv.ast()).map(AST::start).orElse(null));
            current = astEnv.asfEnv().context();
        }
        ActivationContext finalCurrent = current;
        ScriptNode finalStart = start;
        json.forEach((key, value) -> Optional.ofNullable(this.buildReturnValue(value, finalCurrent, finalStart))
                .ifPresent(var -> entity.put("." + key, var)));
        return entity;
    }

    private ReturnValue buildReturnValue(Object value, ActivationContext finalCurrent, ScriptNode finalStart) {
        ReturnValue returnValue = null;
        if (value instanceof JSONObject) {
            returnValue = new ReturnValue(finalCurrent, TypeExprFactory.createExternal(finalStart),
                    jsonToEntity((JSONObject) value));
        }
        if (value instanceof JSONArray) {
            List<Object> values = new ArrayList<>();
            ((JSONArray) value).forEach(obj -> values.add(this.buildReturnValue(obj, finalCurrent, finalStart)));
            returnValue = new ReturnValue(finalCurrent, TypeExprFactory.createArray(finalStart), values);
        }
        if (value instanceof String) {
            returnValue = new ReturnValue(finalCurrent, TypeExprFactory.createString(finalStart), value);
        }
        if (value instanceof Number) {
            returnValue = new ReturnValue(finalCurrent, TypeExprFactory.createNumber(finalStart), value);
        }
        if (value instanceof Boolean) {
            returnValue = new ReturnValue(finalCurrent, TypeExprFactory.createBool(finalStart), value);
        }
        return returnValue;
    }

    /**
     * 让当前线程睡眠一段时间
     *
     * @param millis 毫秒数
     * @throws InterruptedException 如果线程在等待、休眠、或者占用，且其他线程中断它，则抛出此异常
     */
    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
