/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.validation.data.Car;
import modelengine.fitframework.validation.data.CarValidate;
import modelengine.fitframework.validation.data.Company;
import modelengine.fitframework.validation.data.NestedValidate;
import modelengine.fitframework.validation.data.Product;
import modelengine.fitframework.validation.data.ProductValidate;
import modelengine.fitframework.validation.data.StudentValidate;
import modelengine.fitframework.validation.exception.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link ValidationHandler} 的单元测试。
 *
 * @author 邬涨财
 * @since 2023-05-23
 */
public class ValidationHandlerTest {
    private final BeanContainer beanContainer = mock(BeanContainer.class);
    private final FitRuntime fitRuntime = mock(FitRuntime.class);
    private final AnnotationMetadataResolver annotationMetadataResolver = new DefaultAnnotationMetadataResolver();
    private final Validated validated = Mockito.mock(Validated.class);
    private final ValidationHandler handler = new ValidationHandler(beanContainer);

    @Nested
    @DisplayName("使用默认的分组进行校验")
    class UseDefaultGroupTest {
        @BeforeEach
        void setUp() {
            when(validated.value()).thenReturn(new Class[0]);
            when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
            when(beanContainer.runtime()).thenReturn(fitRuntime);
        }

        /**
         * 调用 {@link CarValidate#validate1(Car)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，该数据类的 Constraint 字段会被校验，其余字段不会被校验")
        public void givenFieldsWithConstraintAnnotationThenValidateHappened() {
            // when
            Method validateMethod = ReflectionUtils.getDeclaredMethod(CarValidate.class, "validate1", Car.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Car car = new Car(10, 10, "", "", -1, -1);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {car});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo(
                    "座位数量范围只能在0和6！, 发动机数量范围只能在0和2！, 品牌不能为空！, 型号不能为空！");
        }

        /**
         * 调用 {@link ProductValidate#validate1(Product)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，该数据类的 Constraint 字段会被校验，其余字段不会被校验")
        void givenFieldsWithConstraintAnnotationThenValidateHappened2() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(ProductValidate.class, "validate1", Product.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Product product = new Product("computer", -1.0, 100, " ");
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {product});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("产品价格必须为正, 产品类别不能为空");
        }

        /**
         * 调用 {@link ProductValidate#validate1(Product)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，该数据类的 Constraint 字段会被校验，其余字段不会被校验")
        void givenFieldsWithConstraintAnnotationThenValidateHappened3() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(ProductValidate.class, "validate1", Product.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Product product = new Product(null, 12999.0, null, "electronic devices");
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {product});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("产品名不能为空, 产品数量必须为正");
        }

        /**
         * 调用 {@link CarValidate#validate2(int, int)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验方法参数，该方法的 Constraint 参数会被校验，其余参数不会被校验")
        public void givenParametersWithConstraintAnnotationThenValidateHappened() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(CarValidate.class, "validate2", int.class, int.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {-1, -1});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("座位数量范围只能在0和6！");
        }
    }

    @Nested
    @DisplayName("使用自定义的分组进行校验")
    class UseCustomGroupTest {
        @BeforeEach
        void setUp() {
            when(validated.value()).thenReturn(new Class[0]);
            when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
            when(beanContainer.runtime()).thenReturn(fitRuntime);
        }

        /**
         * 调用 {@link CarValidate#validate3(Car)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，只会校验与数据类的分组一致的字段分组。")
        public void givenFieldsThenSameGroupValidateHappened() {
            // when
            Method validateMethod = ReflectionUtils.getDeclaredMethod(CarValidate.class, "validate3", Car.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Car car = new Car(10, 10, "", "", -1, -1);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {car});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("生产年份在2000-2030之内");
        }

        /**
         * 调用 {@link StudentValidate#validateStudent(int)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验方法参数，只会校验与数据类的分组一致的方法参数。")
        public void givenParametersThenSameGroupValidateHappened() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(StudentValidate.class, "validateStudent", int.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {-1});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("范围要在7~20之内");
        }

        /**
         * 调用 {@link StudentValidate#validateTeacher(int)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验方法参数，与数据类的分组不一致的方法参数不会校验。")
        public void givenParametersThenDifferentGroupValidateNotHappened() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(StudentValidate.class, "validateTeacher", int.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {-1});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            assertThat(invocationTargetException == null).isTrue();
        }
    }

    @Nested
    @DisplayName("测试嵌套校验")
    class NestedTest {
        private static final String INVALID_CAR1_MSG = "座位数量范围只能在0和6！, 品牌不能为空！";
        private static final String INVALID_CAR2_MSG = "型号不能为空！";
        private static final String INVALID_PRODUCT1_MSG = "产品名不能为空, 产品数量必须为正";
        private static final String INVALID_PRODUCT2_MSG = "产品类别不能为空";

        private Car validCar = new Car(5, 1, "brand", "model", 2024, 1999);
        private Car invalidCar1 = new Car(-1, 1, "", "model", 2024, 1999);
        private Car invalidCar2 = new Car(5, 1, "brand", "", 2024, 1999);
        private Product validProduct = new Product("name", 1.0, 1, "category");
        private Product invalidProduct1 = new Product("", 1.0, -1, "category");
        private Product invalidProduct2 = new Product("name", 1.0, 1, "");
        private Method handleValidatedMethod =
                ReflectionUtils.getDeclaredMethod(ValidationHandler.class, "handle", JoinPoint.class, Validated.class);
        private JoinPoint joinPoint = mock(JoinPoint.class);

        @BeforeEach
        void setUp() {
            when(validated.value()).thenReturn(new Class[0]);
            when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
            when(beanContainer.runtime()).thenReturn(fitRuntime);
            handleValidatedMethod.setAccessible(true);
        }

        @Test
        @DisplayName("测试嵌套校验类 Company")
        void shouldReturnMsgWhenValidateCompany() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test1", Company.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            Company company =
                    new Company(-1, 100, this.invalidProduct2, Arrays.asList(this.invalidCar1, this.invalidCar2));
            when(this.joinPoint.getArgs()).thenReturn(new Object[] {company});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList =
                    Arrays.asList("经理只能有0-1个！", INVALID_PRODUCT2_MSG, INVALID_CAR1_MSG, INVALID_CAR2_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated List<Car>")
        void shouldReturnMsgWhenValidateList() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test2", List.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            when(this.joinPoint.getArgs()).thenReturn(new Object[] {
                    Arrays.asList(this.validCar, this.invalidCar1, this.invalidCar2)
            });
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList = Arrays.asList(INVALID_CAR1_MSG, INVALID_CAR2_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated List<List<Car>>")
        void shouldReturnMsgWhenValidateListInList() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test3", List.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            List<Car> personList1 = Arrays.asList(this.validCar, this.invalidCar1, this.invalidCar2);
            List<Car> personList2 = Arrays.asList(this.validCar, this.invalidCar1);

            when(this.joinPoint.getArgs()).thenReturn(new Object[] {Arrays.asList(personList1, personList2)});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList = Arrays.asList(INVALID_CAR1_MSG, INVALID_CAR2_MSG, INVALID_CAR1_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated Map<String, Car>")
        void shouldReturnMsgWhenValidateMapSimple() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test4", Map.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            LinkedHashMap<String, Car> map = new LinkedHashMap<>();
            map.put("1", this.validCar);
            map.put("2", this.invalidCar1);
            map.put("3", this.invalidCar2);

            when(this.joinPoint.getArgs()).thenReturn(new Object[] {map});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList = Arrays.asList(INVALID_CAR1_MSG, INVALID_CAR2_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated Map<Car, Product>")
        void shouldReturnMsgWhenValidateMapObj() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test5", Map.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            LinkedHashMap<Car, Product> map = new LinkedHashMap<>();
            map.put(this.validCar, this.validProduct);
            map.put(this.invalidCar1, this.invalidProduct1);
            map.put(this.invalidCar2, this.invalidProduct2);

            when(this.joinPoint.getArgs()).thenReturn(new Object[] {map});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList =
                    Arrays.asList(INVALID_CAR1_MSG, INVALID_PRODUCT1_MSG, INVALID_CAR2_MSG, INVALID_PRODUCT2_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated Map<Car, Map<Car, Prodcut>>")
        void shouldReturnMsgWhenValidateMapInMap() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test6", Map.class);

            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            LinkedHashMap<Car, Product> map1 = new LinkedHashMap<>();
            map1.put(this.validCar, this.invalidProduct1);
            map1.put(this.invalidCar1, this.validProduct);
            map1.put(this.invalidCar2, this.invalidProduct2);
            LinkedHashMap<Car, Product> map2 = new LinkedHashMap<>();
            map2.put(this.invalidCar1, this.invalidProduct1);
            map2.put(this.validCar, this.validProduct);
            LinkedHashMap<Car, Map<Car, Product>> nestMap = new LinkedHashMap<>();
            nestMap.put(this.validCar, map1);
            nestMap.put(this.invalidCar1, map2);

            when(this.joinPoint.getArgs()).thenReturn(new Object[] {nestMap});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList = Arrays.asList(INVALID_PRODUCT1_MSG,
                    INVALID_CAR1_MSG,
                    INVALID_CAR2_MSG,
                    INVALID_PRODUCT2_MSG,
                    INVALID_CAR1_MSG,
                    INVALID_CAR1_MSG,
                    INVALID_PRODUCT1_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated List<Map<Car, Prodcut>>")
        void shouldReturnMsgWhenValidateMapInCar() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test7", List.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            LinkedHashMap<Car, Product> map1 = new LinkedHashMap<>();
            map1.put(this.validCar, this.invalidProduct1);
            map1.put(this.invalidCar1, this.validProduct);
            map1.put(this.invalidCar2, this.invalidProduct2);
            LinkedHashMap<Car, Product> map2 = new LinkedHashMap<>();
            map2.put(this.invalidCar1, this.invalidProduct1);
            map2.put(this.validCar, this.validProduct);

            when(this.joinPoint.getArgs()).thenReturn(new Object[] {Arrays.asList(map1, map2)});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList = Arrays.asList(INVALID_PRODUCT1_MSG,
                    INVALID_CAR1_MSG,
                    INVALID_CAR2_MSG,
                    INVALID_PRODUCT2_MSG,
                    INVALID_CAR1_MSG,
                    INVALID_PRODUCT1_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated Map<Person, List<Product>>")
        void shouldReturnMsgWhenValidateListInMap() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test8", Map.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            List<Product> productList1 = Arrays.asList(this.validProduct, this.invalidProduct1);
            List<Product> productList2 = Arrays.asList(this.validProduct, this.invalidProduct2);
            LinkedHashMap<Car, List<Product>> map = new LinkedHashMap<>();
            map.put(this.invalidCar1, productList1);
            map.put(this.invalidCar2, productList2);

            when(this.joinPoint.getArgs()).thenReturn(new Object[] {map});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList =
                    Arrays.asList(INVALID_CAR1_MSG, INVALID_PRODUCT1_MSG, INVALID_CAR2_MSG, INVALID_PRODUCT2_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }

        @Test
        @DisplayName("测试 @Validated List<Company>")
        void shouldReturnMsgWhenValidateListComplex() {
            Method validateMethod = ReflectionUtils.getDeclaredMethod(NestedValidate.class, "test9", List.class);
            when(this.joinPoint.getMethod()).thenReturn(validateMethod);
            Company company =
                    new Company(-1, 100, this.validProduct, Arrays.asList(this.invalidCar1, this.invalidCar2));
            when(this.joinPoint.getArgs()).thenReturn(new Object[] {Arrays.asList(company)});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, this.joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            List<String> msgList = Arrays.asList("经理只能有0-1个！", INVALID_CAR1_MSG, INVALID_CAR2_MSG);
            assertThat(expectedException.getMessage()).isEqualTo(msgList.stream().collect(Collectors.joining(", ")));
        }
    }
}
