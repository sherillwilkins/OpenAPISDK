package com.w83ll43.openapisdk.client;

import com.w83ll43.openapisdk.enums.Scheme;
import com.w83ll43.openapisdk.model.request.ApiRequest;
import com.w83ll43.openapisdk.model.response.ApiResponse;

/**
 * apiClient基类
 *
 * @author VK.Gao
 * @date 2017/03/02
 */
public abstract class BaseApiClient {

    /**
     * appKey
     */
    String appKey;

    /**
     * secretKey
     */
    String appSecret;

    /**
     * 请求协议
     */
    Scheme scheme;

    /**
     * 请求地址
     */
    String host;

    /**
     * 发送同步请求
     * @param apiRequest
     * @return
     */
    protected abstract ApiResponse sendSyncRequest(ApiRequest apiRequest);

    // TODO 完成异步请求
//    /**
//     * 发送异步请求
//     * @param apiRequest
//     * @param apiCallback
//     */
//    protected abstract void sendAsyncRequest(final ApiRequest apiRequest , final ApiCallback apiCallback);
}
