package com.zhang.assistant.controller;

import com.zhang.assistant.service.pgvectorService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private pgvectorService pgvectorService;
    @Autowired
    private ChatMemory chatMemory;

    @GetMapping("/ai/chat")
    public String chat(@RequestParam("message") String message, @RequestParam("id")  String id) {
        return chatClient.prompt(message)
                //发送请求时采用环绕增强，给会话配置会话id
//                .advisors(new Consumer<ChatClient.AdvisorSpec>() {
//                    @Override
//                    public void accept(ChatClient.AdvisorSpec advisorSpec) {
//                        advisorSpec.param(ChatMemory.CONVERSATION_ID,id);
//                    }
//                })
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
//                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,id))
                .tools(pgvectorService)
                .call().content();
    }

    @GetMapping("/ai/chatStream")
    public Flux<String> chatStream(@RequestParam("message") String message, @RequestParam("id")  String id) {
        return  chatClient.prompt(message)
                //发送请求时采用环绕增强，给会话配置会话id
//                .advisors(new Consumer<ChatClient.AdvisorSpec>() {
//                    @Override
//                    public void accept(ChatClient.AdvisorSpec advisorSpec) {
//                        advisorSpec.param(ChatMemory.CONVERSATION_ID,id);
//                    }
//                })
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
//                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, id))
                .tools(pgvectorService)
                .stream().content();
    }

    @GetMapping("/ai/memory")
    public List<Message> getMemoryById(@RequestParam("chatId") String ChatId){
        return chatMemory.get(ChatId);
    }


}
