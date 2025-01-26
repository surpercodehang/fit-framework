# 聊天模型

## 简介

聊天模型是 FEL 的核心组件，其使用聊天消息作为输出，并返回聊天消息作为输出。为方便集成不同模型提供商（openai、qwen等）提供的模型服务，FEL 抽象了一个标准接口来进行交互。

``` java
public interface ChatModel {
    /**
     * 调用聊天模型生成结果。
     *
     * @param prompt 表示提示词的 {@link Prompt}。
     * @param chatOption 表示聊天模型参数的 {@link ChatOption}。
     * @return 表示聊天模型生成结果的 {@link Choir}{@code <}{@link ChatMessage}{@code >}。
     */
    Choir<ChatMessage> generate(Prompt prompt, ChatOption chatOption);
}
```
聊天模型接收提示词以及一个可选参数，返回一个聊天消息流，根据可选参数中 `stream` 的值，返回一个或多个聊天消息。

## 示例

1. 在项目 pom.xml 加入以下依赖：

``` xml
    <dependencies>
        <dependency>
            <groupId>modelengine.fit.starter</groupId>
            <artifactId>fit-starter</artifactId>
            <version>${fit.version}</version>
        </dependency>
        <dependency>
            <groupId>modelengine.fit.starter</groupId>
            <artifactId>fit-plugins-starter-web</artifactId>
            <version>${fit.version}</version>
        </dependency>
        <dependency>
            <groupId>modelengine.fit.plugin</groupId>
            <artifactId>fit-http-client-okhttp</artifactId>
            <version>${fit.version}</version>
        </dependency>
        <dependency>
            <groupId>modelengine.fit.jade.fel</groupId>
            <artifactId>fel-core</artifactId>
            <version>${fel.version}</version>
        </dependency>
        <dependency>
            <groupId>modelengine.fit.jade.community</groupId>
            <artifactId>fel-model-openai-plugin</artifactId>
            <version>${fel.version}</version>
        </dependency>
    </dependencies>
```

2. 在 application.yml 配置文件中加入以下配置：

```yaml
fel:
  openai:
    api-base: '${api-base}'
    api-key: '${your-api-key}'
example:
  model: '${model-name}'
```

3. 添加如下代码：

``` java
@Component
public class ChatModelExampleController {
    private final ChatModel chatModel;
    @Value("${example.model}")
    private String modelName;

    public ChatModelExampleController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public ChatMessage chat(@RequestParam("query") String query) {
        ChatOption option = ChatOption.custom().model(this.modelName).stream(false).build();
        return this.chatModel.generate(ChatMessages.from(new HumanMessage(query)), option).blockAll().get(0);
    }
}
```

4. 让模型返回流式输出：

``` java
    @GetMapping("/chat-stream")
    public Choir<ChatMessage> chatStream(@RequestParam("query") String query) {
        ChatOption option = ChatOption.custom().model(this.modelName).stream(false).build();
        return this.chatModel.generate(ChatMessages.from(new HumanMessage(query)), option);
    }
```

至此完成了聊天模型的接入，返回 Choir 对象，`fit` 框架将自动使用 SSE 格式返回流式输出。

## 验证

在浏览器栏输入：`http://localhost:8080/ai/example/chat?query=告诉我一个笑话`

返回如下响应：

```json
{
  "content": "当然，接下来我将告诉你一个笑话：\n\n为什么袜子总是只丢一只？因为丢两只根本就不会发现。 \n\n希望这能给你带来一些欢笑！如果你还需要其他的笑话或者其他帮助，随时告诉我。",
  "toolCalls": []
}
```

在浏览器栏输入：`http://localhost:8080/ai/example/chat-stream?query=告诉我一个笑话`

返回如下响应：

```plaintext
data:{"content":"","toolCalls":[]}

data:{"content":"当然","toolCalls":[]}

data:{"content":"，","toolCalls":[]}

data:{"content":"接下来","toolCalls":[]}

data:{"content":"是一个","toolCalls":[]}

data:{"content":"轻松","toolCalls":[]}

data:{"content":"的","toolCalls":[]}

data:{"content":"笑话","toolCalls":[]}

data:{"content":"：\n\n","toolCalls":[]}

data:{"content":"为什么","toolCalls":[]}

data:{"content":"袜","toolCalls":[]}

...
```

## 配置项说明

``` java
public interface ChatOption {
    /**
     * 获取调用模型的名字。
     *
     * @return 表示模型名字的 {@link String}。
     */
    @Nonnull
    String model();

    /**
     * 获取是否使用流式接口。
     * <p>
     * 如果设置为 {@code true}，则使用流式接口；否则，使用普通接口。
     * </p>
     *
     * @return 表示是否使用流式接口的 {@code boolean}。
     */
    @Nonnull
    Boolean stream();

    /**
     * 获取模型接口秘钥。
     *
     * @return 表示模型接口秘钥的 {@link String}。
     */
    String apiKey();

    /**
     * 获取生成文本的最大长度。
     *
     * @return 表示生成文本最大长度的 {@link Integer}。
     */
    Integer maxTokens();

    /**
     * 获取频率惩罚系数。
     * <p>
     * 取值范围为 {@code -2.0 - 2.0} 之间的数字，影响模型如何根据已存在文本的频率惩罚新文本。<br/>
     * 正值将通过惩罚已经频繁使用的词来降低模型一行中重复用词的可能性。<br/>
     * 为了稍微减少输出中的重复词语，惩罚系数的合理值通常约为0.1至1。<br/>
     * 如果目标是显著抑制重复，系数可以增加到2，但这可能会对输出的质量产生负面影响。<br/>
     * 相反，使用负值可以增加重复的可能性。<br/>
     * </p>
     *
     * @return 表示频率惩罚系数的 {@link Double}。
     */
    Double frequencyPenalty();

    /**
     * 获取文本出现惩罚系数。
     * <p>
     * 取值范围为 {@code -2.0 - 2.0} 之间的数字，影响模型如何根据到目前为止是否出现在文本中来惩罚新词汇。<br/>
     * 正值将通过惩罚已经使用的词，增加模型谈论新主题的可能性。<br/>
     * 通常 presencePenalty 的默认值为0，当希望使用输入中的单词生成与输入提示一致的文本时，使用该值。<br/>
     * 另一方面，如果希望模型不受输入限制，那么可以使用更大的存在惩罚，这将鼓励模型生成输入中不存在的新词，
     * 从而允许更多样化和创造性的输出。<br/>
     * frequency_penalty 和 presence_penalty 的不同点在于 frequency_penalty 的惩罚会随着 token 出现的次数增加而不断加强，
     * 而 presence_penalty 则只会区分是否出现。
     * </p>
     *
     * @return 表示文本出现惩罚系数的 {@link Double}。
     */
    Double presencePenalty();

    /**
     * 获取停止字符串列表。
     * <p>
     * 当模型输出中存在定义的字符串后，将会停止生成。
     * </p>
     *
     * @return 表示停止字符串列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> stop();

    /**
     * 获取采样温度。
     * <p>
     * 取值范围为 {@code 0.0 - 2.0} 之间的数字，控制生成的文本的多样性和随机性。<br/>
     * 如果 temperature 值较高，则更倾向于生成随机的文本；如果值较低，则更倾向于生成相对可预测的文本。
     * </p>
     *
     * @return 表示采样温度的 {@link Double}。
     */
    Double temperature();

    /**
     * 获取采样率。
     * <p>
     * 取值范围为 {@code 0.0 - 1.0} 之间的数字，表示模型考虑具有 top_p 概率质量的令牌的结果，与 temperature 不建议同时设置。
     * 例如： 0.1 意味着只考虑包含前 10% 概率质量的标记。
     * </p>
     *
     * @return 表示采样率的 {@link Double}。
     */
    Double topP();

    /**
     * 获取模型能使用的工具列表。
     *
     * @return 表示模型能使用的工具列表的 {@link List}{@code <}{@link ToolInfo}{@code >}。
     */
    List<ToolInfo> tools();
}
```
