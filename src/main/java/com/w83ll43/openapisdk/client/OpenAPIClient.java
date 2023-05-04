package com.w83ll43.openapisdk.client;

import com.w83ll43.openapisdk.constant.SDKConstant;
import com.w83ll43.openapisdk.enums.HttpMethod;
import com.w83ll43.openapisdk.model.request.ApiRequest;
import com.w83ll43.openapisdk.model.response.ApiResponse;

public class OpenAPIClient extends ApacheHttpClient {

    /**
     * 请求地址
     */
    private static final String GATEWAY_HOST = "localhost:8082";

    public OpenAPIClient(String appKey, String appSecret) {
        super.init(appKey, appSecret, GATEWAY_HOST);
    }

    public ApiResponse getRandomJokeByClient() {
        String path = "/api/joke";
        ApiRequest request = new ApiRequest(HttpMethod.GET, path);
        return sendSyncRequest(request);
    }

    public ApiResponse getRandomSentenceByClient(String type) {
        String path = "/api/sentences/getPost";
        ApiRequest apiRequest = new ApiRequest(HttpMethod.POST, path);
        return sendSyncRequest(apiRequest);
    }


    /**
     * 将 ApiResponse 对象解析为 String
     * @param response
     * @return
     */
    public static String getResultString(ApiResponse response) {
        StringBuilder result = new StringBuilder();
        result.append("Response from backend server").append(SDKConstant.LF).append(SDKConstant.LF);
        result.append("ResultCode:").append(SDKConstant.LF).append(response.getCode()).append(SDKConstant.LF).append(SDKConstant.LF);
        if(response.getCode() != 200){
            result.append("Error description:").append(response.getHeaders().get("X-Ca-Error-Message")).append(SDKConstant.LF).append(SDKConstant.LF);
        }

        result.append("ResultBody:").append(SDKConstant.LF).append(new String(response.getBytesBody() , SDKConstant.ENCODING));

        return result.toString();
    }
}
