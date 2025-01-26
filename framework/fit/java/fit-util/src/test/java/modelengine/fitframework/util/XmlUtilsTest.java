/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/**
 * 为 {@link XmlUtils} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
@DisplayName("测试 XmlUtils")
public class XmlUtilsTest {
    private Document document;
    private Document department;
    private Document employee;
    private Node rootNode;
    private Map<String, Object> documentMap;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws IOException {
        try (InputStream in = XmlUtilsTest.class.getResourceAsStream("/demo.xml")) {
            this.document = XmlUtils.load(in);
            this.rootNode = this.document.getChildNodes().item(0);
        }

        try (InputStream in = XmlUtilsTest.class.getResourceAsStream("/employee.xml")) {
            this.employee = XmlUtils.load(in);
        }

        try (InputStream in = XmlUtilsTest.class.getResourceAsStream("/department.xml")) {
            this.department = XmlUtils.load(in);
        }

        try (InputStream in = XmlUtilsTest.class.getResourceAsStream("/demo.json")) {
            this.documentMap = mapper.readValue(in, Map.class);
        }
    }

    @AfterEach
    void tearDown() {
        this.rootNode = null;
        this.document = null;
    }

    /**
     * 目标方法：{@link XmlUtils#appendChild(Node, Function)}。
     */
    @Nested
    @DisplayName("Test method: appendChild(Node parentNode, Function<Document, T> factory)")
    class TestAppendChild {
        @Test
        @DisplayName("ParentNode is single element, factory is not null, output is exception")
        void givenParentNodeIsSingleElementThenThrowException() {
            Element element = mock(Element.class);
            IllegalStateException exception = catchThrowableOfType(() -> XmlUtils.appendChild(element,
                    document -> document.createElement("Name")), IllegalStateException.class);
            assertThat(exception).hasMessage("The XML node to create child does not belong to any document.");
        }
    }

    /**
     * 目标方法：{@link XmlUtils#appendElement(Node, String)}。
     */
    @Nested
    @DisplayName("Test method: appendElement(Node parentNode, String tagName)")
    class TestAppendElement {
        @Test
        @DisplayName("ParentNode is null, tagName is '', output is exception")
        void givenParentNodeNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> XmlUtils.appendElement(null, "Hello"), IllegalArgumentException.class);
            assertThat(exception).hasMessage("The parent node to create child element cannot be null.");
        }

        @Test
        @DisplayName("ParentNode is not null, tagName is '', output is exception")
        void givenEmptyParentNodeAndEmptyTagNameThenThrowException() {
            Document createdDocument = XmlUtils.createDocument();
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> XmlUtils.appendElement(createdDocument, StringUtils.EMPTY),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("The name of child tag to create cannot be blank.");
        }

        @Test
        @DisplayName("ParentNode is not null, tagName is 'Hello', output is correct")
        void givenEmptyParentNodeAndNotEmptyTagNameThenReturnCorrectly() {
            Document createdDocument = XmlUtils.createDocument();
            Element actual = XmlUtils.appendElement(createdDocument, "Hello");
            assertThat(actual).isNotNull();
            assertThat(actual.getNodeName()).isEqualTo("Hello");
        }
    }

    /**
     * 目标方法：{@link XmlUtils#appendText(Node, String)}。
     */
    @Nested
    @DisplayName("Test method: appendText(Node parentNode, String text)")
    class TestAppendText {
        @Test
        @DisplayName("ParentNode is null, text is 'Hello', output is exception")
        void givenParentNodeNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> XmlUtils.appendText(null, "Hello"), IllegalArgumentException.class);
            assertThat(exception).hasMessage("The parent node to append text cannot be null.");
        }

        @Test
        @DisplayName("ParentNode is parent/child, text is 'Hello', output is correct")
        void givenEmptyParentNodeAndTextHelloThenReturnNoException() {
            Node child = XmlUtils.child(XmlUtilsTest.this.rootNode, "parent/child");
            assertThat(child.getTextContent()).isEqualTo(StringUtils.EMPTY);
            assertThatNoException().isThrownBy(() -> XmlUtils.appendText(child, "Hello"));
            assertThat(child.getTextContent()).isEqualTo("Hello");
        }

        @Test
        @DisplayName("ParentNode is parent/child, text is '', output is correct")
        void givenEmptyParentNodeAndTextEmptyThenReturnNoException() {
            Node child = XmlUtils.child(XmlUtilsTest.this.rootNode, "parent/child");
            assertThat(child.getTextContent()).isEqualTo(StringUtils.EMPTY);
            assertThatNoException().isThrownBy(() -> XmlUtils.appendText(child, StringUtils.EMPTY));
            assertThat(child.getTextContent()).isEqualTo(StringUtils.EMPTY);
        }
    }

    /**
     * 目标方法：{@link XmlUtils#child(Node, String)}。
     */
    @Nested
    @DisplayName("Test method: child(Node parentNode, String xpath)")
    class TestChild {
        @Test
        @DisplayName("ParentNode is root, xpath is null, output is root")
        void givenXpathNullThenReturnParentNode() {
            Node actual = XmlUtils.child(XmlUtilsTest.this.rootNode, null);
            assertThat(actual).isEqualTo(XmlUtilsTest.this.rootNode);
        }

        @Test
        @DisplayName("ParentNode is root, xpath is 'parent/child', output is parent/child")
        void givenExistXpathParentChildThenReturnNodeChild() {
            Node actual = XmlUtils.child(XmlUtilsTest.this.rootNode, "parent/child");
            assertThat(actual).isNotNull();
            assertThat(actual.getNodeName()).isEqualTo("child");
        }

        @Test
        @DisplayName("ParentNode is root, xpath is 'parent/childNotExist', output is null")
        void givenNotExistXpathThenReturnNode() {
            Node actual = XmlUtils.child(XmlUtilsTest.this.rootNode, "parent/childNotExist");
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("ParentNode is null, xpath is 'parent', output is null")
        void givenParentNullAndExistXpathParentThenReturnNull() {
            Node actual = XmlUtils.child(null, "parent");
            assertThat(actual).isNull();
        }
    }

    /**
     * 目标方法：{@link XmlUtils#createDocument()}。
     */
    @Nested
    @DisplayName("Test method: createDocument()")
    class TestCreateDocument {
        @Test
        @DisplayName("No input, output is a new document")
        void givenDefaultSecurityOptionsThenReturnNewDocument() {
            Document actual = XmlUtils.createDocument();
            assertThat(actual).isNotNull();
            assertThat(actual.getNodeName()).isEqualTo("#document");
            assertThat(actual.getTextContent()).isNull();
        }

        @SuppressWarnings("HttpUrlsUsage")
        @Test
        @DisplayName("Mock DocumentBuilderFactory, enable feature error, output is exception")
        void givenMockedDocumentBuilderFactoryAndEnableFeatureErrorThenThrowException()
                throws ParserConfigurationException {
            String feature = "http://apache.org/xml/features/disallow-doctype-decl";
            try (MockedStatic<DocumentBuilderFactory> mocked = mockStatic(DocumentBuilderFactory.class)) {
                DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);
                doThrow(new ParserConfigurationException()).when(factory).setFeature(eq(feature), eq(true));
                mocked.when(DocumentBuilderFactory::newInstance).thenReturn(factory);
                IllegalStateException exception =
                        catchThrowableOfType(XmlUtils::createDocument, IllegalStateException.class);
                assertThat(exception).hasMessage(StringUtils.format(
                        "Failed to enable feature for document builder factory. [feature={0}]",
                        feature)).getCause().isInstanceOf(ParserConfigurationException.class);
            }
        }

        @SuppressWarnings("HttpUrlsUsage")
        @Test
        @DisplayName("Mock DocumentBuilderFactory, disable feature error, output is exception")
        void givenMockedDocumentBuilderFactoryAndDisableFeatureErrorThenThrowException()
                throws ParserConfigurationException {
            String feature = "http://xml.org/sax/features/external-general-entities";
            try (MockedStatic<DocumentBuilderFactory> mocked = mockStatic(DocumentBuilderFactory.class)) {
                DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);
                doThrow(new ParserConfigurationException()).when(factory).setFeature(eq(feature), eq(false));
                mocked.when(DocumentBuilderFactory::newInstance).thenReturn(factory);
                IllegalStateException exception =
                        catchThrowableOfType(XmlUtils::createDocument, IllegalStateException.class);
                assertThat(exception).hasMessage(StringUtils.format(
                        "Failed to disable feature for document builder factory. [feature={0}]",
                        feature)).getCause().isInstanceOf(ParserConfigurationException.class);
            }
        }

        @Test
        @DisplayName("Mock DocumentBuilderFactory, newDocumentBuilder error, output is exception")
        void givenMockedDocumentBuilderFactoryAndNewDocumentBuilderErrorThenThrowException()
                throws ParserConfigurationException {
            try (MockedStatic<DocumentBuilderFactory> mocked = mockStatic(DocumentBuilderFactory.class)) {
                DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);
                doThrow(new ParserConfigurationException()).when(factory).newDocumentBuilder();
                mocked.when(DocumentBuilderFactory::newInstance).thenReturn(factory);
                IllegalStateException exception =
                        catchThrowableOfType(XmlUtils::createDocument, IllegalStateException.class);
                assertThat(exception).hasMessage("Failed to create document builder.")
                        .getCause()
                        .isInstanceOf(ParserConfigurationException.class);
            }
        }
    }

    @Nested
    @DisplayName("Test filter")
    class TestFilter {
        /**
         * 目标方法：{@link XmlUtils#filter(NodeList, Predicate)}。
         */
        @Nested
        @DisplayName("Test method: filter(NodeList nodes, Predicate<Node> predicate)")
        class TestFilterByPredicate {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Nodes is null, predicate is class='class1', output is null")
            void givenNodesIsNullThenReturnNull() {
                Predicate<Node> predicate = XmlUtilsTest.this.predicateByClassAttribute("class1");
                NodeList actual = XmlUtils.filter(null, predicate);
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Nodes is root document children, predicate is tagName='first-level', "
                    + "output is 4 first-level nodes")
            void givenRootDocumentAndTagNameFilterThenReturn3FirstLevelNodes() {
                Predicate<Node> filter = node -> StringUtils.equals(node.getNodeName(), "first-level");
                NodeList actual = XmlUtils.filter(XmlUtilsTest.this.rootNode.getChildNodes(), filter);
                assertThat(actual).isNotNull();
                assertThat(actual.getLength()).isEqualTo(4);
            }
        }

        /**
         * 目标方法：{@link XmlUtils#filterByAttribute(NodeList, String, String)}。
         */
        @Nested
        @DisplayName("Test method: filterByAttribute(NodeList nodes, String attribute, String value)")
        class TestFilterByAttributeNotIgnoreCase {
            @Test
            @DisplayName("Nodes is root document children, attribute is 'id', value is '1', output is first-level-1")
            void givenRootDocumentAndIdIs1FilterThenReturnFirstLevel1() {
                NodeList actual = XmlUtils.filterByAttribute(XmlUtilsTest.this.rootNode.getChildNodes(), "id", "1");
                assertThat(actual).isNotNull();
                assertThat(actual.getLength()).isEqualTo(1);
            }
        }

        /**
         * 目标方法：{@link XmlUtils#filterByName(NodeList, String)}。
         */
        @Nested
        @DisplayName("Test method: filterByName(NodeList nodes, String name)")
        class TestFilterByNameNotIgnoreCase {
            @Test
            @DisplayName("Nodes is root document children, name='first-level', output is 4 first-level nodes")
            void givenRootDocumentAndNameFirstLevelThenReturn3FirstLevelNodes() {
                NodeList actual = XmlUtils.filterByName(XmlUtilsTest.this.rootNode.getChildNodes(), "first-level");
                assertThat(actual).isNotNull();
                assertThat(actual.getLength()).isEqualTo(4);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：content(Node, String)")
    class TestContent {
        @Test
        @DisplayName("给定值为 null 的 Node 值，属性值为 parent/child，返回值为空")
        void givenNullNodeThenReturnNull() {
            assertThat(XmlUtils.content(null, "parent/child")).isNull();
        }

        @Test
        @DisplayName("给定有效的 Node 值与空的属性值，返回值为空")
        void givenEmptyAttributeValueThenReturnNull() {
            assertThat(XmlUtils.content(rootNode, "")).isNull();
        }

        @Test
        @DisplayName("给定对于 Node 值不存在的 String 属性值，返回空")
        void givenNotExistStringValueThenReturnNull() {
            assertThat(XmlUtils.content(rootNode, "attribute")).isNull();
        }

        @Test
        @DisplayName("给定对于 Node 值有效的 String 属性值，返回不为空")
        void givenExistStringValueThenReturnValue() {
            assertThat(XmlUtils.content(rootNode, "parent/child")).isNotNull();
        }
    }

    @Nested
    @DisplayName("Test firstElement")
    class TestFirstElement {
        /**
         * 目标方法：{@link XmlUtils#firstElement(NodeList)}。
         */
        @Nested
        @DisplayName("Test method: firstElement(NodeList nodes)")
        class TestFirstElementWithoutPredicate {
            @Test
            @DisplayName("Input is root document children, output is first-level-1 node")
            void givenRootDocumentThenReturnFirstElement() {
                Node actual = XmlUtils.firstElement(XmlUtilsTest.this.rootNode.getChildNodes());
                assertThat(actual).isNotNull();
                assertThat(actual.getNodeName()).isEqualTo("first-level");
                assertThat(actual.getTextContent()).isEqualTo("first-level-1");
                assertThat(XmlUtils.getAttributeValue(actual, "id")).isEqualTo("1");
            }
        }

        /**
         * 目标方法：{@link XmlUtils#firstElement(NodeList, Predicate)}。
         */
        @Nested
        @DisplayName("Test method: firstElement(NodeList nodes, Predicate<Node> predicate)")
        class TestFirstElementWithPredicate {
            @Test
            @DisplayName("Input is root document children, predicate is class='class1', output is first-level-2 node")
            void givenRootDocumentAndClass1AttributeFilterThenReturnFirstLevel2() {
                Predicate<Node> predicate = XmlUtilsTest.this.predicateByClassAttribute("class1");
                Node actual = XmlUtils.firstElement(XmlUtilsTest.this.rootNode.getChildNodes(), predicate);
                assertThat(actual).isNotNull();
                assertThat(actual.getNodeName()).isEqualTo("first-level");
                assertThat(actual.getTextContent()).isEqualTo("first-level-2");
                assertThat(XmlUtils.getAttributeValue(actual, "id")).isEqualTo("2");
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Input is null, predicate is class='class1', output is null")
            void givenNullAndClass1AttributeFilterThenReturnNull() {
                Predicate<Node> predicate = XmlUtilsTest.this.predicateByClassAttribute("class1");
                Node actual = XmlUtils.firstElement(null, predicate);
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Input is root document children, predicate is class='notExistClass', output is null")
            void givenRootDocumentAndNotExistClassAttributeFilterThenReturnNull() {
                Predicate<Node> predicate = XmlUtilsTest.this.predicateByClassAttribute("notExistClass");
                Node actual = XmlUtils.firstElement(XmlUtilsTest.this.rootNode.getChildNodes(), predicate);
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Input is parent/child, predicate is class='notExistClass', output is null")
            void givenParentChildAndNotExistClassAttributeFilterThenReturnNull() {
                Predicate<Node> predicate = XmlUtilsTest.this.predicateByClassAttribute("notExistClass");
                Node child = XmlUtils.child(XmlUtilsTest.this.rootNode, "parent/child");
                Node actual = XmlUtils.firstElement(child.getChildNodes(), predicate);
                assertThat(actual).isNull();
            }
        }
    }

    /**
     * 目标方法：{@link XmlUtils#getAttributeValue(Node, String)}。
     */
    @Nested
    @DisplayName("Test method: getAttributeValue(Node node, String attribute)")
    class TestGetAttributeValue {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Node is null, attribute is 'a', output is null")
        void givenNodeNullThenReturnNull() {
            String actual = XmlUtils.getAttributeValue(null, "a");
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Node is root document, attribute is null, output is null")
        void givenNodeNotNullAndAttributeNullThenReturnNull() {
            String actual = XmlUtils.getAttributeValue(XmlUtilsTest.this.rootNode, null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Node is root document, attribute is 'a', output is null")
        void givenNodeNotNullAndNotExistAttributeThenReturnNull() {
            String actual = XmlUtils.getAttributeValue(XmlUtilsTest.this.rootNode, "a");
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Node is text node, attribute is 'a', output is null")
        void givenTextNodeThenReturnNull() {
            Node textNode = XmlUtilsTest.this.rootNode.getChildNodes().item(0);
            String actual = XmlUtils.getAttributeValue(textNode, "a");
            assertThat(actual).isNull();
        }
    }

    /**
     * 目标方法：{@link XmlUtils#load(InputStream)}。
     */
    @Nested
    @DisplayName("Test method: load(InputStream in)")
    class TestLoad {
        @Test
        @DisplayName("In is empty, output is exception")
        void givenNotXmlInputStreamThenThrowException() throws IOException {
            try (InputStream in = new ByteArrayInputStream(new byte[0])) {
                IllegalStateException exception =
                        catchThrowableOfType(() -> XmlUtils.load(in), IllegalStateException.class);
                assertThat(exception).hasMessage("Failed to parse XML from the input stream.")
                        .getCause()
                        .isInstanceOf(SAXException.class);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：writer(OutputStream out)")
    class TestWrite {
        @Test
        @DisplayName("给定传入参数为 null，输出抛出异常")
        void givenEmptyXmlOutputStreamThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> XmlUtils.writer(null), IllegalArgumentException.class);
            assertThat(exception).hasMessage("The output stream of writer cannot be null.")
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("测试类：XmlUtils.Writer")
    class WriteTest {
        private static final String FOUR_BLANK = "    ";

        @Nested
        @DisplayName("测试方法：write(Document document)")
        class TestWrite {
            @Test
            @DisplayName("给定传入文件参数为 null，输出抛出异常")
            void givenEmptyXmlDocumentThenThrowException() throws IOException {
                try (ByteArrayOutputStream temporary = new ByteArrayOutputStream()) {
                    IllegalArgumentException exception = catchThrowableOfType(() -> XmlUtils.writer(temporary)
                            .enableIndent()
                            .indentWidth(4)
                            .write(null), IllegalArgumentException.class);
                    assertThat(exception).hasMessage("The XML document to save cannot be null.")
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Test
            @DisplayName("给定非空文件，不设缩进的宽度，写入数据成功")
            void givenNotEmptyDocumentThenWriteSuccess() {
                ByteArrayOutputStream temporary = new ByteArrayOutputStream();
                assertThat(temporary.size()).isEqualTo(0);
                XmlUtils.Writer writer = XmlUtils.writer(temporary);
                writer.write(document);
                assertThat(temporary.size()).isGreaterThan(0);
            }

            @Test
            @DisplayName("当给定非空文件，写入配置出错时，抛出异常")
            void givenDocumentWithConfigErrorThenThrowException() throws TransformerConfigurationException {
                ByteArrayOutputStream temporary = new ByteArrayOutputStream();
                assertThat(temporary.size()).isEqualTo(0);
                XmlUtils.Writer writer = XmlUtils.writer(temporary);
                final Document mockedDocument = mock(Document.class);
                try (final MockedStatic<TransformerFactory> mockedStatic = mockStatic(TransformerFactory.class)) {
                    final TransformerFactory factory = mock(TransformerFactory.class);
                    mockedStatic.when(TransformerFactory::newInstance).thenReturn(factory);
                    doThrow(new TransformerConfigurationException()).when(factory)
                            .setFeature(anyString(), anyBoolean());
                    assertThatThrownBy(() -> writer.write(mockedDocument)).isInstanceOf(IllegalStateException.class);
                }
            }

            @Test
            @DisplayName("当给定非空文件，转换出错时，抛出异常")
            void givenDocumentWithTransformErrorThenThrowException() throws TransformerConfigurationException {
                ByteArrayOutputStream temporary = new ByteArrayOutputStream();
                assertThat(temporary.size()).isEqualTo(0);
                XmlUtils.Writer writer = XmlUtils.writer(temporary);
                final Document mockedDocument = mock(Document.class);
                try (final MockedStatic<TransformerFactory> mockedStatic = mockStatic(TransformerFactory.class)) {
                    final TransformerFactory factory = mock(TransformerFactory.class);
                    mockedStatic.when(TransformerFactory::newInstance).thenReturn(factory);
                    when(factory.newTransformer()).thenThrow(new TransformerConfigurationException());
                    assertThatThrownBy(() -> writer.write(mockedDocument)).isInstanceOf(IllegalStateException.class);
                }
            }

            @Test
            @DisplayName("给定非空文件，设置 4 缩进的宽度，写入数据成功")
            void givenNotEmptyDocumentWithFourIndentThenWriteSuccess() throws ParserConfigurationException {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();
                final Element root = doc.createElement("root");
                Element elementChildOne = doc.createElement("person");
                elementChildOne.setAttribute("attr", "personOne");
                root.appendChild(elementChildOne);
                doc.appendChild(root);
                ByteArrayOutputStream temporary = new ByteArrayOutputStream();
                XmlUtils.Writer writer = XmlUtils.writer(temporary);
                final XmlUtils.Writer writerWithIndent = writer.enableIndent().indentWidth(FOUR_BLANK.length());
                writerWithIndent.write(doc);
                final String content = new String(temporary.toByteArray());
                assertThat(content).contains(FOUR_BLANK);
            }
        }
    }

    @Nested
    @DisplayName("测试类：XmlUtils.NodeHolder")
    class NodeHolderTest {
        private final String name = "configuration";
        private final XmlUtils.NodeHolder holder = XmlUtils.holder(name);

        @Nested
        @DisplayName("测试方法：intValue()")
        class TestIntValue {
            @Test
            @DisplayName("给定不可转换的值，抛出异常")
            void givenCanNotTransformValueThenThrowException() {
                XmlUtils.NodeHolder actual = holder.accept(rootNode);
                assertThat(actual.ready()).isTrue();
                assertThat(actual.toString()).isEqualTo(name);
                IllegalStateException exception = catchThrowableOfType(actual::intValue, IllegalStateException.class);
                assertThat(exception).hasMessage(StringUtils.format(
                        "Content of node {0} is not a 32-bit integer. [value={1}]",
                        name,
                        actual.stringValue())).isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("测试方法：require()")
        class TestRequire {
            @Test
            @DisplayName("给定不存在的 Node 值，抛出异常")
            void givenNotExistNodeValueThenThrowException() {
                XmlUtils.NodeHolder actual = holder.accept(rootNode.getChildNodes());
                IllegalStateException exception = catchThrowableOfType(actual::intValue, IllegalStateException.class);
                assertThat(exception).hasMessage(StringUtils.format("Node {0} not found.", name))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("测试方法：children()")
        class TestChildren {
            @Test
            @DisplayName("给定一个 Node 值，获取子节点的信息")
            void givenTheNodeThenGetTheChildrenInfo() {
                NodeList children = holder.accept(rootNode).children();
                assertThat(children).isNotNull();
                assertThat(children.item(0).getNodeName()).isEqualTo("#text");
                assertThat(children.item(1).getNodeName()).isEqualTo("first-level");
                assertThat(children.item(1).getTextContent()).isEqualTo("first-level-1");
                assertThat(children.item(3).getTextContent()).isEqualTo("first-level-2");
                assertThat(children.item(5).getTextContent()).isEqualTo("first-level-3");
            }
        }
    }

    @Nested
    @DisplayName("测试方法：toMap(Document xml)")
    class TestParseXmlToMap {
        @Test
        @DisplayName("给定非空文件，转换成功")
        void givenNotEmptyDocumentThenParseSuccess() throws Exception {
            Map<String, Object> actualMap = XmlUtils.toMap(document);
            assertThat(actualMap).isEqualTo(documentMap);
        }
    }

    @Nested
    @DisplayName("测试方法：toObject(Document xml, Type type)")
    class TestParseXmlToObject {
        @Test
        @DisplayName("给映射转简单类，转换成功")
        void givenEntityDocumentThenParseSuccess() throws Exception {
            Employee actual = XmlUtils.toObject(employee, Employee.class);
            assertThat(actual).isNotNull()
                    .returns("John", Employee::getName)
                    .returns(25, Employee::getAge)
                    .returns("2023-01-04", Employee::getEntryDate);
        }

        @Test
        @DisplayName("给映射转复杂类，转换成功")
        void givenNestedEntityDocumentThenParseSuccess() throws Exception {
            Department actual = XmlUtils.toObject(department, Department.class);
            assertThat(actual).isNotNull().returns("Research", Department::getName);
            assertThat(actual.getEmployees().get(0)).isNotNull()
                    .returns(123456L, Employee::getId)
                    .returns("John", Employee::getName)
                    .returns(25, Employee::getAge)
                    .returns("2023-01-04", Employee::getEntryDate);
            assertThat(actual.getEmployees().get(1)).isNotNull()
                    .returns(234567L, Employee::getId)
                    .returns("David", Employee::getName)
                    .returns(30, Employee::getAge);
        }
    }

    /**
     * 创建一个根据类型属性对节点进行判定的校验器。
     *
     * @param value 表示所需类型属性的值的 {@link String}。
     * @return 表示用以根据类型属性进行判定的谓词的 {@link Predicate}{@code <}{@link Node}{@code >}。
     */
    private Predicate<Node> predicateByClassAttribute(String value) {
        return node -> StringUtils.equals(XmlUtils.getAttributeValue(node, "class"), value);
    }

    /**
     * 表示测试的类型。
     */
    public static class Employee {
        private Long id;
        private String name;
        private Integer age;
        private String entryDate;

        public Long getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public Integer getAge() {
            return this.age;
        }

        public String getEntryDate() {
            return entryDate;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public void setEntryDate(String entryDate) {
            this.entryDate = entryDate;
        }

        @Override
        public String toString() {
            return "Employee{" + "name='" + name + '\'' + ", age=" + age + '}';
        }
    }

    /**
     * 表示测试的类型。
     */
    public static class Department {
        private String name;
        private List<Employee> employees;

        public String getName() {
            return name;
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmployees(List<Employee> employees) {
            this.employees = employees;
        }
    }

    @Nested
    @DisplayName("测试安全设置的启用和禁用效果")
    class XmlSecurityTest {
        @Nested
        @DisplayName("测试 XmlUtils 类的安全设置")
        class XmlUtilsSecurityTest {
            @Test
            @DisplayName("测试 FEATURE_DISALLOW_DOCTYPE_DECL 设置的启用，即禁用 DOCTYPE 声明")
            void testDisallowDoctypeDecl() throws Exception {
                try (InputStream in = getClass().getResourceAsStream("/security-test-file/external.xml")) {
                    assertThatThrownBy(() -> XmlUtils.load(in)).isInstanceOf(IllegalStateException.class)
                            .hasMessage("Failed to parse XML from the input stream.")
                            .getCause()
                            .isInstanceOf(SAXException.class);
                }
            }
        }

        @Nested
        @DisplayName("为安全设置提供对照测试")
        class XmlUtilsSecurityControlledTest {
            @Test
            @DisplayName("测试 FEATURE_DISALLOW_DOCTYPE_DECL 设置的禁用，即启用 DOCTYPE 声明")
            void testDisallowDoctypeDecl() throws Exception {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
                try (InputStream in = getClass().getResourceAsStream("/security-test-file/external.xml")) {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(in);
                    String extractedData = doc.getDocumentElement().getTextContent();
                    assertThat(extractedData).isEqualTo("This is an example of an external entity.");
                }
            }

            @Test
            @DisplayName("测试 FEATURE_ALLOW_EXTERNAL_GENERAL_ENTITIES 设置的启用，即启用外部一般实体加载")
            void testExternalGeneralEntities() throws Exception {
                String txtFilePath = XmlUtilsTest.class.getResource("/security-test-file/external.txt").getFile();
                String maliciousXml =
                        "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE root [\n" + "<!ENTITY ext SYSTEM \"file://"
                                + txtFilePath + "\">\n" + "]>\n" + "<root>&ext;</root>\n";
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
                try (InputStream in = new ByteArrayInputStream(maliciousXml.getBytes(StandardCharsets.UTF_8))) {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(in);
                    String extractedData = doc.getDocumentElement().getTextContent();
                    assertThat(extractedData).isEqualTo("This is an external general entity.");
                }
            }

            @Test
            @DisplayName("测试 FEATURE_ALLOW_LOAD_EXTERNAL_DTD 设置的启用，即启用加载外部 DTD")
            void testLoadExternalDTD() throws Exception {
                String dtdFilePath = XmlUtilsTest.class.getResource("/security-test-file/external.dtd").getFile();
                String maliciousXml =
                        "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE root SYSTEM \"file://" + dtdFilePath + "\">\n"
                                + "<root>&demoEntity;</root>\n";
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
                try (InputStream in = new ByteArrayInputStream(maliciousXml.getBytes(StandardCharsets.UTF_8))) {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(in);
                    String extractedData = doc.getDocumentElement().getTextContent();
                    assertThat(extractedData).isEqualTo("Demo entity content of a DTD file.");
                }
            }
        }
    }
}
