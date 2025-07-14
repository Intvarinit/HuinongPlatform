package com.zhang.assistant.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhang.assistant.domain.Information;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class pgvectorService {
    @Autowired
    VectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;

    @Tool(description = "当查询与惠农回收平台功能相关，搜索与查询相关的文档内容")
    public List<Document> searchSimilarDocuments(String query){
        System.out.println("Call the RAG Tool！");
        // Retrieve documents similar to a query
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(5).build());
    }

    public static List<Document> convertToDocuments(List<Information> data) {
        List<Document> documents = new ArrayList<>();

        int chunkId = 1;
        for (Information item : data) {
            // 确保必要的字段不为 null 或空字符串
            if (item.getMsg() == null || item.getMsg().trim().isEmpty()) {
                continue; // 跳过无效的条目
            }

            String content = item.getMsg();
            String category = item.getCatagory() != null ? item.getCatagory().trim() : "Uncategorized"; // 提供默认分类
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("category", category);
            metadata.put("chunk_id", "llm-split-" + (chunkId++));

            // 确保 metadata 中没有 null 值
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                if (entry.getValue() == null) {
                    entry.setValue(""); // 或者选择其他处理方式，例如抛出自定义异常或记录日志
                }
            }

            documents.add(new Document(content, metadata));
        }

        return documents;
    }

    // 解析JSON字符串并获取内容
    public static List<Document> parseJsonToDocuments(String jsonContent) {
        Gson gson = new Gson();
        // 修改为如下，使用TypeToken来表示这是一个列表
        Type listType = new TypeToken<List<Information>>() {}.getType(); // 使用TypeToken来指定这是一个列表
        List<Information> infoList = gson.fromJson(jsonContent, listType);
        return convertToDocuments(infoList);
    }

    public static String trimSquareBrackets(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) return jsonStr;

        int firstOpenBracket = jsonStr.indexOf('[');
        int lastCloseBracket = jsonStr.lastIndexOf(']');

        // 如果没有找到匹配的括号，则返回原始字符串
        if (firstOpenBracket == -1 || lastCloseBracket == -1 || firstOpenBracket >= lastCloseBracket) {
            return jsonStr;
        }

        // 提取从第一个 '[' 到最后一个 ']' 之间的内容（不包括这两个括号本身）
        String result = jsonStr.substring(firstOpenBracket, lastCloseBracket+1);

        // 返回结果，并去除可能存在的前后空白字符
        return result.trim();
    }

    public void addDocument(String document){
        List<Document> documents = new ArrayList<>();
        String ret = chatClient.prompt(document)
                .user("请将内容按语义分割成小段，每一段要求有它的catagory属性，回复要求json格式，" +
                        "如[msg:\"msg\",catagory:\"catagory\"]|[...]，某些流程类的要求有简略过程总结，回复不要有其他内容，只需要这个结果").call().content();

        ret = trimSquareBrackets(ret);

        // 解析并转换
        documents = parseJsonToDocuments(ret);

        // 可选：打印结果验证
        for (Document doc : documents) {
            System.out.println("Metadata: " + doc.getMetadata());
            System.out.println("Content: " + doc.getFormattedContent());
            System.out.println("--------------------------");
        }

        addDocumentsToVectorStore(documents);
    }

    public void addDocumentsToVectorStore(List<Document> documents) {
        final int BATCH_SIZE = 10; // 定义每批次的最大大小

        for (int i = 0; i < documents.size(); i += BATCH_SIZE) {
            // 计算当前批次的结束索引
            int end = Math.min(i + BATCH_SIZE, documents.size());
            // 获取当前批次的子列表
            List<Document> batch = documents.subList(i, end);
            // 将当前批次添加到向量库中
            vectorStore.add(batch);
        }
    }

}
