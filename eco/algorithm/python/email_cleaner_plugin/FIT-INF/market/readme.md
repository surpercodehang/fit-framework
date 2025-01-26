# 电子邮箱匿名化插件

## 背景

1. 本插件实现电子邮箱匿名化的功能，将文本中的电子邮箱替换为归一化标签 token——`<email>`
2. 引入开源包jionlp

## 约束
1. 通过@符号正则匹配字符串，使用开源三方包email_validator校验邮箱合法性。若合法则匿名化为<email>

## 调用接口输入
``` python
inputs = [
    {
        "businessData": {
            "params": {}
        },
        "passData": {
            "data": "",
            "text": "这个是邮箱号xxx@xxx.xxx",  # 待处理数据
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
            "params": {}
        },
        "passData": {
            "data": "",
            "text": "这个是邮箱号<email>",  # 待处理数据
            "meta": {
                "fileName": "xxx.doc"
            }  # 待处理数据元信息 
        },
        "contextData": ""
    },
    {}
]
```

## 执行效率

处理速度毫秒级。451个测试文档、大小33.6MB，耗时245ms

## 接口并发效率

每秒请求数969个Request
