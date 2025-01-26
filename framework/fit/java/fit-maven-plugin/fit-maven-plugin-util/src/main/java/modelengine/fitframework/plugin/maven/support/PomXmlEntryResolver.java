/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import static modelengine.fitframework.plugin.maven.JarMavenCoordinateResolver.MAVEN_ENTRY_PREFIX;

import modelengine.fitframework.plugin.maven.JarEntryResolver;
import modelengine.fitframework.plugin.maven.MavenCoordinate;
import modelengine.fitframework.plugin.maven.exception.FitMavenPluginException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * 对 pom.xml 文件进行解析的解析器。
 *
 * @author 梁济时
 * @since 2020-11-26
 */
public class PomXmlEntryResolver implements JarEntryResolver<MavenCoordinate> {
    /** 表示 {@link PomXmlEntryResolver} 解析器的单例。 */
    public static final PomXmlEntryResolver INSTANCE = new PomXmlEntryResolver();

    @Override
    public boolean is(JarEntry entry) {
        String name = entry.getName();
        return name.startsWith(MAVEN_ENTRY_PREFIX) && name.endsWith("pom.xml");
    }

    @Override
    public MavenCoordinate resolve(InputStream in) {
        Document xml = load(in);
        return MavenCoordinate.builder()
                .setGroupId(valueWithBackup(xml, "project/groupId", "project/parent/groupId"))
                .setArtifactId(valueWithBackup(xml, "project/artifactId", "project/parent/artifactId"))
                .setVersion(valueWithBackup(xml, "project/version", "project/parent/version"))
                .build();
    }

    private static Document load(InputStream in) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ex) {
            throw new FitMavenPluginException("Fail to set features of document builder factory for security.", ex);
        }
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new FitMavenPluginException("Fail to instantiate document builder with factory.", ex);
        }
        try {
            return builder.parse(in);
        } catch (SAXException | IOException ex) {
            throw new FitMavenPluginException("Fail to load pom.xml as a XML document.", ex);
        }
    }

    private static String valueWithBackup(Document xml, String xpath, String backupXpath) {
        String value = value(xml, xpath(xpath));
        if (value == null) {
            value = value(xml, xpath(backupXpath));
        }
        return value;
    }

    private static String[] xpath(String xpath) {
        final char separator = '/';
        List<String> path = new ArrayList<>();
        StringBuilder builder = null;
        for (int i = 0; i < xpath.length(); i++) {
            char ch = xpath.charAt(i);
            if (ch == separator) {
                if (builder != null) {
                    path.add(builder.toString());
                    builder = null;
                }
            } else {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(ch);
            }
        }
        if (builder != null) {
            path.add(builder.toString());
        }
        return path.toArray(new String[0]);
    }

    private static String value(Document document, String... xpath) {
        Node node = document;
        for (int i = 0; i < xpath.length && node != null; i++) {
            node = child(node, xpath[i]);
        }
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }

    private static Node child(Node parent, String tag) {
        NodeList children = parent.getChildNodes();
        if (children == null) {
            return null;
        }
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child != null && tag.equalsIgnoreCase(child.getNodeName())) {
                return child;
            }
        }
        return null;
    }
}
