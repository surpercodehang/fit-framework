FEL 中内置了 6 个 RAG 相关的 Llamaindex 工具，他们分别是`SimilarityPostprocessor`, `SentenceEmbeddingOptimizer`, `LLMRerank`，`LongContextReorder`，`LLMSingleSelector`和`LLMMultiSelector`。

## 插件编写

与`Langchain`不同，`Llamaindex` 提供的内置插件并不是`Runnable`，所以我们提供了一些函数来把他们包装成可调用的`Runnable`。

## Llamaindex 插件

``` python
# 省略 import

@unique
class SelectorMode(Enum):
    SINGLE = "single"
    MULTI = "multi"

def llm_choice_selector(choice: List[str], query_str: str, **kwargs) -> List[SingleSelection]:
    """LLM-based selector that chooses one or multiple out of many options."""
    if len(choice) == 0:
        return []
    api_key = kwargs.get("api_key") or "EMPTY"
    model_name = kwargs.get("model_name")
    api_base = kwargs.get("api_base")
    prompt = kwargs.get("prompt")
    mode = str(kwargs.get("mode") or SelectorMode.SINGLE.value)

    if mode.lower() not in [m.value for m in SelectorMode]:
        raise ValueError(f"Invalid mode {mode}.")
    llm = OpenAI(model_name=model_name, api_base=api_base, api_key=api_key)

    if mode.lower() == SelectorMode.SINGLE.value:
        selector_prompt = prompt or DEFAULT_SINGLE_SELECT_PROMPT_TMPL
        selector = LLMSingleSelector.from_defaults(llm=llm, prompt_template_str=selector_prompt)
    else:
        multi_selector_prompt = prompt or DEFAULT_MULTI_SELECT_PROMPT_TMPL
        selector = LLMMultiSelector.from_defaults(llm=llm, prompt_template_str=multi_selector_prompt)
    try:
        return selector.select(choice, query_str).selections
    except BaseException:
        fit_logger.error("Invoke choice selector failed.")
        traceback.print_exc()
        return []
```

注册插件

``` python
tool = (llm_choice_selector, ["model_name", "api_key", "api_base", "prompt", "mode"], "The selected choice.")

func = tool[0]
generic_id = "llama_index.rag.toolkit"
fitable_id = f"{func.__name__}"

tool_invoke = functools.partial(__invoke_tool, tool_func=func)
tool_invoke.__module__ = register_llama_toolkit.__module__
tool_invoke.__annotations__ = {
    'input_args': dict,
    'return': signature(func).return_annotation
}
register_fitable(generic_id, fitable_id, False, [], tool_invoke)
```

调用插件

```bash
curl --location --request POST 'http://localhost:9666/fit/llama_index.rag.toolkit/llm_choice_selector' \
--header 'FIT-Data-Format: 1' \
--header 'FIT-Genericable-Version: 1.0.0' \
--header 'Content-Type: application/json' \
--data-raw '[
    {
        "api_key": "EMPTY",
        "model_name": "替换成api_base对应的model_name",
        "api_base": "替换成可用的api_base",
        "prompt": "",
        "query_str": "dog",
        "choice": [
            "I found a lovely puppy and brought him home.",
            "I lost my wallet yesterday.",
            "I have a dream",
            "Bob is a dog."
        ]
    }
]'
```

调用结果

```bash
[
    {
        "index": 0,
        "reason": "The choice mentions a puppy, which is a type of dog."
    },
    {
        "index": 3,
        "reason": "This choice explicitly states that Bob is a dog."
    }
]
```

## 插件打包

除了插件源码，还需要提供工具元数据信息`tools.json`参考如下。

```json
{
	"tools": [
		{
			"runnables": {
				"LlamaIndex": {
					"genericableId": "llama_index.rag.toolkit",
					"fitableId": "llm_choice_selector"
				}
			},
			"schema": {
				"name": "llm_choice_selector",
				"description": "LLM-based selector that chooses one or multiple out of many options.",
				"parameters": {
					"type": "object",
					"properties": {
						"choice": {
							"title": "Choice",
							"type": "array",
							"items": {
								"type": "string"
							}
						},
						"query_str": {
							"title": "Query Str",
							"type": "string"
						},
						"model_name": {
							"type": "string",
							"description": "model_name"
						},
						"api_key": {
							"type": "string",
							"description": "api_key"
						},
						"api_base": {
							"type": "string",
							"description": "api_base"
						},
						"prompt": {
							"type": "string",
							"description": "prompt"
						},
						"mode": {
							"type": "string",
							"description": "mode"
						}
					},
					"required": [
						"choice",
						"query_str"
					]
				},
				"return": {
					"title": "The Selected Choice.",
					"type": "array",
					"items": {
						"title": "SingleSelection",
						"description": "A single selection of a choice.",
						"type": "object",
						"properties": {
							"index": {
								"title": "Index",
								"type": "integer"
							},
							"reason": {
								"title": "Reason",
								"type": "string"
							}
						},
						"required": [
							"index",
							"reason"
						]
					}
				},
				"parameterExtensions": {
					"config": [
						"model_name",
						"api_key",
						"api_base",
						"prompt",
						"mode"
					]
				}
			}
		}
	]
}
```
