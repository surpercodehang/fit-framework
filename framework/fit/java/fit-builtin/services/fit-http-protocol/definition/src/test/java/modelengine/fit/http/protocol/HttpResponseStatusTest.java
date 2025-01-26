/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * {@link HttpResponseStatus} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-15
 */
@DisplayName("测试 HttpResponseStatus 类")
public class HttpResponseStatusTest {
    @ParameterizedTest
    @CsvSource({
            "CONTINUE,100", "SWITCHING_PROTOCOLS,101", "PROCESSING,102", "OK,200", "CREATED,201", "ACCEPTED,202",
            "NON_AUTHORITATIVE_INFORMATION,203", "NO_CONTENT,204", "RESET_CONTENT,205", "PARTIAL_CONTENT,206",
            "MULTI_STATUS,207", "MULTIPLE_CHOICES,300", "MOVED_PERMANENTLY,301", "FOUND,302", "SEE_OTHER,303",
            "NOT_MODIFIED,304", "USE_PROXY,305", "TEMPORARY_REDIRECT,307", "PERMANENT_REDIRECT,308", "BAD_REQUEST,400",
            "UNAUTHORIZED,401", "PAYMENT_REQUIRED,402", "FORBIDDEN,403", "NOT_FOUND,404", "METHOD_NOT_ALLOWED,405",
            "NOT_ACCEPTABLE,406", "PROXY_AUTHENTICATION_REQUIRED,407", "REQUEST_TIMEOUT,408", "CONFLICT,409",
            "GONE,410", "LENGTH_REQUIRED,411", "PRECONDITION_FAILED,412", "REQUEST_ENTITY_TOO_LARGE,413",
            "REQUEST_URI_TOO_LONG,414", "UNSUPPORTED_MEDIA_TYPE,415", "REQUESTED_RANGE_NOT_SATISFIABLE,416",
            "EXPECTATION_FAILED,417", "MISDIRECTED_REQUEST,421", "UNPROCESSABLE_ENTITY,422", "LOCKED,423",
            "FAILED_DEPENDENCY,424", "UNORDERED_COLLECTION,425", "UPGRADE_REQUIRED,426", "PRECONDITION_REQUIRED,428",
            "TOO_MANY_REQUESTS,429", "REQUEST_HEADER_FIELDS_TOO_LARGE,431", "INTERNAL_SERVER_ERROR,500",
            "NOT_IMPLEMENTED,501", " BAD_GATEWAY,502", "SERVICE_UNAVAILABLE,503", "GATEWAY_TIMEOUT,504",
            " HTTP_VERSION_NOT_SUPPORTED,505", "VARIANT_ALSO_NEGOTIATES,506", "INSUFFICIENT_STORAGE,507",
            "NOT_EXTENDED,510", "NETWORK_AUTHENTICATION_REQUIRED,511"
    })
    @DisplayName("获取的状态码与给定值相等")
    void theStatusCodeShouldBeEqualsToTheGivenCode(HttpResponseStatus httpResponseStatus, int expectCode) {
        int actualCode = httpResponseStatus.statusCode();
        assertThat(actualCode).isEqualTo(expectCode);
    }

    @ParameterizedTest
    @CsvSource({
            "CONTINUE,Continue", "SWITCHING_PROTOCOLS,Switching Protocols", "PROCESSING,Processing", "OK,OK",
            "CREATED,Created", "ACCEPTED,Accepted", "NON_AUTHORITATIVE_INFORMATION,Non-Authoritative Information",
            "NO_CONTENT,No Content", "RESET_CONTENT,Reset Content", "PARTIAL_CONTENT,Partial Content",
            "MULTI_STATUS,Multi-Status", "MULTIPLE_CHOICES,Multiple Choices", "MOVED_PERMANENTLY,Moved Permanently",
            "FOUND,Found", "SEE_OTHER,See Other", "NOT_MODIFIED,Not Modified", "USE_PROXY,Use Proxy",
            "TEMPORARY_REDIRECT,Temporary Redirect", "PERMANENT_REDIRECT,Permanent Redirect", "BAD_REQUEST,Bad Request",
            "UNAUTHORIZED,Unauthorized", "PAYMENT_REQUIRED,Payment Required", "FORBIDDEN,Forbidden",
            "NOT_FOUND,Not Found", "METHOD_NOT_ALLOWED,Method Not Allowed", "NOT_ACCEPTABLE,Not Acceptable",
            "PROXY_AUTHENTICATION_REQUIRED,Proxy Authentication Required", "REQUEST_TIMEOUT,Request Timeout",
            "CONFLICT,Conflict", "GONE,Gone", "LENGTH_REQUIRED,Length Required",
            "PRECONDITION_FAILED,Precondition Failed", "REQUEST_ENTITY_TOO_LARGE,Request Entity Too Large",
            "REQUEST_URI_TOO_LONG,Request-URI Too Long", "UNSUPPORTED_MEDIA_TYPE,Unsupported Media Type",
            "REQUESTED_RANGE_NOT_SATISFIABLE,Requested Range Not Satisfiable", "EXPECTATION_FAILED,Expectation Failed",
            "MISDIRECTED_REQUEST,Misdirected Request", "UNPROCESSABLE_ENTITY,Unprocessable Entity", "LOCKED,Locked",
            "FAILED_DEPENDENCY,Failed Dependency", "UNORDERED_COLLECTION,Unordered Collection",
            "UPGRADE_REQUIRED,Upgrade Required", "PRECONDITION_REQUIRED,Precondition Required",
            "TOO_MANY_REQUESTS,Too Many Requests", "REQUEST_HEADER_FIELDS_TOO_LARGE,Request Header Fields Too Large",
            "INTERNAL_SERVER_ERROR,Internal Server Error", "NOT_IMPLEMENTED,Not Implemented",
            " BAD_GATEWAY,Bad Gateway", "SERVICE_UNAVAILABLE,Service Unavailable", " GATEWAY_TIMEOUT,Gateway Timeout",
            "HTTP_VERSION_NOT_SUPPORTED,HTTP Version Not Supported", "VARIANT_ALSO_NEGOTIATES,Variant Also Negotiates",
            "INSUFFICIENT_STORAGE,Insufficient Storage", "NOT_EXTENDED,Not Extended",
            "NETWORK_AUTHENTICATION_REQUIRED,Network Authentication Required"
    })
    @DisplayName("获取的状态短语与给定值相等")
    void theReasonPhraseShouldBeEqualsToTheGivenPhrase(HttpResponseStatus httpResponseStatus,
            String expectReasonPhrase) {
        String reasonPhrase = httpResponseStatus.reasonPhrase();
        assertThat(reasonPhrase).isEqualTo(expectReasonPhrase);
    }

    @ParameterizedTest
    @CsvSource({
            "CONTINUE,100", "SWITCHING_PROTOCOLS,101", "PROCESSING,102", "OK,200", "CREATED,201", "ACCEPTED,202",
            "NON_AUTHORITATIVE_INFORMATION,203", "NO_CONTENT,204", "RESET_CONTENT,205", "PARTIAL_CONTENT,206",
            "MULTI_STATUS,207", "MULTIPLE_CHOICES,300", "MOVED_PERMANENTLY,301", "FOUND,302", "SEE_OTHER,303",
            "NOT_MODIFIED,304", "USE_PROXY,305", "TEMPORARY_REDIRECT,307", "PERMANENT_REDIRECT,308", "BAD_REQUEST,400",
            "UNAUTHORIZED,401", "PAYMENT_REQUIRED,402", "FORBIDDEN,403", "NOT_FOUND,404", "METHOD_NOT_ALLOWED,405",
            "NOT_ACCEPTABLE,406", "PROXY_AUTHENTICATION_REQUIRED,407", "REQUEST_TIMEOUT,408", "CONFLICT,409",
            "GONE,410", "LENGTH_REQUIRED,411", "PRECONDITION_FAILED,412", "REQUEST_ENTITY_TOO_LARGE,413",
            "REQUEST_URI_TOO_LONG,414", "UNSUPPORTED_MEDIA_TYPE,415", "REQUESTED_RANGE_NOT_SATISFIABLE,416",
            "EXPECTATION_FAILED,417", "MISDIRECTED_REQUEST,421", "UNPROCESSABLE_ENTITY,422", "LOCKED,423",
            "FAILED_DEPENDENCY,424", "UNORDERED_COLLECTION,425", "UPGRADE_REQUIRED,426", "PRECONDITION_REQUIRED,428",
            "TOO_MANY_REQUESTS,429", "REQUEST_HEADER_FIELDS_TOO_LARGE,431", "INTERNAL_SERVER_ERROR,500",
            "NOT_IMPLEMENTED,501", " BAD_GATEWAY,502", "SERVICE_UNAVAILABLE,503", "GATEWAY_TIMEOUT,504",
            " HTTP_VERSION_NOT_SUPPORTED,505", "VARIANT_ALSO_NEGOTIATES,506", "INSUFFICIENT_STORAGE,507",
            "NOT_EXTENDED,510", "NETWORK_AUTHENTICATION_REQUIRED,511"
    })
    @DisplayName("根据指定的响应状态码，获取响应的状态正确")
    void givenSpecifiedStatusCodeThenReturnCorrespondingResponseStatus(HttpResponseStatus httpResponseStatus,
            int status) {
        HttpResponseStatus responseStatus = HttpResponseStatus.from(status);
        assertThat(responseStatus).isEqualTo(httpResponseStatus);
    }
}
