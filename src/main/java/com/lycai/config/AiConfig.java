package com.lycai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder
                .defaultSystem("你是 Java 与 Spring 学习助手。使用简体中文，先简要解释思路，再按需给出代码；不确定时明确说明，不编造 API")
                .build();
    }



}
