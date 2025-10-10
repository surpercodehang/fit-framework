/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.community.model.openai;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notEmpty;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.community.model.openai.api.OpenAiApi;
import modelengine.fel.community.model.openai.config.OpenAiConfig;
import modelengine.fel.community.model.openai.entity.chat.OpenAiChatCompletionRequest;
import modelengine.fel.community.model.openai.entity.chat.OpenAiChatCompletionResponse;
import modelengine.fel.community.model.openai.entity.embed.OpenAiEmbedding;
import modelengine.fel.community.model.openai.entity.embed.OpenAiEmbeddingRequest;
import modelengine.fel.community.model.openai.entity.embed.OpenAiEmbeddingResponse;
import modelengine.fel.community.model.openai.entity.image.OpenAiImageRequest;
import modelengine.fel.community.model.openai.entity.image.OpenAiImageResponse;
import modelengine.fel.community.model.openai.entity.rerank.OpenAiRerankRequest;
import modelengine.fel.community.model.openai.entity.rerank.OpenAiRerankResponse;
import modelengine.fel.community.model.openai.enums.ModelProcessingState;
import modelengine.fel.community.model.openai.util.HttpUtils;
import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fel.core.embed.EmbedModel;
import modelengine.fel.core.embed.EmbedOption;
import modelengine.fel.core.embed.Embedding;
import modelengine.fel.core.image.ImageModel;
import modelengine.fel.core.image.ImageOption;
import modelengine.fel.core.model.http.SecureConfig;
import modelengine.fel.core.rerank.RerankModel;
import modelengine.fel.core.rerank.RerankOption;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.security.Decryptor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 表示 openai 模型服务。
 *
 * @author 易文渊
 * @since 2024-08-07
 */
@Component
public class OpenAiModel implements EmbedModel, ChatModel, ImageModel, RerankModel {
    private static final Logger log = Logger.get(OpenAiModel.class);
    private static final Map<String, Boolean> HTTPS_CONFIG_KEY_MAPS = MapBuilder.<String, Boolean>get()
            .put("client.http.secure.ignore-trust", Boolean.FALSE)
            .put("client.http.secure.ignore-hostname", Boolean.FALSE)
            .put("client.http.secure.trust-store-file", Boolean.FALSE)
            .put("client.http.secure.trust-store-password", Boolean.TRUE)
            .put("client.http.secure.key-store-file", Boolean.FALSE)
            .put("client.http.secure.key-store-password", Boolean.TRUE)
            .build();
    private static final String RESPONSE_TEMPLATE = "<think>{0}<//think>{1}";

    private final HttpClassicClientFactory httpClientFactory;
    private final HttpClassicClientFactory.Config clientConfig;
    private final String baseUrl;
    private final String defaultApiKey;
    private final ObjectSerializer serializer;
    private final Config config;
    private final Decryptor decryptor;
    private final LazyLoader<HttpClassicClient> httpClient;

    /**
     * 创建 openai 嵌入模型服务的实例。
     *
     * @param httpClientFactory 表示 http 客户端工厂的 {@link HttpClassicClientFactory}。
     * @param clientConfig 表示 openai http 配置的 {@link OpenAiConfig}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param config 表示配置信息的 {@link Config}。
     * @param container 表示 bean 容器的 {@link BeanContainer}。
     * @throws IllegalArgumentException 当 {@code httpClientFactory}、{@code config} 为 {@code null} 时。
     */
    public OpenAiModel(HttpClassicClientFactory httpClientFactory, OpenAiConfig clientConfig,
            @Fit(alias = "json") ObjectSerializer serializer, Config config, BeanContainer container) {
        notNull(clientConfig, "The config cannot be null.");
        this.httpClientFactory = notNull(httpClientFactory, "The http client factory cannot be null.");
        this.clientConfig = HttpClassicClientFactory.Config.builder()
                .connectTimeout(clientConfig.getConnectTimeout())
                .socketTimeout(clientConfig.getReadTimeout())
                .build();
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.baseUrl = clientConfig.getApiBase();
        this.defaultApiKey = clientConfig.getApiKey();
        this.httpClient = new LazyLoader<>(this::getHttpClient);
        this.config = config;
        this.decryptor = container.lookup(Decryptor.class)
                .map(BeanFactory::<Decryptor>get)
                .orElseGet(() -> encrypted -> encrypted);
    }

    @Override
    public List<Embedding> generate(List<String> inputs, EmbedOption option) {
        notEmpty(inputs, "The input cannot be empty.");
        notNull(option, "The embed option cannot be null.");
        notBlank(option.model(), "The embed model name cannot be null.");
        HttpClassicClientRequest request = this.httpClient.get()
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(this.baseUrl, OpenAiApi.EMBEDDING_ENDPOINT));
        String actualApiKey = StringUtils.blankIf(option.apiKey(), this.defaultApiKey);
        if (StringUtils.isNotBlank(actualApiKey)) {
            HttpUtils.setBearerAuth(request, actualApiKey);
        }
        request.jsonEntity(new OpenAiEmbeddingRequest(inputs, option.model()));
        Class<OpenAiEmbeddingResponse> clazz = OpenAiEmbeddingResponse.class;
        try (HttpClassicClientResponse<OpenAiEmbeddingResponse> response = request.exchange(clazz)) {
            return response.objectEntity()
                    .map(entity -> CollectionUtils.<Embedding, OpenAiEmbedding>asParent(entity.object().data()))
                    .orElseThrow(() -> new FitException("The response body is abnormal."));
        } catch (IOException e) {
            throw new FitException(e);
        }
    }

    @Override
    public Choir<ChatMessage> generate(Prompt prompt, ChatOption chatOption) {
        notNull(prompt, "The prompt cannot be null.");
        notNull(chatOption, "The chat option cannot be null.");
        String modelSource = StringUtils.blankIf(chatOption.baseUrl(), this.baseUrl);
        HttpClassicClientRequest request = this.getHttpClient(chatOption.secureConfig())
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(modelSource, OpenAiApi.CHAT_ENDPOINT));
        String actualApiKey = StringUtils.blankIf(chatOption.apiKey(), this.defaultApiKey);
        if (StringUtils.isNotBlank(actualApiKey)) {
            HttpUtils.setBearerAuth(request, actualApiKey);
        }
        request.jsonEntity(new OpenAiChatCompletionRequest(prompt, chatOption));
        return chatOption.stream() ? this.createChatStream(request) : this.createChatCompletion(request);
    }

    @Override
    public List<Media> generate(String prompt, ImageOption option) {
        notNull(prompt, "The prompt cannot be null.");
        notNull(option, "The image option cannot be null.");
        String modelSource = StringUtils.blankIf(option.baseUrl(), this.baseUrl);
        HttpClassicClientRequest request = this.httpClient.get()
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(modelSource, OpenAiApi.IMAGE_ENDPOINT));
        String actualApiKey = StringUtils.blankIf(option.apiKey(), this.defaultApiKey);
        if (StringUtils.isNotBlank(actualApiKey)) {
            HttpUtils.setBearerAuth(request, actualApiKey);
        }
        request.jsonEntity(new OpenAiImageRequest(option.model(), option.size(), prompt));
        Class<OpenAiImageResponse> clazz = OpenAiImageResponse.class;
        try (HttpClassicClientResponse<OpenAiImageResponse> response = request.exchange(clazz)) {
            return response.objectEntity()
                    .map(entity -> entity.object().media())
                    .orElseThrow(() -> new FitException("The response body is abnormal."));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close response.", e);
        }
    }

    @Override
    public List<MeasurableDocument> generate(List<MeasurableDocument> documents, RerankOption rerankOption) {
        notEmpty(documents, "The documents cannot be empty.");
        notNull(rerankOption, "The rerank option cannot be null.");
        String modelSource = StringUtils.blankIf(rerankOption.baseUri(), this.baseUrl);
        HttpClassicClientRequest request = this.getHttpClient(rerankOption.secureConfig())
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(modelSource, OpenAiApi.RERANK_ENDPOINT));
        String actualApiKey = StringUtils.blankIf(rerankOption.apiKey(), this.defaultApiKey);
        if (StringUtils.isNotBlank(actualApiKey)) {
            HttpUtils.setBearerAuth(request, actualApiKey);
        }
        List<String> docs = documents.stream().map(MeasurableDocument::text).collect(Collectors.toList());
        OpenAiRerankRequest fields = new OpenAiRerankRequest(rerankOption, docs);
        request.entity(Entity.createObject(request, fields));
        OpenAiRerankResponse rerankResponse = this.rerankExchange(request);

        return rerankResponse.results()
                .stream()
                .map(result -> new MeasurableDocument(documents.get(result.index()), result.relevanceScore()))
                .sorted((document1, document2) -> (int) (document2.score() - document1.score()))
                .collect(Collectors.toList());
    }

    private OpenAiRerankResponse rerankExchange(HttpClassicClientRequest request) {
        try (HttpClassicClientResponse<Object> response = request.exchange(OpenAiRerankResponse.class)) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                log.error("Failed to get rerank model response. [code={}, reason={}]",
                        response.statusCode(),
                        response.reasonPhrase());
                throw new FitException("Failed to get rerank model response.");
            }
            return ObjectUtils.cast(response.objectEntity()
                    .map(ObjectEntity::object)
                    .orElseThrow(() -> new FitException("The response body is abnormal.")));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to request rerank model.", e);
        }
    }

    private Choir<ChatMessage> createChatStream(HttpClassicClientRequest request) {
        AtomicReference<ModelProcessingState> modelProcessingState =
                new AtomicReference<>(ModelProcessingState.INITIAL);
        return request.<String>exchangeStream(String.class)
                .filter(str -> !StringUtils.equals(str, "[DONE]"))
                .map(str -> this.serializer.<OpenAiChatCompletionResponse>deserialize(str,
                        OpenAiChatCompletionResponse.class))
                .map(response -> getChatMessage(response, modelProcessingState));
    }

    private ChatMessage getChatMessage(OpenAiChatCompletionResponse response,
            AtomicReference<ModelProcessingState> state) {
        // 适配reasoning_content格式返回的模型推理内容，模型生成内容顺序为先reasoning_content后content
        // 在第一个reasoning_content chunk之前增加<think>标签，并且在第一个content chunk之前增加</think>标签
        if (state.get() == ModelProcessingState.INITIAL && StringUtils.isNotEmpty(response.reasoningContent().text())) {
            String text = "<think>" + response.reasoningContent().text();
            state.set(ModelProcessingState.THINKING);
            return new AiMessage(text, response.message().toolCalls());
        }
        if (state.get() == ModelProcessingState.THINKING && StringUtils.isNotEmpty(response.message().text())) {
            String text = "</think>" + response.message().text();
            state.set(ModelProcessingState.RESPONDING);
            return new AiMessage(text, response.message().toolCalls());
        }
        if (state.get() == ModelProcessingState.THINKING) {
            return new AiMessage(response.reasoningContent().text(), response.message().toolCalls());
        }
        return response.message();
    }

    private Choir<ChatMessage> createChatCompletion(HttpClassicClientRequest request) {
        try (HttpClassicClientResponse<OpenAiChatCompletionResponse> response = request.exchange(
                OpenAiChatCompletionResponse.class)) {
            OpenAiChatCompletionResponse chatCompletionResponse = response.objectEntity()
                    .map(ObjectEntity::object)
                    .orElseThrow(() -> new FitException("The response body is abnormal."));
            String finalMessage = chatCompletionResponse.message().text();
            if (StringUtils.isNotBlank(chatCompletionResponse.reasoningContent().text())) {
                finalMessage = StringUtils.format(RESPONSE_TEMPLATE,
                        chatCompletionResponse.reasoningContent().text(),
                        finalMessage);
            }
            return Choir.just(new AiMessage(finalMessage, chatCompletionResponse.message().toolCalls()));
        } catch (IOException e) {
            throw new FitException(e);
        }
    }

    private HttpClassicClient getHttpClient() {
       Map<String, Object> custom = HTTPS_CONFIG_KEY_MAPS.keySet()
               .stream()
               .filter(sslKey -> this.config.keys().contains(Config.canonicalizeKey(sslKey)))
               .collect(Collectors.toMap(sslKey -> sslKey, sslKey -> {
                   Object value = this.config.get(sslKey, Object.class);
                   if (HTTPS_CONFIG_KEY_MAPS.get(sslKey)) {
                       value = this.decryptor.decrypt(ObjectUtils.cast(value));
                   }
                   return value;
               }));

        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder()
                .socketTimeout(this.clientConfig.socketTimeout())
                .connectTimeout(this.clientConfig.connectTimeout())
                .custom(custom)
                .build());
    }

    private HttpClassicClient getHttpClient(SecureConfig secureConfig) {
        if (secureConfig == null) {
            return getHttpClient();
        }

        Map<String, Object> custom = buildHttpsConfig(secureConfig);
        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder()
                .socketTimeout(this.clientConfig.socketTimeout())
                .connectTimeout(this.clientConfig.connectTimeout())
                .custom(custom)
                .build());
    }

    private Map<String, Object> buildHttpsConfig(SecureConfig secureConfig) {
        Map<String, Object> result = new HashMap<>();
        putConfigIfNotNull(secureConfig.ignoreTrust(), "client.http.secure.ignore-trust", result);
        putConfigIfNotNull(secureConfig.ignoreHostName(), "client.http.secure.ignore-hostname", result);
        putConfigIfNotNull(secureConfig.trustStoreFile(), "client.http.secure.trust-store-file", result);
        putConfigIfNotNull(secureConfig.trustStorePassword(), "client.http.secure.trust-store-password", result);
        putConfigIfNotNull(secureConfig.keyStoreFile(), "client.http.secure.key-store-file", result);
        putConfigIfNotNull(secureConfig.keyStorePassword(), "client.http.secure.key-store-password", result);
        return result;
    }

    private static void putConfigIfNotNull(Object value, String key, Map<String, Object> result) {
        if (value != null) {
            result.put(key, value);
        }
    }
}