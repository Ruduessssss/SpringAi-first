package com.lycai.chat.controller;

import com.lycai.chat.dto.ChatRequest;
import com.lycai.chat.dto.ChatResponse;
import com.lycai.chat.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService)
    {
        this.chatService=chatService;
    }


    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request)
    {
        String question = request.question();
        if(question==null||question.isBlank())
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "问题不能为空"
            );
        }
        question = question.trim();
        if(question.length()>2000)
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "问题不能超过2000字"
            );
        }

        String ans = this.chatService.ask(question,request.mode());
        return new ChatResponse(ans);
    }


//    @GetMapping(value = "/chat",produces = "text/plain;charset=UTF-8")
//    public String chat(@RequestParam("question") String question)
//    {
//        if(question.isBlank())
//        {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "问题不能为空"
//            );
//
//        }
//
//        if(question.length()>2000)
//        {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "问题不能超过 2000 个字符"
//            );
//        }
//
//        return chatService.ask(question.trim());
//    }
}
