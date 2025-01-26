<div style="text-align: center;"><span style="font-size: 40px"><b>Maven插件</b></span></div>

[TOC]

FIT通过Maven插件提供FIT插件、应用的打包、发布等能力。

# [fit-build-maven-plugin](fit-build-maven-plugin/README.md)

提供FIT插件、应用的打包能力。

例如，通过以下配置，可构建`AllInOne`模式的FIT插件，该FIT插件的名称为`my-plugin`，是加载级别为`3`的系统插件。

``` xml
<plugin>
    <groupId>modelengine.fitframework</groupId>
    <artifactId>fit-build-maven-plugin</artifactId>
    <version>${fit.version}</version>
    <executions>
        <execution>build</execution>
        <goals>
            <goal>build-plugin</goal>
        </goals>
        <configuration>
            <metadata>
                <name>my-plugin</name>
                <category>system</category>
                <level>3</level>
            </metadata>
            <packaging>AllInOne</packaging>
            <outputDirectory>../plugins/</outputDirectory>
        </configuration>
    </executions>
</plugin>
```

通过以下配置，可构建`AllInOne`模式的FIT应用。

``` xml
<plugin>
    <groupId>modelengine.fitframework</groupId>
    <artifactId>fit-build-maven-plugin</artifactId>
    <version>${fit.version}</version>
    <executions>
        <execution>build</execution>
        <goals>
            <goal>build-app</goal>
        </goals>
        <configuration>
            <packaging>AllInOne</packaging>
            <outputDirectory>../target/</outputDirectory>
            <plugins>
                <plugin>
                    <group>fit.hakuna</group>
                    <name>grpc-client</name>
                    <version>1.0.0</version>
                </plugin>
                <plugin>
                    <group>fit.hakuna</group>
                    <name>grpc-server</name>
                    <version>1.0.0</version>
                </plugin>
                <plugin>
                    <group>fit.hakuna</group>
                    <name>http-server</name>
                    <version>1.0.0</version>
                </plugin>
                <plugin>
                    <group>fit.hakuna</group>
                    <name>rest-gateway</name>
                    <version>1.0.0</version>
                </plugin>
            </plugins>
        </configuration>
    </executions>
</plugin>
```