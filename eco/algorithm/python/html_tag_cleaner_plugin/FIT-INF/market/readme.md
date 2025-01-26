# 去除HTML标签插件

## 背景

本插件实现将html标签去除的功能

## 约束

1. 去除常见的html标签及其属性值（不包括<table>|<tbody>|<tr>|<td>|</table>|</tbody>|</tr>|</td>）。去除的标签如下:
   tag_list = [
   '<a>', '<abbr>', '<acronym>', '<address>', '<applet>', '<area>', '<article>', '<aside>',
   '<audio>', '<b>', '<base>', '<basefont>', '<bdi>', '<bdo>', '<bgsound>', '<big>', '<blink>',
   '<blockquote>', '<body>', '<br>', '<button>', '<canvas>', '<caption>', '<center>', '<cite>',
   '<code>', '<col>', '<colgroup>', '<command>', '<content>', '<data>', '<datalist>', '<dd>',
   '<del>', '<details>', '<dfn>', '<dialog>', '<dir>', '<div>', '<dl>', '<dt>', '<em>',
   '<embed>', '<fieldset>', '<figcaption>', '<figure>', '<font>', '<footer>', '<form>', '<frame>',
   '<frameset>', '<h1>', '<h2>', '<h3>', '<h4>', '<h5>', '<h6>', '<head>', '<header>', '<hgroup>',
   '<hr>', '<html>', '<i>', '<iframe>', '<image>', '<img>', '<input>', '<ins>', '<isindex>',
   '<kbd>', '<keygen>', '<label>', '<legend>', '<li>', '<link>', '<listing>', '<main>', '<map>',
   '<mark>', '<marquee>', '<menu>', '<menuitem>', '<meta>', '<meter>', '<nav>', '<nobr>', '<noembed>',
   '<noframes>', '<noscript>', '<object>', '<ol>', '<optgroup>', '<option>', '<output>', '<p>',
   '<param>', '<picture>', '<plaintext>', '<pre>', '<progress>', '<q>', '<rp>', '<rt>', '<rtc>',
   '<ruby>', '<s>', '<samp>', '<script>', '<section>', '<select>', '<shadow>', '<small>',
   '<source>', '<spacer>', '<span>', '<strike>', '<strong>', '<style>', '<sub>', '<summary>',
   '<sup>', '<template>', '<textarea>', '<tfoot>', '<thead>', '<time>', '<title>', '<track>', '<tt>', '<u>',
   '<ul>', '<var>', '<video>', '<wbr>', '<xmp>'
   ]
2. 仅去除html标签，不去除标签对之间的文本
3. 去除表格标签内的属性值（不包括colspan、rowspan属性），eg:<td class="td8" rowspan="3"> —> <td rowspan="3">
4. 不对xml文档做处理

## 调用接口输入

``` python
inputs = {
    "businessData": {

    },
    "passData": {
        "data": "",
        "text": "<p><b>机器学习</b>是<a href=\"/wiki/%E4%BA%BA%E5%B7%A5%E6%99%BA%E8%83%BD\" title=\"人工智能\">人工智能</a>的一个分支。</p>",
        "meta": {"fileName": "xxx.doc"}  # 待处理数据元信息 
    },
    "contextData": ""
}

```

## 调用接口输出

``` python
outputs = {
    "businessData": {

    },
    "passData": {
        "data": "",
        "text": "机器学习是人工智能的一个分支。",  # 处理后数据
        "meta": {"fileName": "xxx.doc"}  # 处理后数据元信息 
    },
    "contextData": ""
}

```