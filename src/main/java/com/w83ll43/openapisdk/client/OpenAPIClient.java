package com.w83ll43.openapisdk.client;

import com.w83ll43.openapisdk.constant.SDKConstant;
import com.w83ll43.openapisdk.enums.HttpMethod;
import com.w83ll43.openapisdk.enums.ParamPosition;
import com.w83ll43.openapisdk.model.request.ApiRequest;
import com.w83ll43.openapisdk.model.response.ApiResponse;

import java.util.HashMap;

public class OpenAPIClient extends ApacheHttpClient {

    /**
     * 请求地址
     */
    private static final String GATEWAY_HOST = "localhost:8082";

    public OpenAPIClient(String appKey, String appSecret) {
        super.init(appKey, appSecret, GATEWAY_HOST);
    }

    /**
     * 获取随机笑话
     * @return
     */
    public ApiResponse getRandomJoke() {
        String path = "/api/joke";
        ApiRequest request = new ApiRequest(HttpMethod.GET, path);
        return sendSyncRequest(request);
    }

    /**
     * 根据类型获取随机句子 POST 请求
     * @param type
     * @return
     */
    public ApiResponse getRandomSentencePostByType(String type) {
        String path = "/api/sentences/getPost";
        ApiRequest apiRequest = new ApiRequest(HttpMethod.POST, path);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", type);
        apiRequest.setJsonParams(hashMap);
        return sendSyncRequest(apiRequest);
    }

    /**
     * 根据类型获取随机句子 GET 请求
     * @param type
     * @return
     */
    public ApiResponse getRandomSentenceGetByType(String type) {
        String path = "/api/sentences/get/[type]";
        ApiRequest apiRequest = new ApiRequest(HttpMethod.GET, path);
        apiRequest.addParam("type", type, ParamPosition.PATH, true);
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
