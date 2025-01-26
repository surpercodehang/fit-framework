/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 表示 URI 中的各个组件的类型。
 * <p>该类型参考了 RFC 3986。/p>
 *
 * @author 季聿阶
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986</a>
 * @since 2022-12-23
 */
public enum UriComponentType {
    /**
     * 表示查询参数。
     * <p>其定义如下：{@code query = *( pchar / "/" / "?" )}。</p>
     */
    QUERY {
        @Override
        public boolean isAllowed(int ch) {
            return this.isPchar(ch) || ch == '/' || ch == '?';
        }
    },
    /**
     * 表示 URI 的结尾部分。
     * <p>其定义如下：{@code fragment = *( pchar / "/" / "?" )}。</p>
     */
    FRAGMENT {
        @Override
        public boolean isAllowed(int ch) {
            return this.isPchar(ch) || ch == '/' || ch == '?';
        }
    };

    private static final Set<Character> UNRESERVED_SET = new HashSet<>();
    private static final Set<Character> SUB_DELIMITER_SET = new HashSet<>();

    static {
        UNRESERVED_SET.addAll(Arrays.asList('-', '.', '_', '~'));
        SUB_DELIMITER_SET.addAll(Arrays.asList('!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '='));
    }

    /**
     * 判断指定字节在当前类型中是否允许出现。
     *
     * @param ch 表示指定字节的 {@code int}。
     * @return 如果允许出现，则返回 {@code true}，否则，返回 {@code false}。
     */
    public abstract boolean isAllowed(int ch);

    /**
     * 判断指定字节是否为一个英文字母。
     *
     * @param ch 表示指定字节的 {@code int}。
     * @return 当指定字节是一个英文字母时，返回 {@code true}，否则，返回 {@code false}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986#appendix-A">RFC 3986, appendix A</a>
     */
    protected boolean isAlpha(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    /**
     * 判断指定字节是否为一个数字。
     *
     * @param ch 表示指定字节的 {@code int}。
     * @return 当指定字节是一个数字时，返回 {@code true}，否则，返回 {@code false}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986#appendix-A">RFC 3986, appendix A</a>
     */
    protected boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 判断指定字节是否为一个子定界符。
     * <p>子定界符定义如下：{@code sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="}。</p>
     *
     * @param ch 表示指定字节的 {@code int}。
     * @return 当指定字节是子定界符，返回 {@code true}，否则，返回 {@code false}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986#appendix-A">RFC 3986, appendix A</a>
     */
    protected boolean isSubDelimiter(int ch) {
        return SUB_DELIMITER_SET.contains((char) ch);
    }

    /**
     * 判断指定字节是否为非保留的。
     * <p>非保留字符定义如下：{@code unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"}。</p>
     *
     * @param ch 表示指定字节的 {@code int}。
     * @return 当指定字节是非保留的，返回 {@code true}，否则，返回 {@code false}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986#appendix-A">RFC 3986, appendix A</a>
     */
    protected boolean isUnreserved(int ch) {
        return this.isAlpha(ch) || this.isDigit(ch) || UNRESERVED_SET.contains((char) ch);
    }

    /**
     * 判断指定字节是否为一个 {@code pchar}。
     * <p>pchar 的定义如下：{@code pchar = unreserved / pct-encoded / sub-delims / ":" / "@"}。</p>
     * <p>因为 {@code pct-encoded} 为百分比编码，单个百分号并不属于有效字符，因此忽略。</p>
     *
     * @param ch 表示指定字节的 {@code int}。
     * @return 当指定字节是 pchar，返回 {@code true}，否则，返回 {@code false}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc3986#appendix-A">RFC 3986, appendix A</a>
     */
    protected boolean isPchar(int ch) {
        return this.isUnreserved(ch) || this.isSubDelimiter(ch) || ch == ':' || ch == '@';
    }
}
