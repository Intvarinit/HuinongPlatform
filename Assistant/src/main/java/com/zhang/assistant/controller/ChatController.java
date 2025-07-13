package com.zhang.assistant.controller;

import com.zhang.assistant.service.pgvectorService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private pgvectorService pgvectorService;

    @GetMapping("/ai/chat")
    public String chat(@RequestParam("message") String message, @RequestParam("id")  String id) {
        return chatClient.prompt(message)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,id))
                .tools(pgvectorService.searchSimilarDocuments(message))
                .call().content();
    }

    @GetMapping("/ai/chatStream")
    public Flux<String> chatStream(@RequestParam("message") String message, @RequestParam("id")  String id) {
        return  chatClient.prompt(message)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, id))
                .tools(pgvectorService.searchSimilarDocuments(message))
                .stream().content();
    }


}
