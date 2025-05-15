package com.core.aiagent.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MarkdownGenerator {

    public static void main(String[] args) {

        // 基本配置
        String myId = "wangweirenotl";
        String herId = "zyykaisy1126";

        String inputPath = "/Users/weiren/Downloads/wxdump_work/export/wangweirenotl/json/zyykaisy1126/zyykaisy1126_0_284804.json";
        String outputBaseDir = "/Users/weiren/Downloads/拉拉脸AI_知识库/";

        // 读取 JSON 文件
        String jsonStr = FileUtil.readString(new File(inputPath), StandardCharsets.UTF_8);
        JSONArray array = JSONUtil.parseArray(jsonStr);

        // 过滤文本消息并按时间排序
        List<JSONObject> messages = array.stream()
                .map(obj -> (JSONObject) obj)
                .filter(obj -> "文本".equals(obj.getStr("type_name")))
                .sorted(Comparator.comparing(o -> o.getStr("CreateTime")))
                .toList();

        // 月份分组 Map
        Map<String, List<JSONObject>> monthlyGroups = new LinkedHashMap<>();

        for (JSONObject msg : messages) {
            String timeStr = msg.getStr("CreateTime");
            if (StrUtil.isBlank(timeStr) || StrUtil.isBlank(msg.getStr("msg"))) continue;

            DateTime dt = DateUtil.parse(timeStr, "yyyy-MM-dd HH:mm:ss");
            String monthKey = dt.toString("yyyy-MM");

            monthlyGroups.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(msg);
        }

        // 遍历每个月生成一个 Markdown 文件
        for (Map.Entry<String, List<JSONObject>> entry : monthlyGroups.entrySet()) {
            String month = entry.getKey();
            List<JSONObject> monthMsgs = entry.getValue();

            StringBuilder markdown = new StringBuilder();
            markdown.append("# 拉拉脸AI - 微信对话知识库\n");
            markdown.append("## 月份：").append(month).append("\n");

            String currentDate = "";

            for (JSONObject msg : monthMsgs) {
                String timeStr = msg.getStr("CreateTime");
                String content = msg.getStr("msg");
                DateTime dt = DateUtil.parse(timeStr, "yyyy-MM-dd HH:mm:ss");

                String date = dt.toDateStr();
                String time = dt.toTimeStr();

                if (!StrUtil.equals(date, currentDate)) {
                    currentDate = date;
                    markdown.append("\n## 对话片段 - ").append(currentDate).append("\n\n");
                }

                String talker = msg.getStr("talker");
                String room = msg.getStr("room_name");
                String who = (herId.equals(talker) && herId.equals(room)) ? "她" : "我";

                markdown.append("**").append(who).append("（").append(time).append("）**：")
                        .append(content.trim()).append("\n");
            }

            // 保存成文件
            String filename = "拉拉脸AI_对话_" + month + ".md";
            File outFile = new File(outputBaseDir, filename);
            FileUtil.writeString(markdown.toString(), outFile, StandardCharsets.UTF_8);
            System.out.println("✅ 已导出：" + outFile.getAbsolutePath());
        }
    }
}
