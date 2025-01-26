/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript;

/**
 * ohScript里允许的语法测试字符串
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public final class ProductionCases {
    /**
     * 测试变量声明语法
     * 这里是一个变量声明语句，声明了一个名为var1的变量，并赋值为-3.66
     */
    public static final String VARIANT_DECLARATION = "let var1 = -3.66;";

    /**
     * 测试未知标记语法
     * 这里是一个未知标记语句，尝试声明一个名为unknown var1的变量，并赋值为3.66
     */
    public static final String UNKNOWN_TOKEN = "00unknown var1 = 3.66;";

    /**
     * 测试表达式语法
     * 这里是一个表达式语句，声明了一个名为a的变量，并赋值为一个复杂的数学表达式的结果
     */
    public static final String EXPRESSION = "let a = (let43+5)*10/3-4;";

    /**
     * 测试if语句语法
     * 这里是一个if语句，根据一些条件执行不同的代码块
     */
    public static final String IF = "if(a>=0 && b==0 || c<=0){" + System.lineSeparator() + "a=1;"
            + System.lineSeparator() + "}else a=2;";

    /**
     * 测试while语句语法
     * 这里是一个while语句，根据一些条件反复执行某个代码块
     */
    public static final String WHILE = "while(while123!=100){a++;break;}";

    /**
     * 测试for语句语法
     * 这里是一个for语句，根据一些条件反复执行某个代码块
     */
    public static final String FOR = "for(var i=0; i<100; i++){a = b|c; d=e&f; continue;}";

    /**
     * 测试import语句语法
     * 这里是一个import语句，引入了一个外部的函数库
     */
    public static final String IMPORT = "import modelengine.jober.somefunction;";

    /**
     * 测试with语句语法
     * 这里是一个with语句，设置了一些上下文环境，并在这个环境下执行某些代码
     */
    public static final String WITH = "with(context,form){name = name;}";

    /**
     * 测试namespace语句语法
     * 这里是一个namespace语句，设置了一个命名空间，并在这个命名空间下执行某些代码
     */
    public static final String NAMESPACE = "namespace modelengine.jober";

    /**
     * 测试实体语句语法
     * 这里是一个实体语句，定义了一个实体，并设置了一些属性和方法
     */
    public static final String ENTITY = "entity{.name=\"will\"; .age=48; .age = func(){this.age}}";
}
