/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.document;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.resource.web.Media;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示可量化比较的 {@link Document}。
 *
 * @author 易文渊
 * @since 2024-08-06
 */
public class MeasurableDocument implements Document, Measurable {
    private final Document document;
    private final double score;

    /**
     * 创建 {@link MeasurableDocument} 的实体。
     *
     * @param document 表示原始文档的 {@link Document}。
     * @param score 表示文档评分的 {@code double}。
     * @throws IllegalArgumentException 当 {@code document} 为 {@code null} 时。
     */
    public MeasurableDocument(Document document, double score) {
        this.document = notNull(document, "The document cannot be null.");
        this.score = score;
    }

    @Override
    @Nonnull
    public String text() {
        return this.document.text();
    }

    @Override
    public List<Media> medias() {
        return this.document.medias();
    }

    @Override
    public String id() {
        return this.document.id();
    }

    @Nonnull
    @Override
    public Map<String, Object> metadata() {
        return this.document.metadata();
    }

    @Override
    public double score() {
        return this.score;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MeasurableDocument that = (MeasurableDocument) object;
        return Double.compare(this.score, that.score) == 0 && Objects.equals(this.document, that.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.document, this.score);
    }

    @Override
    public String toString() {
        return "DocumentWithScore{" + "document=" + document + ", score=" + score + '}';
    }
}