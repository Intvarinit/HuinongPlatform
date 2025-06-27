package com.zhang.huinongplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import com.zhang.huinongplatform.common.Result;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/common")
public class CommonController {

//    @Value("${file.upload-dir:logs/upload}")
//    private String uploadDir;

    @Operation(summary = "上传图片", description = "通用图片上传接口，返回图片URL")
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("仅支持图片类型");
        }
        // 生成唯一文件名
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + ext;

        // 保存到项目根目录下的upload文件夹
        String basePath = System.getProperty("user.dir") + File.separator + "upload";
        File dir = new File(basePath);
        if (!dir.exists() && !dir.mkdirs()) {
            return Result.error("目录创建失败：" + dir.getAbsolutePath());
        }
        File dest = new File(dir, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件保存失败: " + e.getMessage());
        }
        // 只返回/upload/xxx.png
        String url = "/upload/" + filename;
        return Result.success(url);
    }
} 