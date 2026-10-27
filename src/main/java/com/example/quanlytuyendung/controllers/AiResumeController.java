package com.example.quanlytuyendung.controllers;

import com.example.quanlytuyendung.dtos.ParsedResumeResponse;
import com.example.quanlytuyendung.services.AiResumeParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiResumeController {

    private final AiResumeParserService aiResumeParserService;

    public AiResumeController(AiResumeParserService aiResumeParserService) {
        this.aiResumeParserService = aiResumeParserService;
    }

    /**
     * API Bóc tách CV từ file upload (PDF hoặc TXT)
     * POST /api/ai/scan-cv
     * Form-Data: file = [file CV]
     */
    @PostMapping(value = "/scan-cv", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Object>> scanResumeFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            ParsedResumeResponse result = aiResumeParserService.parseResume(file);
            response.put("status", "SUCCESS");
            response.put("message", "Phân tích và trích xuất dữ liệu CV thành công!");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "BAD_REQUEST");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Có lỗi xảy ra trong quá trình xử lý CV: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API Bóc tách CV từ đoạn text gửi lên (tiện cho việc test nhanh trên Postman bằng JSON)
     * POST /api/ai/scan-text
     * Body: { "text": "Họ tên: Nguyễn Văn An, Email: an.nguyen@gmail.com..." }
     */
    @PostMapping(value = "/scan-text")
    public ResponseEntity<Map<String, Object>> scanResumeText(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String text = payload.get("text");
        if (text == null || text.isBlank()) {
            response.put("status", "BAD_REQUEST");
            response.put("message", "Vui lòng truyền trường 'text' chứa nội dung CV cần phân tích!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ParsedResumeResponse result = aiResumeParserService.parseResumeText(text);
            response.put("status", "SUCCESS");
            response.put("message", "Phân tích văn bản CV thành công!");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Lỗi xử lý: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
