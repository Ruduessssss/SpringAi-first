package com.lycai;

import com.lycai.chat.service.ChatService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class SpringAiFirstApplicationTests {

    @Resource
    private ChatService chatService;

    @Resource
    private MockMvc mockMvc;
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
