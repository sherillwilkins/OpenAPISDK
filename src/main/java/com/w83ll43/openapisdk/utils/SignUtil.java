package com.w83ll43.openapisdk.utils;

import cn.hutool.crypto.digest.MD5;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 签名工具类
 */
public class SignUtil {

    public static String generateGetSign(String data, String secret) throws UnsupportedEncodingException {
        MD5 md5 = MD5.create();
        byte[] digest = md5.digest(data + secret);
        String sign = new String(digest);
        return URLEncoder.encode(sign, "UTF-8");
    }
}
