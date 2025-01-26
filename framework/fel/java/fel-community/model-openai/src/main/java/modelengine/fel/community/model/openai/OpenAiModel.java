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
import modelengine.fel.community.model.openai.util.HttpUtils;
import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.embed.EmbedModel;
import modelengine.fel.core.embed.EmbedOption;
import modelengine.fel.core.embed.Embedding;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * 表示 openai 模型服务。
 *
 * @author 易文渊
 * @since 2024-08-07
 */
@Component
public class OpenAiModel implements EmbedModel, ChatModel {
    private final HttpClassicClientFactory httpClientFactory;
    private final HttpClassicClientFactory.Config config;
    private final String baseUrl;
    private final String defaultApiKey;
    private final ObjectSerializer serializer;

    /**
     * 创建 openai 嵌入模型服务的实例。
     *
     * @param httpClientFactory 表示 http 客户端工厂的 {@link HttpClassicClientFactory}。
     * @param config 表示 openai http 配置的 {@link OpenAiConfig}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @throws IllegalArgumentException 当 {@code httpClientFactory}、{@code config} 为 {@code null} 时。
     */
    public OpenAiModel(HttpClassicClientFactory httpClientFactory, OpenAiConfig config, ObjectSerializer serializer) {
        notNull(config, "The config cannot be null.");
        this.httpClientFactory = notNull(httpClientFactory, "The http client factory cannot be null.");
        this.config = HttpClassicClientFactory.Config.builder()
                .connectTimeout(config.getConnectTimeout())
                .socketTimeout(config.getReadTimeout())
                .build();
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.baseUrl = config.getApiBase();
        this.defaultApiKey = config.getApiKey();
    }

    @Override
    public List<Embedding> generate(List<String> inputs, EmbedOption option) {
        notEmpty(inputs, "The input cannot be empty.");
        notNull(option, "The embed option cannot be null.");
        notBlank(option.model(), "The embed model name cannot be null.");
        HttpClassicClientRequest request = this.httpClientFactory.create(this.config)
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(this.baseUrl, OpenAiApi.EMBEDDING_ENDPOINT));
        HttpUtils.setBearerAuth(request, StringUtils.blankIf(option.apiKey(), this.defaultApiKey));
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
        HttpClassicClientRequest request = this.httpClientFactory.create(this.config)
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(this.baseUrl, OpenAiApi.CHAT_ENDPOINT));
        HttpUtils.setBearerAuth(request, StringUtils.blankIf(chatOption.apiKey(), this.defaultApiKey));
        request.jsonEntity(new OpenAiChatCompletionRequest(prompt, chatOption));
        return chatOption.stream() ? this.createChatStream(request) : this.createChatCompletion(request);
    }

    private Choir<ChatMessage> createChatStream(HttpClassicClientRequest request) {
        return request.<String>exchangeStream(String.class)
                .filter(str -> !StringUtils.equals(str, "[DONE]"))
                .map(str -> this.serializer.<OpenAiChatCompletionResponse>deserialize(str,
                        OpenAiChatCompletionResponse.class))
                .map(OpenAiChatCompletionResponse::message);
    }

    private Choir<ChatMessage> createChatCompletion(HttpClassicClientRequest request) {
        try (HttpClassicClientResponse<OpenAiChatCompletionResponse> response = request.exchange(
                OpenAiChatCompletionResponse.class)) {
            OpenAiChatCompletionResponse chatCompletionResponse = response.objectEntity()
                    .map(ObjectEntity::object)
                    .orElseThrow(() -> new FitException("The response body is abnormal."));
            return Choir.just(chatCompletionResponse.message());
        } catch (IOException e) {
            throw new FitException(e);
        }
    }
}