package com.lycai.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService (ChatClient chatClient)
    {
        this.chatClient=chatClient;
    }

    public String ask(String question) {
        return ask(question, "explain");
    }
    public String ask(String question, String mode) {
        String basePrompt =
                "你是 Java 与 Spring 学习助手。使用简体中文；"
                        + "不确定时明确说明，不编造 API。";

        String modePrompt;
        if ("review".equals(mode)) {
            modePrompt = "重点指出代码中的潜在问题、原因及修改建议。";
        } else {
            // explain、未传 mode 或未知值，都使用解释模式
            modePrompt = "先简要解释思路，再按需给出代码。";
        }

        return "助手:" + chatClient.prompt()
                .system(basePrompt + "\n" + modePrompt)
                .user(question)
                .call()
                .content();
    }

}
