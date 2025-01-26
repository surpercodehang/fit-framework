# word抽取插件

## 背景

1. 本插件将word文档中的文字抽取成字符串

## 约束

1. 支持中英文
2. 支持抽取超链接的文字，不支持抽取超链接的网址
3. 支持目录的抽取，不支持抽取目录域中的“...”。对于超链接形式的目录，有的目录doc可能无法抽取，有的目录doc抽取后为链接形式，如TOC
   \o "1-9" \h \u HYPERLINK \l _Toc569227348 DataBus简略技术文档1、或HYPERLINK \l _Toc1389641667
   背景1。当前使用正则进行提取目录标题，正则如下：( *TOC.*".*"[\\ huz]*)|( *HYPERLINK.*Toc\d+ *)
4. 仅支持由MicroSoft Office/WPS创建的docx文档，仅支持用MicroSoft Office和WPS都能正常打开的文件，docx文档需要支持XML解析
5. 不支持加密word文档，不支持抽取只有只读权限的文档，只支持抽取有完整读写权限的文件
6. 不支持抽取表格中的图片；
7. 不支持符号编号的抽取，docx文档有序数字编号可能出现数字错误、%问题，比如 图1-1 会被抽取成 图1-%8
8. 不支持抽取对象链接、文本框内容、艺术字、公式签名、批注、缩进格式、首字母悬挂格式、首字母下沉、关键词框、超链接中的网址
9. 默认去除文章的首尾空白字符

## 可能出现的异常场景

1. 多栏、图文环绕下，可能抽取多余编号、多余空行、标题数字改变问题
2. 竖向文字下，数字编号可能被替换为其他格式的编号
3. doc场景下，对于某种超链接形式的文字，可能会抽成"或无法抽取
4. docx抽取表格时，可能抽出多余空行，但不影响表格实际显示效果
5. doc、docx使用开源组件将文章转化为html再从html中抽取文字，当前无法解决开源组件文字抽取错误的场景

## URI说明

代码中出现的URI（Uniform Resource
Identifier）如下，这些URI并不指向实际的网页或网络资源，而是在XML解析器中用于启用或禁用特定的功能，这些配置有助于提高XML解析器的安全性，防止潜在的安全问题如外部实体攻击。
"http://apache.org/xml/features/disallow-doctype-decl"
"http://xml.org/sax/features/external-general-entities"
"http://xml.org/sax/features/external-parameter-entities"

## 调用接口输入

``` python
inputs = [
    {
        "businessData": {
            "params": {}
        },
        "passData": {
            "data": b"xxxxxx",  # 待处理数据  
            "text": "",
            "meta": {
                "fileName": "xxx.doc",
                "filePath": "xxx/xxx/xxx.doc",  # 文件路径
                "type": "doc"  # 支持类型doc、docx
            }  # 待处理数据元信息 
        },
        "contextData": {}
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
            "text": "这是抽取出来的doc文本",  # 抽取出来的word文本(抽取出来的文本除表格外，不带html标签)
            "meta": {
                "fileName": "xxx.doc",
                "filePath": "xxx/xxx/xxx.doc",
                "type": "doc"
            }
        },
        "contextData": {}
    },
    {}
]
```
