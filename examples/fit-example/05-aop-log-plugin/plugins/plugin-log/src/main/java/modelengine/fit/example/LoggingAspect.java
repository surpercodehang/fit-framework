/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.example;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.log.Logger;

/**
 * 全局日志切面。
 */
@Aspect(scope = Scope.GLOBAL)
@Component
public class LoggingAspect {
    private static final Logger logger = Logger.get(LoggingAspect.class);

    @Around("@annotation(modelengine.fit.http.annotation.GetMapping)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取方法信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        // 记录方法开始日志
        logger.info("===> {}.{}() 开始执行", className, methodName);
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            // 记录方法结束日志
            logger.info("<=== {}.{}() 执行成功 | 耗时: {}ms", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("<=== {}.{}() 执行失败 | 耗时: {}ms | 异常: {}",
                    className,
                    methodName,
                    executionTime,
                    e.getMessage(),
                    e);
            throw e;
        }
    }
}
