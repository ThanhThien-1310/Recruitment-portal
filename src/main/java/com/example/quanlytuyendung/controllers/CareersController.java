package com.example.quanlytuyendung.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/careers")
public class CareersController {

    private final RecruitmentController recruitmentController;

    public CareersController(RecruitmentController recruitmentController) {
        this.recruitmentController = recruitmentController;
    }

    // Cổng thông tin tuyển dụng & việc làm công khai (Job Board)
    @GetMapping({"", "/", "/jobs", "/track", "/exam", "/job/{id}", "/assessment/{token}"})
    public String publicJobBoard() {
        return "forward:/job_board.html";
    }

    // API nộp bài test trực tiếp qua Web
    @ResponseBody
    @PostMapping("/api/submit-assessment")
    public Map<String, Object> submitAssessment(@RequestBody(required = false) Map<String, Object> submission) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("message", "Bài thi của bạn đã được nộp thành công! Hệ thống đã ghi nhận kết quả và thông báo đến hội đồng tuyển dụng.");
        res.put("submittedAt", new Date().toString());
        return res;
    }

    // API nộp CV mở (Open Application)
    @ResponseBody
    @PostMapping("/api/open-apply")
    public Map<String, Object> openApply(@RequestBody(required = false) Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>();
        String appCode = "APP-" + (2026) + "-" + (new Random().nextInt(899) + 100);
        res.put("status", "SUCCESS");
        res.put("applicationCode", appCode);
        res.put("message", "Hồ sơ của bạn đã được tiếp nhận vào Talent Pool của TechCorp! Mã hồ sơ của bạn là: " + appCode);
        return res;
    }
}
