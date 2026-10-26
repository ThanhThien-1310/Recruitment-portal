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

    // Trang chủ tuyển dụng công khai: Lọc theo ngành nghề & khu vực
    @GetMapping({"", "/"})
    public String careerPortal(
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<RecruitmentController.JobPosting> allJobs = recruitmentController.getMockJobs();
        List<RecruitmentController.JobPosting> filtered = allJobs.stream()
                .filter(j -> department == null || department.isEmpty() || j.department().toLowerCase().contains(department.toLowerCase()))
                .filter(j -> location == null || location.isEmpty() || j.location().toLowerCase().contains(location.toLowerCase()))
                .filter(j -> keyword == null || keyword.isEmpty() || j.title().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        model.addAttribute("jobs", filtered);
        model.addAttribute("selectedDepartment", department != null ? department : "");
        model.addAttribute("selectedLocation", location != null ? location : "");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "recruitment/careers";
    }

    // Chi tiết công việc & Form nộp CV theo việc
    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable("id") Long id, Model model) {
        var job = recruitmentController.getMockJobs().stream()
                .filter(j -> j.id().equals(id))
                .findFirst()
                .orElse(recruitmentController.getMockJobs().get(0));
        model.addAttribute("job", job);
        return "recruitment/job-detail";
    }

    // Tra cứu trạng thái hồ sơ ứng tuyển
    @GetMapping("/track")
    public String trackApplication(
            @RequestParam(value = "query", required = false) String query,
            Model model) {

        RecruitmentController.Candidate foundCandidate = null;
        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            foundCandidate = recruitmentController.getMockCandidates().stream()
                    .filter(c -> c.applicationCode().toLowerCase().equals(q)
                            || c.email().toLowerCase().equals(q)
                            || c.phone().replace(" ", "").contains(q.replace(" ", "")))
                    .findFirst()
                    .orElse(null);
        }

        // Default demo candidate if no search query provided yet
        if (foundCandidate == null && (query == null || query.isEmpty())) {
            foundCandidate = recruitmentController.getMockCandidates().get(0); // Nguyễn Văn An
        }

        model.addAttribute("candidate", foundCandidate);
        model.addAttribute("query", query != null ? query : "");
        return "recruitment/track-application";
    }

    // Trang làm bài test năng lực bảo mật (Secure Assessment Portal)
    @GetMapping("/assessment/{token}")
    public String secureAssessment(
            @PathVariable("token") String token,
            Model model) {

        // Match or provide demo test
        var candidate = recruitmentController.getMockCandidates().get(0); // Nguyễn Văn An
        model.addAttribute("candidate", candidate);
        model.addAttribute("token", token);
        return "recruitment/assessment-exam";
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
