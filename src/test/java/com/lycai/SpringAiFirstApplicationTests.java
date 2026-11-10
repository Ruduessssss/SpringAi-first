package com.lycai;

import com.lycai.chat.service.ChatService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("mock")
@SpringBootTest
@AutoConfigureMockMvc
class SpringAiFirstApplicationTests {



    @Resource
    private ChatService chatService;

    @Resource
    private MockMvc mockMvc;
    @Resource
    ChatClient chatClient;


    @Test
    void testChatBlankResponse() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"question":"请只回复：你好"}
                            """))
                .andExpect(status().isInternalServerError())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message")
                        .value(not(blankOrNullString())));
    }


    @Test
    void testChatResponse() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"question":"请只回复：你好"}
                            """))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.answer").isString())
                .andExpect(jsonPath("$.answer")
                        .value(not(blankOrNullString())));
    }

    @Test
    void 测试本地模拟服务重试() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.ask("你好")
        );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatusCode()
        );
    }


    @Test
    void 测试自动注入()
    {
        Assertions.assertNotNull(chatClient);
        Assertions.assertNotNull(chatService);
    }


    @Test
    void 控制层调用错误模式() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                                {
                                    "question":"解释一下Bean",
                                    "mode":"unknown"
                                }
                                """
                ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(not(blankOrNullString())));
    }

    @Test
    void 业务层调用错误模式()
    {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> chatService.ask("解释一下Bean", "unknown"));

        assertEquals(HttpStatus.BAD_REQUEST,ex.getStatusCode());
    }

    @Test
    void 正常问题应返回回答() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                                {
                                  "question": "请用一句话介绍 Spring AI。"
                                }
                                """)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.answer")
                        .value(not(blankOrNullString())));
    }

    @Test
    void 空白问题应返回400() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "   "
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(not(blankOrNullString())));
    }

    @Test
    void 缺少问题字段应返回400() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(not(blankOrNullString())));
    }

    @Test
    void 格式错误的JSON应返回400() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(not(blankOrNullString())));
    }


//    @Test
//    void 测试真实模型问答() {
//        String answer = chatService.ask("请用一句话介绍 Spring AI。");
//
//        System.out.println("模型回答:"+answer);
//
//        assertNotNull(answer, "模型回答不能为 null");
//        assertFalse(answer.isBlank(), "模型回答不能为空白");
//    }

}
