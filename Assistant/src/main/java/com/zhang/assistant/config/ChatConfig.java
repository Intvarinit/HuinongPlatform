package com.zhang.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {       //持久化到数据库
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository) // 使用 JDBC 存储
                .maxMessages(20)
                .build();
    }

//    @Bean
//    public ChatMemory chatMemory() {      //对话记忆的内存实现
//        return new InMemoryChatMemory();
//    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory){
        return ChatClient.builder(model)
                .defaultSystem("你是惠农循环管理平台的小助手，指引用户完成软件的使用流程。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()        // 添加聊天记录记忆的环绕增强器
                )
                .build();
    }
}