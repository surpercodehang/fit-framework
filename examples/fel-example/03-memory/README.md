# Memory

## 简介

大语言模型提供无状态推理接口，其基于用户传入的上下文进行推理，然而一个更复杂的系统需要有一个不断更新的世界模型，这允许它做一些事情，比如维护实体及其关系的信息。我们把这种储存过去互动信息的能力称为“记忆”。在
FEL 中，我们使用 `Memory` 保存对话的历史记录，包括先前的输入和输出，这样模型可以根据之前的对话内容生成更相关的回应。

``` java
public interface Memory {
    /**
     * 插入一条历史记录。
     *
     * @param message 表示问题描述的 {@link ChatMessage}。
     */
    void add(ChatMessage message);

    /**
     * 设置历史记录。
     *
     * @param messages 表示历史记录的 {@link List}{@code <}{@link ChatMessage}{@code >}。
     */
    void set(List<ChatMessage> messages);

    /**
     * 清空历史记录。
     */
    void clear();

    /**
     * 获取历史记录的问答对列表。
     *
     * @return 表示问答对列表的 {@link List}{@code <}{@link ChatMessage}{@code >}。
     */
    List<ChatMessage> messages();

    /**
     * 获取历史记录格式化的文本。
     *
     * @return 表示历史记录文本的 {@link String}。
     */
    String text();
}
```

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
controller:

@Component
@RequestMapping("/ai/example")
public class ChatMemoryExampleController {
    private final ChatModel chatModel;
    private final Memory memory = new CacheMemory();
    
    @Value("${example.model}")
    private String modelName;

    public ChatMemoryExampleController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public ChatMessage chat(@RequestParam("query") String query) {
        ChatOption option = ChatOption.custom().model(this.modelName).stream(false).build();
        this.memory.add(new HumanMessage(query));
        ChatMessage aiMessage =
                this.chatModel.generate(ChatMessages.from(this.memory.messages()), option).first().block().get();
        this.memory.add(aiMessage);
        return aiMessage;
    }
}
```

在例子中，我们使用基于内存的记忆保存与大模型交互的历史记录，并在用户询问前从记忆中取出，使得大模型具备了多轮对话的能力。

## 验证

1. 浏览器栏输入 `http://localhost:8080/ai/example/chat?query=什么是大模型`

```json
{
  "content": "大模型通常指的是拥有大量参数的机器学习模型，如大规模的神经网络模型。以通义千问为例，这是一个基于Transformer架构的超大规模语言模型，拥有千亿级别的参数量。大模型通常需要大量的计算资源和数据进行训练，但能够学习到更复杂、更丰富的特征表示，从而在各种自然语言处理任务上表现出更强大的性能和泛化能力。大模型的发展和应用，已成为推动人工智能技术进步和应用落地的重要驱动力。",
  "toolCalls": []
}
```

2. 再输入 `http://localhost:8080/ai/example/chat?query=他与普通的模型有什么区别`

```json
{
  "content": "大模型与普通模型的主要区别在于模型的规模和复杂度。以下是大模型与普通模型的一些主要区别：\n\n1. 参数量：大模型通常拥有更多的参数，可以达到数百万、数十亿甚至更多。相比之下，普通模型的参数量通常较小，可能只有几千、几万或几十万。\n\n2. 训练数据：大模型需要更多的训练数据来优化其参数，以获得更好的性能和泛化能力。相比之下，普通模型可能只需要较小的数据集进行训练。\n\n3. 计算资源：大模型需要更多的计算资源和时间来进行训练和推理。相比之下，普通模型可能只需要较小的计算资源和时间。\n\n4. 表示能力：大模型通常具有更强的表示能力，可以学习到更复杂、更抽象的特征表示，从而在各种自然语言处理任务上表现出更强大的性能和泛化能力。相比之下，普通模型的表示能力可能相对较弱。\n\n5. 应用场景：大模型通常适用于大规模的自然语言处理任务，如机器翻译、文本生成、问答系统等。相比之下，普通模型可能更适合于小规模或特定领域的自然语言处理任务。\n\n综上所述，大模型与普通模型的主要区别在于模型的规模、复杂度、表示能力、训练数据、计算资源和应用场景等方面。",
  "toolCalls": []
}
```

可以发现，大模型在第二轮问答中，正确的将主语替换为大模型。