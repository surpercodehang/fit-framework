FEL 中内置了 4 个数据库相关的 `Langchain` 工具，他们本别是 `InfoSQLDatabaseTool`, `ListSQLDatabaseTool`, `QuerySQLCheckerTool` 和 `QuerySQLDataBaseTool`。用户也可以将自己使用 `Langchain` 编写的一些算子接入 FIT 变成一个可以调用的 fitable。

## 插件编写

Python 中的插件以 `Runnable` 的形式被注册和调用，而 `Langchain` 内置的工具、接入的模型和编写的算子都是 `Runable`，所以可以很方便的包装成 `Fitable`。

## Langchain 插件

``` python
# 省略 import 和加载数据库的函数实现

def langchain_sql_list(kwargs: dict) -> BaseTool:
    db = get_db(kwargs.get("sql_url"), kwargs.get("sql_table"), kwargs.get("sql_name"), kwargs.get("sql_pwd"))

    list_sql_database_tool = ListSQLDatabaseTool(db=db)
    return list_sql_database_tool

register_api_tools(langchain_sql_list, ["sql_url", "sql_table", "sql_name", "sql_pwd"], "sql_db_list_tables")
```

这个函数的入参是一个包含了所需入参的字典，输出是一个 `Langchain` 的 `BaseTool`。`register_api_tools` 的三个入参分别是插件函数，入参名字列表，以及注册的 fitable_id。接下来把这个 `.py` 文件打包成 `.tar` 格式的压缩包后，将压缩包放在 `\fit\python\custom_dynamic_plugins` 里，就可以动态添加这个 `Langchain` 插件。

我通过如下命令调用`sql_db_list_tables`，来获取一个本地数据库里`chinook`的表名。

```bash
curl --location --request POST 'http://localhost:9666/fit/langchain.tool/sql_db_list_tables' \
--header 'FIT-Data-Format: 1' \
--header 'FIT-Genericable-Version: 1.0.0' \
--header 'Content-Type: text/plain' \
--data-raw '[{
    "sql_url": "localhost:5432",
    "sql_table": "chinook",
    "sql_name": "postgres",
    "sql_pwd": "postgres"
}]'
```

```bash
"album, artist, customer, employee, genre, invoice, invoice_line, media_type, playlist, playlist_track, track"
```

如果用户不希望把算子直接注册成插件，也可以参考 `Llamaindex` 插件的接入方式。

## 插件打包

除了插件源码，还需要提供工具元数据信息`tools.json`参考如下。

```json
{
	"tools": [
		{
            "tags": [
                "Langchain",
                "Config"
            ],
            "runnables": {
                "langchain": {
                    "genericableId": "langchain.tool",
                    "fitableId": "sql_db_list_tables"
                }
            },
            "schema": {
                "name": "sql_db_list_tables",
                "description": "Input is an empty string, output is a comma-separated list of tables in the database.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "tool_input": {
                            "description": "An empty string",
                            "default": "",
                            "type": "string"
                        },
                        "sql_url": {
							"type": "string",
							"description": "sql_url"
						},
                        "sql_table": {
							"type": "string",
							"description": "sql_table"
						},
                        "sql_name": {
							"type": "string",
							"description": "sql_name"
						},
                        "sql_pwd": {
							"type": "string",
							"description": "sql_pwd"
						}
                    },
                    "required": [
                        "sql_table",
                        "sql_url",
                        "sql_name",
                        "sql_pwd"
                    ]
                },
                "return": {
                    "type": "string"
                },
                "parameterExtensions": {
                    "config": [
                        "sql_url",
                        "sql_table",
                        "sql_name",
                        "sql_pwd"
                    ]
                }
            }
        }
	]
}
```

## 接入示例

下面我们用一个很简单的例子来展示怎么把 `Langchain` 已经开发的链路快速简单的放进一个插件。下面使用 `Langchian` 编写了一个基于大模型翻译文本的链路，输入是目标语言和需要翻译的文本，输出是对象语言的文本。

``` python
import getpass
import os

from langchain_community.agent_toolkits import create_sql_agent
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI
```

``` python
os.environ["OPENAI_API_KEY"] = getpass.getpass()
model = ChatOpenAI(model="gpt-4")

system_template = "Translate the following into {language}:"
prompt_template = ChatPromptTemplate.from_messages(
    [("system", system_template), ("user", "{text}")]
)

chain = prompt_template | llm | StrOutputParser()

chain.invoke({"language": "french", "text": "hi"})
```

可以看到我们基本保留了全部的 `Langchain` 代码，只有在构建模型的时候使用了外部输入的参数。

``` python
def translator(kwargs) -> RunnableSerializable[str, str]:
    system_template = "Translate the following into {language}:"
    prompt_template = ChatPromptTemplate.from_messages(
        [("system", system_template), ("user", "{text}")]
    )

    api_key = kwargs.get("api_key") or "EMPTY"
    model_name = kwargs.get("model_name")
    temperature = kwargs.get("temperature") or 0

    llm = ChatOpenAI(model_name=model_name, openai_api_key=api_key, temperature=temperature)

    chain = prompt_template | model | StrOutputParser()
    return chain

# 将工具注册到后端
register_api_tools(
    translator, 
    ["language", "text", "model_name", "api_key", "api_base", "temperature"],
    "translator"
)
```

下面是这个插件的 `JSON Schema` ：

```json
{
    "tags": [
        "Pileline",
    ],
    "runnables": {
        "langchain": {
            "genericableId": "langchain.pipeline",
            "fitableId": "translator"
        }
    },
    "schema": {
        "name": "translator",
        "description": "Input is target language string and content string, output is the translated content in larget language.",
        "parameters": {
            "type": "object",
            "properties": {
                "language": {
                    "description": "An empty string",
                    "default": "English",
                    "type": "string"
                },
                "text": {
                    "type": "string",
                    "description": "text"
                }
                "model_name": {
                    "type": "string",
                    "description": "model_name"
                },
                "api_key": {
                    "type": "string",
                    "description": "api_key"
                },
                "temperature": {
                    "type": "string",
                    "description": "temperature"
                },
            "required": [
                "text"
                "model_name",
                "api_key",
                "temperature"
            ]
        },
        "return": {
            "type": "string"
        },
        "parameterExtensions": {
            "config": [
                "text",
                "language",
                "model_name",
                "api_key",
                "temperature"
            ]
        }
    }
}

```
