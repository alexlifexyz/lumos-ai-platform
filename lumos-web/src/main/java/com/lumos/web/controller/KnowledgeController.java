package com.lumos.web.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lumos.api.dto.UploadDocumentResponse;
import com.lumos.core.domain.Document;
import com.lumos.core.service.KnowledgeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Knowledge Base", description = "知识库管理接口，支持文档解析与自动化入库")
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
@Validated
@Slf4j
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @Operation(summary = "上传文档", description = "上传 PDF 或其他格式文档，Agent 会自动进行解析、切片并存入向量库。")
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UploadDocumentResponse upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Received file upload request: {}, size: {}", file.getOriginalFilename(), file.getSize());
        
        // 1. 同步阶段：注册文档元数据
        Document doc = knowledgeService.registerDocument(
                file.getOriginalFilename(), 
                file.getContentType(), 
                file.getSize()
        );

        // 2. 异步阶段：启动处理流水线
        // 注意：在大规模生产环境，InputStream 应当转存到 OSS 后异步处理，这里为了简化直接传递
        knowledgeService.processDocument(doc.getId(), file.getInputStream());

        return new UploadDocumentResponse(
                doc.getUuid(),
                doc.getFilename(),
                doc.getStatus().name()
        );
    }
}
