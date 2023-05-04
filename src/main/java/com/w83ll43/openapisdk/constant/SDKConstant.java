package com.w83ll43.openapisdk.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * SDK 相关常量
 */
public class SDKConstant {

    // 签名 Header
    public static final String X_CA_SIGNATURE = "X-Ca-Signature";

    // 所有参与签名的 Header
    public static final String X_CA_SIGNATURE_HEADERS = "X-Ca-Signature-Headers";

    // 请求时间戳
    public static final String X_CA_TIMESTAMP = "X-Ca-Timestamp";

    // 请求放重放 Nonce 15分钟内保持唯一 建议使用 UUID
    public static final String X_CA_NONCE = "X-Ca-Nonce";

    // APP KEY
    public static final String X_CA_KEY = "X-Ca-Key";

    // 错误信息
    public static final String X_CA_ERROR_MESSAGE = "X-Ca-Error-Message";

    //签名版本号
    public static final String CA_VERSION = "CA_VERSION";

    //编码UTF-8
    public static final Charset ENCODING = StandardCharsets.UTF_8;

    //Header头的编码
    public static final Charset HEADER_ENCODING = StandardCharsets.ISO_8859_1;

    //签名算法
    public static final String X_CA_SIGNATURE_METHOD = "X-Ca-Signature-Method";


    // 通用内容常量

    //UserAgent
    public static final String USER_AGENT = "SDK";

    //换行符
    public static final String LF = "\n";

    // 默认请求超时时间 单位毫秒
    public static final int  DEFAULT_TIMEOUT = 1000;


    // 参与签名的系统 Header 前缀 只有指定前缀的 Header 才会参与到签名中
    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Ca-";

    //签名版本号
    public static final String CA_VERSION_VALUE = "1.1.2";
}
