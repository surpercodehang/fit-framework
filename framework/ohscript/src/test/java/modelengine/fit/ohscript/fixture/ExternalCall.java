/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.fixture;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * external调用的工具
 *
 * @since 1.0
 */
public class ExternalCall {
    @Getter
    private final Integer fix;

    private String strvalue = "me";

    private int numvalue = 100;

    @Getter
    private ExternalCall nest = null;

    @Getter
    private List<ExternalCall> list = new ArrayList<>();

    @Getter
    private Map<String, ExternalCall> map = new HashMap<>();

    @Getter
    private Set<String> set = new HashSet<>();

    /**
     * 构造函数
     *
     * @param fix 固定值
     */
    public ExternalCall(Integer fix) {
        this.fix = fix;
    }

    /**
     * 创建一个ExternalCall对象
     *
     * @param fix 固定值
     * @param str 字符串值
     * @return 返回创建的ExternalCall对象
     */
    public static ExternalCall create(Integer fix, String str) {
        ExternalCall call = new ExternalCall(fix);
        call.setStringValue(str);
        return call;
    }

    /**
     * 这个方法是用来创建一个新的ExternalCall对象的
     *
     * @return 返回创建的ExternalCall对象
     */
    public ExternalCall run1() {
        return new ExternalCall(fix);
    }

    /**
     * 这个方法是用来创建一个包含一个键值对的Map对象的
     *
     * @return 返回创建的Map对象
     */
    public Map<String, Integer> run2() {
        Map<String, Integer> aMap = new HashMap<>();
        aMap.put("a", 200);
        return aMap;
    }

    /**
     * 这个方法是用来将输入的ExternalCall对象的字符串值添加到当前对象的字符串值的后面的
     *
     * @param arg 输入的ExternalCall对象
     */
    public void run3(ExternalCall arg) {
        this.strvalue += arg.getStringValue();
    }

    /**
     * 这个方法是用来将输入的Map对象中的name键的值添加到当前对象的字符串值的后面的
     *
     * @param arg 输入的Map对象
     */
    public void run4(Map<String, Object> arg) {
        this.strvalue += arg.get("name");
    }

    /**
     * 这个方法是用来将输入的List对象中的第二个元素的值添加到当前对象的字符串值的后面的
     *
     * @param arg 输入的List对象
     */
    public void run5(List<Object> arg) {
        this.strvalue += arg.get(1).toString();
    }

    /**
     * 这个方法是用来将输入的ExternalCall对象的整数值添加到当前对象的整数值的
     *
     * @param arg 输入的ExternalCall对象
     */
    public void run6(ExternalCall arg) {
        this.numvalue += arg.getNumValue();
    }

    /**
     * 这个方法是用来将输入的ExternalCall对象的整数值添加到当前对象的整数值的，
     * 并将输入的ExternalCall对象的字符串值添加到当前对象的字符串值的后面的，
     * 并将输入的ExternalCall对象的固定值添加到当前对象的固定值的，
     * 并将输入的ExternalCall对象的list、map、set属性赋值给当前对象的同名属性的
     *
     * @param arg 输入的ExternalCall对象
     */
    public void run8(ExternalCall arg) {
        this.run6(arg);
        this.run3(arg);
        this.numvalue += arg.getFix();
        this.nest = arg;
        this.list = arg.list;
        this.map = arg.map;
        this.set = arg.set;
    }

    /**
     * 这个方法是用来生成一个包含5个元素的Byte数组的
     *
     * @return 返回生成的Byte数组
     */
    public Byte[] run10() {
        Byte[] bs = new Byte[5];
        for (byte i = 0; i < 5; i++) {
            bs[i] = i;
        }
        return bs;
    }

    /**
     * 这个方法是用来实现对一个Byte数组的每个元素乘以10的运算的
     *
     * @param bs 输入的Byte数组
     * @return 返回运算后的Byte数组
     */
    public Byte[] run11(Byte[] bs) {
        Byte[] bss = new Byte[bs.length];
        for (Byte i = 0; i < bs.length; i++) {
            bss[i] = (byte) (bs[i] * 10);
        }
        return bss;
    }

    /**
     * 这个方法是用来创建一个新的ExternalCall对象的
     *
     * @return 返回创建的ExternalCall对象
     */
    public ExternalCall run12() {
        return new ExternalCall(fix);
    }

    /**
     * 这个方法是用来创建一个包含两个ExternalCall对象的数组的
     *
     * @param call 输入的ExternalCall对象
     * @return 返回创建的ExternalCall对象数组
     */
    public ExternalCall[] run13(ExternalCall call) {
        ExternalCall[] calls = new ExternalCall[2];
        calls[0] = call;
        calls[1] = call;
        return calls;
    }

    /**
     * 这个方法是用来创建一个包含一个Map对象的Map对象的
     *
     * @return 返回创建的Map对象
     */
    public Map<String, Map> run15() {
        Map<String, Map> r = new HashMap<>();
        Map<String, String> p = new HashMap<>();
        r.put("will", p);
        p.put("name", "will zhang");
        return r;
    }

    /**
     * 这个方法是用来修改输入的ExternalCall对象数组中的第一个对象的字符串值，并返回这个数组
     *
     * @param calls 输入的ExternalCall对象数组
     * @return 返回修改后的ExternalCall对象数组
     */
    public ExternalCall[] run14(List<ExternalCall> calls) {
        calls.get(0).setStringValue("ohscript");
        return calls.toArray(new ExternalCall[0]);
    }

    /**
     * 这个方法是用来将输入的字符串数组中的第一个元素的值添加到当前对象的字符串值的后面的
     *
     * @param arg 输入的字符串数组
     */
    public void run7(String[] arg) {
        this.strvalue += arg[0];
    }

    /**
     * 这个方法是用来获取当前对象的字符串值的
     *
     * @return 返回当前对象的字符串值
     */
    public String getStringValue() {
        return this.strvalue;
    }

    /**
     * 这个方法是用来设置当前对象的字符串值的
     *
     * @param value 输入的字符串值
     */
    public void setStringValue(String value) {
        this.strvalue = value;
    }

    /**
     * 这个方法是用来获取当前对象的整数值的
     *
     * @return 返回当前对象的整数值
     */
    public int getNumValue() {
        return this.numvalue;
    }

    /**
     * 这个方法是用来设置当前对象的整数值的
     *
     * @param value 输入的整数值
     */
    public void setNumValue(Integer value) {
        this.numvalue = value;
    }

    /**
     * 这个方法是用来实现两个整数的加法运算的
     *
     * @param x 第一个整数
     * @param y 第二个整数
     * @return 返回两个整数相加的结果
     */
    public Integer add(Integer x, Integer y) {
        return (x + y) * 10;
    }
}

