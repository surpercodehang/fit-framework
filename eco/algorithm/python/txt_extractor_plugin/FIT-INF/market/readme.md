# TXT文本抽取插件

## 背景

本插件实现把输入的txt文件流抽取为字符串的功能

## 实现方法

通过decode方法，用utf-8-sig编码格式将字节串转换为字符串

## 约束

只可对编码为utf-8的txt文档做抽取

## 调用接口输入

``` python
inputs = [
    {
        "businessData": {
            "params": {}
        },
        "passData": {
            "data": b"\xe8\xbf\x99\xe6\x98\xaf\xe6\x8a\xbd\xe5\x8f\x96\xe5\x90\x8e\xe7\x9a\x84\xe5\xad\x97\xe7\xac\xa6\xe4\xb8\xb2",
            # 待处理数据，为字节流
            "text": "",
            "meta": {
                "fileName": "xxx.txt",
                "filePath": "xxx/xxx/xxx.txt",  # 文件路径
                "type": "txt"  # 支持的类型为txt文档
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
            "text": "这是抽取后的字符串。",  # 抽取后的结果为字符串
            "meta": {
                "fileName": "xxx.txt",
                "filePath": "xxx/xxx/xxx.txt",
                "type": "txt"
            }
        },
        "contextData": ""
    },
    {}
]
```
