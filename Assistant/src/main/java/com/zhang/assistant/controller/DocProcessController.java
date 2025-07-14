package com.zhang.assistant.controller;

import com.zhang.assistant.common.Result;
import com.zhang.assistant.service.pgvectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class DocProcessController {

    @Autowired
    private pgvectorService pgvectorService;


    @PostMapping("/file/upload")
    public Result<String> parseFiles(@RequestParam("file") MultipartFile file)  {

        try {
            // 直接读取文件内容
            byte[] bytes = file.getBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);

            // 处理文件内容
            pgvectorService.addDocument(content);

            return Result.success("已成功上传文档！");
        } catch (IOException e) {
            return Result.error("文件上传中出现错误：" + e.getMessage());
        }
    }

}
