/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans.convert;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 为 {@link ValueConverter} 提供内置转换程序的实现。
 *
 * @author 梁济时
 * @since 2022-12-27
 */
public final class BuiltinValueConverters {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private BuiltinValueConverters() {}

    /**
     * 将 {@link Number} 对象转换为 {@link Byte} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Byte}。
     */
    @ConverterMethod
    public static Byte toByte(Number number) {
        return number.byteValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Byte} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Byte}。
     */
    @ConverterMethod
    public static Byte toByte(String string) {
        return Byte.parseByte(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link Short} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Short}。
     */
    @ConverterMethod
    public static Short toShort(Number number) {
        return number.shortValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Short} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Short}。
     */
    @ConverterMethod
    public static Short toShort(String string) {
        return Short.parseShort(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link Integer} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Integer}。
     */
    @ConverterMethod
    public static Integer toInteger(Number number) {
        return number.intValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Integer} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Integer}。
     */
    @ConverterMethod
    public static Integer toInteger(String string) {
        return Integer.parseInt(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link Long} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Long}。
     */
    @ConverterMethod
    public static Long toLong(Number number) {
        return number.longValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Long} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Long}。
     */
    @ConverterMethod
    public static Long toLong(String string) {
        return Long.parseLong(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link Float} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Float}。
     */
    @ConverterMethod
    public static Float toFloat(Number number) {
        return number.floatValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Float} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Float}。
     */
    @ConverterMethod
    public static Float toFloat(String string) {
        return Float.parseFloat(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link Double} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Double}。
     */
    @ConverterMethod
    public static Double toDouble(Number number) {
        return number.doubleValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Double} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Double}。
     */
    @ConverterMethod
    public static Double toDouble(String string) {
        return Double.parseDouble(string);
    }

    /**
     * 将 {@link BigInteger} 对象转换为 {@link Boolean} 对象。
     *
     * @param integer 表示待转换的 {@link BigInteger}。
     * @return 表示转换后的 {@link Boolean}。
     */
    @ConverterMethod
    public static Boolean toBoolean(BigInteger integer) {
        return !Objects.equals(integer, BigInteger.ZERO);
    }

    /**
     * 将 {@link BigDecimal} 对象转换为 {@link Boolean} 对象。
     *
     * @param decimal 表示待转换的 {@link BigDecimal}。
     * @return 表示转换后的 {@link Boolean}。
     */
    @ConverterMethod
    public static Boolean toBoolean(BigDecimal decimal) {
        return !Objects.equals(decimal, BigDecimal.ZERO.setScale(decimal.scale(), BigDecimal.ROUND_FLOOR));
    }

    /**
     * 将 {@link String} 对象转换为 {@link Boolean} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Boolean}。
     */
    @ConverterMethod
    public static Boolean toBoolean(String string) {
        return Boolean.parseBoolean(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link Character} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link Character}。
     */
    @ConverterMethod
    public static Character toCharacter(Number number) {
        return (char) number.longValue();
    }

    /**
     * 将 {@link String} 对象转换为 {@link Character} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Character}。
     */
    @ConverterMethod
    public static Character toCharacter(String string) {
        if (string.isEmpty()) {
            return '\0';
        } else {
            return string.charAt(0);
        }
    }

    /**
     * 将任意对象转换为 {@link String} 对象。
     *
     * @param object 表示待转换的任意对象的 {@link Object}。
     * @return 表示转换后的 {@link String}。
     */
    @ConverterMethod
    public static String toString(Object object) {
        return ObjectUtils.toString(object);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link BigInteger} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link BigInteger}。
     */
    @ConverterMethod
    public static BigInteger toBigInteger(Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        } else {
            return BigInteger.valueOf(number.longValue());
        }
    }

    /**
     * 将 {@link String} 对象转换为 {@link BigInteger} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link BigInteger}。
     */
    @ConverterMethod
    public static BigInteger toBigInteger(String string) {
        return new BigInteger(string);
    }

    /**
     * 将 {@link Number} 对象转换为 {@link BigDecimal} 对象。
     *
     * @param number 表示待转换的 {@link Number}。
     * @return 表示转换后的 {@link BigDecimal}。
     */
    @ConverterMethod
    public static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        } else {
            return BigDecimal.valueOf(number.doubleValue());
        }
    }

    /**
     * 将 {@link String} 对象转换为 {@link BigDecimal} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link BigDecimal}。
     */
    @ConverterMethod
    public static BigDecimal toBigDecimal(String string) {
        return new BigDecimal(string);
    }

    /**
     * 将 {@link String} 对象转换为 {@link Date} 对象。
     *
     * @param string 表示待转换的 {@link String}。
     * @return 表示转换后的 {@link Date}。
     */
    @ConverterMethod
    public static Date toDate(String string) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            return format.parse(string);
        } catch (ParseException ex) {
            throw new IllegalStateException(StringUtils.format("Illegal date string. [value={0}]", string), ex);
        }
    }
}
