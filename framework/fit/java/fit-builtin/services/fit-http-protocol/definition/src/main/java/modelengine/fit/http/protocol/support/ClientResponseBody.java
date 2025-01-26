/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.ClientResponse;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示 {@link ReadableMessageBody} 在 {@link ClientResponse} 中的默认实现。
 *
 * @author 季聿阶
 * @since 2023-09-19
 */
public class ClientResponseBody extends InputStream implements ReadableMessageBody {
    private final ClientResponse response;

    public ClientResponseBody(ClientResponse response) {
        this.response = notNull(response, "The client response cannot be null.");
    }

    @Override
    public int read() throws IOException {
        return this.response.readBody();
    }

    @Override
    public int read(@Nonnull byte[] bytes, int off, int len) throws IOException {
        return this.response.readBody(bytes, off, len);
    }
}
