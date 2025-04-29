package com.core.aiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;

public class HttpAiInvoke {

    public static void main(String[] args) {
        // 从环境变量或配置中读取 API Key
        String apiKey = TestApiKey.DashScopeKey;
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 构建请求体
        JSONObject json = new JSONObject();
        json.put("model", "qwen-plus");

        // 构建 input.messages 列表
        JSONArray messages = new JSONArray();
        JSONObject sysMsg = new JSONObject()
                .put("role", "system")
                .put("content", "You are a helpful assistant.");
        JSONObject userMsg = new JSONObject()
                .put("role", "user")
                .put("content", "你是谁？");
        messages.add(sysMsg);
        messages.add(userMsg);

        // 将 messages 放入 input
        JSONObject input = new JSONObject().put("messages", messages);
        json.put("input", input);

        // 构建 parameters
        JSONObject parameters = new JSONObject()
                .put("result_format", "message");
        json.put("parameters", parameters);

        // 发起 HTTP POST 请求
        HttpResponse response = HttpRequest.post(url)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .body(json.toString())
            // 根据需要设置超时时间，单位：毫秒
            .timeout(60_000)
            .execute();

        // 处理响应
        if (response.getStatus() == 200) {
            System.out.println("调用成功，返回内容：");
            System.out.println(response.body());
        } else {
            System.err.println("调用失败，状态码：" + response.getStatus());
            System.err.println("返回内容： " + response.body());
        }
    }
}
