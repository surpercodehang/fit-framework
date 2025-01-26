/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;
import modelengine.fitframework.ioc.annotation.PreDestroy;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

/**
 * 校验入口类。
 * <p>
 * 当调用的类或方法参数包含 {@link Validated} 注解时，会对该方法进行校验处理。
 * </p>
 *
 * @author 易文渊
 * @since 2024-09-27
 */
@Aspect
@Component
public class ValidationHandler implements AutoCloseable {
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    ValidationHandler() {
        this.validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .failFast(false)
                .buildValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Before(value = "@target(validated) && execution(public * *(..))", argNames = "joinPoint, validated")
    private void handle(JoinPoint joinPoint, Validated validated) {
        ExecutableValidator execVal = this.validator.forExecutables();
        Set<ConstraintViolation<Object>> result = execVal.validateParameters(joinPoint.getTarget(),
                joinPoint.getMethod(),
                joinPoint.getArgs(),
                validated.value());
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        this.validatorFactory.close();
    }
}