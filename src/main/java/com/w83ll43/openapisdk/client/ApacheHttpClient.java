package com.w83ll43.openapisdk.client;

import com.w83ll43.openapisdk.constant.HttpConstant;
import com.w83ll43.openapisdk.enums.Scheme;
import com.w83ll43.openapisdk.exception.SDKException;
import com.w83ll43.openapisdk.model.request.ApiRequest;
import com.w83ll43.openapisdk.model.response.ApiResponse;
import com.w83ll43.openapisdk.utils.ApiRequestMaker;
import com.w83ll43.openapisdk.utils.HttpCommonUtil;
import com.w83ll43.openapisdk.utils.SignUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ApacheHttpClient extends BaseApiClient {

    private CloseableHttpClient httpClient;

    protected ApacheHttpClient(){}

    public void init(String appKey, String appSecret, String host) {

        this.scheme = Scheme.HTTP;

        /**
         * AppKey
         * APP KEY
         */
        this.appKey = appKey;

        /**
         * AppSecret
         * APP 密钥
         */
        this.appSecret = appSecret;

        /**
         * API HOST
         * API 域名
         */
        this.host = host;

        // 创建请求配置信息
        RequestConfig defaultConfig = RequestConfig.custom()
                // 创建请求配置信息
                .setConnectTimeout(10000)
                // 设置响应超时时间
                .setSocketTimeout(10000)
                // 设置从连接池获取链接的超时时间
                .setConnectionRequestTimeout(10000)
                .build();

        httpClient = HttpClients.custom()
                // 设置默认参数
                .setDefaultRequestConfig(defaultConfig)
                .build();
    }

    private HttpUriRequest buildRequest(ApiRequest apiRequest) {

        apiRequest.setHost(this.host);
        apiRequest.setScheme(this.scheme);

        // 为请求添加请求头
        ApiRequestMaker.make(apiRequest , appKey , appSecret);

        RequestBuilder builder = RequestBuilder.create(apiRequest.getMethod().getValue());

        /*
         *  拼接URL
         *  HTTP + HOST + PATH(With PathParameter) + Query Parameter
         */
        try {
            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setScheme(apiRequest.getScheme().getValue());
            uriBuilder.setHost(apiRequest.getHost());
            uriBuilder.setPath(apiRequest.getPath());
            // 查询参数不为空
            if (!HttpCommonUtil.isEmpty(apiRequest.getQuerys())) {
                for (Map.Entry<String, List<String>> entry : apiRequest.getQuerys().entrySet()) {
                    for(String value : entry.getValue()){
                        uriBuilder.addParameter(entry.getKey(), value);
                    }
                }
            }
            builder.setUri(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new SDKException("构建 HTTP 请求 URI 失败", e);
        }

        EntityBuilder bodyBuilder = EntityBuilder.create();

        // 设置请求数据类型
        if(null == apiRequest.getFirstHeaderValue(HttpConstant.HTTP_HEADER_CONTENT_TYPE)) {
            bodyBuilder.setContentType(ContentType.parse(apiRequest.getMethod().getRequestContentType()));
        } else {
            bodyBuilder.setContentType(ContentType.parse(apiRequest.getFirstHeaderValue(HttpConstant.HTTP_HEADER_CONTENT_TYPE)));
        }

        // TODO POST 请求 JSON 数据
//        JSONObject json = new JSONObject();
//        json.set("type", "a");
//        builder.setEntity(new StringEntity(json.toString(),"UTF-8"));

        if (!HttpCommonUtil.isEmpty(apiRequest.getFormParams())) {
            /**
             * 如果 formParams 不为空
             * 将 Form 中的内容以 urlQueryParams 的格式存放在 body 中 (k1=v1&k2=v2&k3=v3)
             */
            List<NameValuePair> paramList = new ArrayList<>();

            for (Entry<String, List<String>> entry : apiRequest.getFormParams().entrySet()) {
                for(String value : entry.getValue()) {
                    paramList.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            bodyBuilder.setParameters(paramList);
            builder.setEntity(bodyBuilder.build());
        } else if (!HttpCommonUtil.isEmpty(apiRequest.getBytesBody())) {
            bodyBuilder.setBinary(apiRequest.getBytesBody());
            builder.setEntity(bodyBuilder.build());
        }

        // 设置请求头信息
        for (Map.Entry<String, List<String>> entry : apiRequest.getHeaders().entrySet()) {
            for(String value : entry.getValue()){
                builder.addHeader(entry.getKey(), value);
            }
        }

        return builder.build();
    }

    /**
     * 将 HttpResponse 解析到 ApiResponse 中
     * @param httpResponse
     * @return
     * @throws IOException
     */
    private ApiResponse parseToApiResponse(HttpResponse httpResponse) throws IOException {
        ApiResponse apiResponse = new ApiResponse(httpResponse.getStatusLine().getStatusCode());

        // 解析响应头 headers
        for (Header header : httpResponse.getAllHeaders()) {
            List<String> values = apiResponse.getHeaders().get(header.getName());

            if(values == null){
                values = new ArrayList<>();
            }

            values.add(header.getValue());
            apiResponse.getHeaders().put(header.getName().toLowerCase() , values);
        }

        // 解析响应消息 message
        apiResponse.setMessage(httpResponse.getStatusLine().getReasonPhrase());

        if(httpResponse.getEntity() != null){
            // Content-Type
            Header contentType = httpResponse.getEntity().getContentType();
            if(contentType != null){
                apiResponse.setContentType(contentType.getValue());
            } else {
                apiResponse.setContentType(HttpConstant.CONTENT_TYPE_TEXT);
            }

            // 解析响应体 Body
            apiResponse.setBytesBody(EntityUtils.toByteArray(httpResponse.getEntity()));

            String contentMD5 = apiResponse.getFirstHeaderValue(HttpConstant.HTTP_HEADER_CA_CONTENT_MD5);
            if(null != contentMD5 && !"".equals(contentMD5)){
                // 消息摘要
                String localContentMd5 = SignUtil.messageDigest(apiResponse.getBytesBody());
                if(!contentMD5.equalsIgnoreCase(localContentMd5)){
                    throw new SDKException("Server Content MD5 does not match body content , server md5 is " + contentMD5 + "  local md5 is " + localContentMd5 + " body is " + new String(apiResponse.getBytesBody()));
                }
            }
        }else{
            String contentTypeStr = apiResponse.getFirstHeaderValue(HttpConstant.HTTP_HEADER_CONTENT_TYPE);
            if(null == contentTypeStr){
                contentTypeStr = HttpConstant.CONTENT_TYPE_TEXT;
            }
            apiResponse.setContentType(contentTypeStr);
        }
        return apiResponse;
    }

    /**
     * 发送同步请求
     * @param apiRequest
     * @return
     */
    @Override
    public final ApiResponse sendSyncRequest(ApiRequest apiRequest) {
        HttpUriRequest httpRequest = buildRequest(apiRequest);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
            return parseToApiResponse(httpResponse);
        } catch (IOException e) {
            throw new SDKException(e);
        } finally {
            HttpCommonUtil.closeQuietly(httpResponse);
        }
    }

    public void shutdown() {
        HttpCommonUtil.closeQuietly(httpClient);
    }

}
