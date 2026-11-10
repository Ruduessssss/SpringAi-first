package com.lycai.chat.service;

import com.openai.core.http.ResponseWebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService (ChatClient chatClient)
    {
        this.chatClient=chatClient;
    }

    private final Logger log = LoggerFactory.getLogger(ChatService.class);



    public String ask(String question) {
        return ask(question, "explain");
    }
    public String ask(String question, String mode) {


        String actualMode = mode==null||mode.isBlank()?"explain":mode.trim();



        if(!"explain".equals(actualMode)&&!"review".equals(actualMode))
            {
                ResponseStatusException exception = new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "不支持的模式,仅支持explain,review"
                );
                log.warn("请求参数校验失败,异常->{}",exception.getClass().getSimpleName());
                throw exception;
            }
        String callId = UUID.randomUUID().toString();
        long start = System.nanoTime();
            try {


                log.info("开始模型调用,id->{},mode->{}", callId,actualMode);
                String basePrompt =
                        "你是 Java 与 Spring 学习助手。使用简体中文；"
                                + "不确定时明确说明，不编造 API。";

                String modePrompt;
                if ("review".equals(actualMode)) {
                    modePrompt = "重点指出代码中的潜在问题、原因及修改建议。";
                } else {
                    // actualMode 已通过校验；非 review 时就是 explain
                    modePrompt = "先简要解释思路，再按需给出代码。";
                }

                String content = chatClient.prompt()
                        .system(basePrompt + "\n" + modePrompt)
                        .user(question)
                        .call()
                        .content();
                if(content==null||content.isBlank())
                    throw new IllegalStateException("模型返回空内容");
                long elapsedMs = (System.nanoTime() - start) / 1_000_000;
                log.info("完成模型调用,id->{},耗时->{}毫秒", callId,elapsedMs);
                return "助手:"+content;
            }
        catch (Exception e)
        {

            ResponseStatusException exception = new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "模型服务暂时不可用"
            );
            log.warn("模型调用异常,id->{},异常->{}",callId, e.getClass().getSimpleName());
            throw exception;
        }
    }

}
