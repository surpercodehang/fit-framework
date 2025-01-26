# 检查文档重复字率插件

## 背景

1. 本插件实现了统计文档词重复率，根据客户设定的阈值将存在重复率高于阈值的字的文档删除。注：此算子只针对中文字进行重复率统计。

## 约束

1. 只针对中文字进行重复率统计
2. 默认字重复率阈值为0.5
3. 只要有一个字重复率高于阈值，该文档就会被过滤

## 调用接口输入

``` python
inputs = [
    {
        "businessData": {
            "params": {
                "repeatWordRatio": 0.5}
        },
        "passData": {
            "data": "",
            "text": "机器学学学学学学学学学学学学学学学学学学学学学学学学学学学学学学习",  # 待处理数据
            "meta": {
                "fileName": "xxx.doc"
            }  # 待处理数据元信息 
        },
        "contextData": ""
    },
    {}
]
```

## 调用接口输出

``` python
outputs = [
    {
        "businessData": {
            "params": {
                "repeatWordRatio": 0.5}
        },
        "passData": {
            "data": "",
            "text": "",  # 处理后数据
            "meta": {
                "fileName": "xxx.doc"
            }  # 处理后数据元信息 
        },
        "contextData": ""
    },
    {}
]
```

