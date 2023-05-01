package com.w83ll43.openapisdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.w83ll43.openapisdk.common.Result;
import com.w83ll43.openapisdk.entity.JokeText;
import com.w83ll43.openapisdk.entity.Sentence;
import com.w83ll43.openapisdk.utils.SignUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class OpenAPIClient {

    /**
     * 请求地址
     */
    private static final String GATEWAY_HOST = "http://localhost:8081";

    private final String accessKey;

    private final String secretKey;

    public OpenAPIClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    // 然后在这里写 SDK 方法


    /**
     * 根据类型获取随机句子
     * @param type
     * @return
     */
    public Sentence getRandomSentenceByType(String type) throws UnsupportedEncodingException {
        String data = "type=" + type;
        HashMap<String, String> hashMap = new HashMap<>();
        // accessKey
        hashMap.put("accessKey", this.accessKey);
        // timestamp 和 nonce 是防止重放攻击
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("sign", SignUtil.generateGetSign(data, this.secretKey));

        String resultString = HttpRequest.get(GATEWAY_HOST + "/sentences/get/" + type)
                .addHeaders(hashMap)
                .execute().body();
        Result result = JSONUtil.toBean(resultString, Result.class);
        String sentenceString = result.getData().toString();

        if (sentenceString == "null") {
            return null;
        }

        Sentence sentence = JSONUtil.toBean(sentenceString, Sentence.class);
        return sentence;
    }

    /**
     * 随机获取 Joke
     * @return
     */
    public JokeText getRandomJoke() throws UnsupportedEncodingException {

        HashMap<String, String> hashMap = new HashMap<>();
        // accessKey
        hashMap.put("accessKey", this.accessKey);
        // timestamp 和 nonce 是防止重放攻击
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("sign", SignUtil.generateGetSign("", this.secretKey));

        String resultString = HttpRequest.get(GATEWAY_HOST + "/joke")
                .addHeaders(hashMap)
                .execute().body();
        Result result = JSONUtil.toBean(resultString, Result.class);
        String jokeString = result.getData().toString();

        if (jokeString == "null") {
            return null;
        }

        JokeText joke = JSONUtil.toBean(jokeString, JokeText.class);
        return joke;
    }
}
