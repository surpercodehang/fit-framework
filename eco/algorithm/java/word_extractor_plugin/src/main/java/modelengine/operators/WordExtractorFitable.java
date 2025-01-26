/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.operators;

import fr.opensagres.poi.xwpf.converter.core.XWPFConverterException;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import modelengine.fit.waterflow.spi.WaterflowTaskHandler;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * WordExtractorFitable
 *
 * @since 2024-01-25
 */
@Component
public class WordExtractorFitable implements WaterflowTaskHandler {
    /**
     * word抽取算子实现封装的 fitable
     */

    private static final Logger logger = Logger.get(WordExtractorFitable.class);

    private static final Pattern PATTERN_SPACES = Pattern.compile(" {2,}");

    private static final Pattern PATTERN_DIV = Pattern.compile("\n *(<div>|</div>) *", Pattern.MULTILINE);

    private static final Pattern PATTERN_HYPERLINK = Pattern.compile(
        "( *TOC.*\".*\"[\\\\huz ]*)|( *HYPERLINK.*Toc\\d+ *)");

    /**
     * word抽取算子实现
     *
     * @param flowData 传入参数
     * @return response 回参
     */
    @Override
    @Fitable(id = "modelengine.operators.word_extractor_plugin")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        List<Map<String, Object>> response = new ArrayList<>(flowData.size());
        for (Map<String, Object> flowDatum : flowData) {
            Map<String, Object> meta = (Map<String, Object>) ((Map<String, Object>) flowDatum.get("passData")).get(
                "meta");
            String path = meta.get("filePath").toString();
            String fileName = meta.get("fileName").toString();
            byte[] fileBytes = (byte[]) ((Map<String, Object>) flowDatum.get("passData")).get("data");
            long startTime = new Date().getTime();
            String wordInfo = extractWord(path, fileBytes, fileName);
            try {
                ((Map<String, Object>) flowDatum.get("passData")).put("text", htmlToText(wordInfo));
            } catch (NullPointerException e) {
                logger.error(fileName + " is not supported. The text content extracted "
                    + "from the file is empty. The error is " + e.getMessage());
                throw new FitException(fileName + " is not supported. The text content "
                    + "extracted from the file is empty. The error is " + e.getMessage());
            }
            response.add(flowDatum);
            if (logger.isInfoEnabled()) {
                logger.info(path + ": total Time cost: " + String.valueOf(System.currentTimeMillis() - startTime));
            }
        }
        return response;
    }

    /**
     * 根据word文件类型选择抽取文字的方法
     *
     * @param path 文件路径
     * @param fileBytes 文件流
     * @param fileName 文件名称
     * @return html格式的文件信息
     */

    protected String extractWord(String path, byte[] fileBytes, String fileName) {
        String wordInfo;
        try {
            if (fileName.toLowerCase(Locale.ROOT).endsWith("doc")) {
                wordInfo = docToHtml(fileBytes, fileName);
            } else if (fileName.toLowerCase(Locale.ROOT).endsWith("docx")) {
                wordInfo = docxToHtml(fileBytes, fileName);
            } else {
                logger.error(fileName + " did not got the right type of word file");
                throw new FitException(fileName + " did not got the right type of word file");
            }
        } catch (ParserConfigurationException | TransformerException e) {
            logger.error(fileName + " fail to parse or transform file: " + path + e.getMessage());
            throw new FitException(fileName + " fail to parse or transform file: " + path + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error(fileName + " not found.");
            throw new FitException(fileName + " not found.");
        } catch (IOException e) {
            logger.error(fileName + " cannot read" + e.getMessage());
            throw new FitException(fileName + " cannot read" + e.getMessage());
        }
        return wordInfo;
    }

    /**
     * 将doc格式文档转成html格式
     *
     * @param fileBytes 文件流
     * @param fileName 文件名称
     * @return html格式的文件信息
     * @throws ParserConfigurationException： 读取文件时报错抛出的异常
     * @throws TransformerException： 读取文件时报错抛出的异常
     * @throws IOException： 读取文件时报错抛出的异常
     */
    protected String docToHtml(byte[] fileBytes, String fileName)
        throws ParserConfigurationException, TransformerException, IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(fileBytes)) {
            // 设置一些防止XXE漏洞的特性,禁用XML解析器加载外部DTD（文档类型定义）和实体
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder db = dbf.newDocumentBuilder();

            // 创建word转化为html对象
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(db.newDocument());
            // 设置图片信息
            wordToHtmlConverter.setPicturesManager(new PicturesManager() {
                public String savePicture(byte[] content, PictureType pictureType, String suggestedName,
                    float widthInches, float heightInches) {
                    return "<image>";
                }
            });

            // 使用HWPFDocument读取Word文档的内容
            HWPFDocument wordDocument = new HWPFDocument(input);
            // 将Word文档的内容转换为HTML,获取转换后的HTML文档
            wordToHtmlConverter.processDocument(wordDocument);
            org.w3c.dom.Document htmlDocument = wordToHtmlConverter.getDocument();
            // 使用DOMSource封装HTML文档
            DOMSource domSource = new DOMSource(htmlDocument);

            // 创建TransformerFactory对象，并设置防止XXE漏洞的特性
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            // 创建Transformer对象，并设置输出的编码utf-8、保留缩进行结构、输出格式为html
            Transformer serializer = factory.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");

            wordDocument.close();

            // 将转换后的HTML文档序列化为字节数组，最终转换为字符串并返回
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                StreamResult streamResult = new StreamResult(byteArrayOutputStream);
                serializer.transform(domSource, streamResult);
                return byteArrayOutputStream.toString("utf-8");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error(
                "current doc file: " + fileName + " is not supported. " + "The file is a UNKNOWN file. The error is "
                    + e.getMessage());
            throw new FitException(
                "current doc file: " + fileName + " is not supported. " + "The file is a UNKNOWN file. The error is "
                    + e.getMessage());
        }
    }

    /**
     * 将docx格式文档转成html格式
     *
     * @param fileBytes 文件流
     * @param fileName 文件名
     * @return html格式的文件信息
     * @throws IOException: 读取文件时报错抛出 IO 异常
     */
    protected String docxToHtml(byte[] fileBytes, String fileName) throws IOException {
        // 设置最小阈值为0，默认0.01会报ZipBomb异常
        ZipSecureFile.setMinInflateRatio(0);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            // 读取docx格式文档
            XWPFDocument xwpfDocument = new XWPFDocument(inputStream);
            XHTMLOptions xhtmlOptions = XHTMLOptions.create();
            xhtmlOptions.setFragment(false);
            // 忽略文档中的所有样式
            xhtmlOptions.setIgnoreStylesIfUnused(true);
            xhtmlOptions.setIgnoreStylesIfUnused(true);
            xwpfDocument.createNumbering();

            try (ByteArrayOutputStream outPutTarget = new ByteArrayOutputStream()) {
                XHTMLConverter.getInstance().convert(xwpfDocument, outPutTarget, xhtmlOptions);
                return outPutTarget.toString();
            } catch (XWPFConverterException | NullPointerException e) {
                logger.error(
                    "current docx file: " + fileName + " cannot convert to html. " + "The error is " + e.getMessage());
                throw new FitException(
                    "current docx file: " + fileName + " cannot convert to html. " + "The error is " + e.getMessage());
            }
        } catch (OLE2NotOfficeXmlFileException e) {
            logger.error("current docx file: " + fileName + " is not supported. "
                + "The only supported type for docx is OOXML. The error is " + e.getMessage());
            throw new FitException("current docx file: " + fileName + " is not supported. "
                + "The only supported type for docx is OOXML. The error is " + e.getMessage());
        }
    }

    /**
     * 将html文件格式化，抽取文章内容
     *
     * @param html html字符串
     * @return word文章内容
     */
    private static String htmlToText(String html) {
        // 使用正则表达式匹配空格及其数量
        Matcher matcherSpaces = PATTERN_SPACES.matcher(html);
        StringBuffer output = new StringBuffer();
        while (matcherSpaces.find()) {
            String spaces = matcherSpaces.group();
            int count = spaces.length();
            String replacement = new String(new char[count]).replace("\0", "&nbsp;");
            matcherSpaces.appendReplacement(output, replacement);
        }
        matcherSpaces.appendTail(output);

        String parseHtmlText = parseHtml(output.toString());

        // 使用 StringBuilder 在字符串首尾添加\n,防止正则匹配失败，正则匹配成功后需删除\n
        StringBuilder modifiedText = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        modifiedText.append(lineSeparator).append(parseHtmlText).append(lineSeparator);

        // 去除div标签及前后空白字符
        Matcher divMatcher = PATTERN_DIV.matcher(modifiedText.toString());
        String replaceDivText = divMatcher.replaceAll("");
        String removeDivText = replaceDivText.substring(lineSeparator.length(),
            replaceDivText.length() - lineSeparator.length());
        String removeUnnecessaryTagsText = removeDivText.replaceAll(" *<p>|<br>|</p>|%2|<p>", "")
            .replaceAll("&nbsp;", " ");

        // 定义正则表达式，匹配 HYPERLINK 开头的字符串 (超链接形式的目录)
        Matcher hyperlinkMatcher = PATTERN_HYPERLINK.matcher(removeUnnecessaryTagsText);
        return hyperlinkMatcher.replaceAll("");
    }

    /**
     * 解析html字符串，去除无用标签
     *
     * @param html html字符串
     * @return word文章内容
     */
    private static String parseHtml(String html) {
        // 解析 HTML 并设置保留空格
        Document document = Jsoup.parse(html);
        document.outputSettings().prettyPrint(true);

        // 转义空格、p标签
        for (Element element : document.select("p")) {
            String newHtml = element.html().replaceAll("</span> </a>", "</span></a>");
            element.html(newHtml);
            String replacement = element.wholeText().replaceAll(" ", "&nbsp;");
            element.text(replacement);
        }

        // 提取<a>标签中的文本并将其设置到它们的父标签中
        Elements links = document.select("a");
        for (Element link : links) {
            link.replaceWith(new Element("span").text(link.text()));
        }

        Elements elements = document.body().getAllElements();
        // 移除所有不需要的属性
        for (Element element : elements) {
            element.removeAttr("style");
            element.removeAttr("class");
        }

        Elements spans = document.select("span");
        for (Element span : spans) {
            span.unwrap();
        }
        // 移除所有空的<p>标签
        document.select("p:empty").remove();

        // 对文本中的符号进行转义
        return document.body()
            .children()
            .toString()
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">")
            .replaceAll("&amp;", "&");
    }
}
