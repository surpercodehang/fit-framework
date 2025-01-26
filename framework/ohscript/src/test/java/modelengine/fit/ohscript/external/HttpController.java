/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestHeader;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.entity.MultiValueEntity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 用于测试的 Http 处理器。
 *
 * @author 季聿阶
 * @since 2023-10-19
 */
@Component
@RequestMapping("/oh")
public class HttpController {
    /**
     * 获取文本数据。
     *
     * @param queries 查询参数。
     * @param header 头部信息。
     * @return 返回查询参数和头部信息的字符串。
     */
    @GetMapping("/get/text")
    public String getText(@RequestParam("q1") List<String> queries, @RequestHeader("h1") String header) {
        return String.join(",", queries) + "," + header;
    }

    /**
     * 获取 JSON 数据。
     *
     * @param query 查询参数。
     * @param header 头部信息。
     * @return 返回一个包含查询参数和头部信息的 Map。
     */
    @GetMapping("/get/json")
    public Map<String, String> getJson(@RequestParam("q1") String query, @RequestHeader("h1") String header) {
        return MapBuilder.<String, String>get().put("k1", query).put("k2", header).build();
    }

    /**
     * 发送文本数据。
     *
     * @param entity 文本实体。
     * @return 返回实体的内容。
     */
    @PostMapping("/post/text")
    public String postText(TextEntity entity) {
        return entity.content();
    }

    /**
     * 发送 JSON 数据。
     *
     * @param entity JSON 实体。
     * @return 返回实体的对象。
     */
    @PostMapping("/post/json")
    public Map<String, Object> postJson(ObjectEntity<Map<String, Object>> entity) {
        return entity.object();
    }

    /**
     * 发送表单数据。
     *
     * @param entity 表单实体。
     * @return 返回实体的第一个值。
     */
    @PostMapping("/post/form")
    public String postForm(MultiValueEntity entity) {
        return entity.first("f1").orElse(StringUtils.EMPTY);
    }

    /**
     * 更新文本数据。
     *
     * @param entity 文本实体。
     * @return 返回实体的内容。
     */
    @PutMapping("/put/text")
    public String putText(TextEntity entity) {
        return entity.content();
    }

    /**
     * 更新 JSON 数据。
     *
     * @param entity JSON 实体。
     * @return 返回实体的对象。
     */
    @PutMapping("/put/json")
    public Map<String, Object> putJson(ObjectEntity<Map<String, Object>> entity) {
        return entity.object();
    }

    /**
     * 更新表单数据。
     *
     * @param entity 表单实体。
     * @return 返回实体的第一个值。
     */
    @PutMapping("/put/form")
    public String putForm(MultiValueEntity entity) {
        return entity.first("f1").orElse(StringUtils.EMPTY);
    }

    /**
     * 更新文本数据。
     *
     * @param entity 文本实体。
     * @return 返回实体的内容。
     */
    @PatchMapping("/patch/text")
    public String patchText(TextEntity entity) {
        return entity.content();
    }

    /**
     * 更新 JSON 数据。
     *
     * @param entity JSON 实体。
     * @return 返回实体的对象。
     */
    @PatchMapping("/patch/json")
    public Map<String, Object> patchJson(ObjectEntity<Map<String, Object>> entity) {
        return entity.object();
    }

    /**
     * 更新表单数据。
     *
     * @param entity 表单实体。
     * @return 返回实体的第一个值。
     */
    @PatchMapping("/patch/form")
    public String patchForm(MultiValueEntity entity) {
        return entity.first("f1").orElse(StringUtils.EMPTY);
    }

    /**
     * 删除文本数据。
     *
     * @param queries 查询参数。
     * @param header 头部信息。
     * @return 返回查询参数和头部信息的字符串。
     */
    @DeleteMapping("/delete/text")
    public String deleteText(@RequestParam("q1") List<String> queries, @RequestHeader("h1") String header) {
        return String.join(",", queries) + "," + header;
    }

    /**
     * 删除 JSON 数据。
     *
     * @param query 查询参数。
     * @param header 头部信息。
     * @return 返回一个包含查询参数和头部信息的 Map。
     */
    @DeleteMapping("/delete/json")
    public Map<String, String> deleteJson(@RequestParam("q1") String query, @RequestHeader("h1") String header) {
        return MapBuilder.<String, String>get().put("k1", query).put("k2", header).build();
    }
}
