package com.example.quanlytuyendung.services;

import com.example.quanlytuyendung.dtos.ParsedResumeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiResumeParserService {

    private static final Logger log = LoggerFactory.getLogger(AiResumeParserService.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String geminiModel;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Từ điển kỹ năng CNTT phổ biến phục vụ Smart Local Fallback
    private static final List<String> TECH_SKILLS = List.of(
            "Java", "Spring Boot", "Spring Cloud", "Hibernate", "JPA", "Microservices",
            "Python", "Django", "Flask", "FastAPI",
            "JavaScript", "TypeScript", "React", "React Native", "Vue.js", "Angular", "Node.js", "Express",
            "Flutter", "Dart", "Swift", "Kotlin", "Android", "iOS",
            "C#", ".NET Core", "ASP.NET", "C++", "Golang", "Rust", "PHP", "Laravel",
            "SQL", "MySQL", "PostgreSQL", "SQL Server", "Oracle", "MongoDB", "Redis", "Elasticsearch",
            "Docker", "Kubernetes", "Kafka", "RabbitMQ", "AWS", "Azure", "GCP", "CI/CD", "Git", "Linux",
            "RESTful API", "GraphQL", "gRPC", "OOP", "Design Pattern", "Clean Architecture",
            "Figma", "UI/UX", "Adobe XD", "Unit Test", "Jira", "Scrum", "Agile"
    );

    /**
     * Bóc tách thông tin từ chuỗi text CV trực tiếp
     */
    public ParsedResumeResponse parseResumeText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new IllegalArgumentException("Nội dung văn bản CV không được để trống!");
        }

        // 1. Thử gọi Google Gemini API nếu có API Key
        if (geminiApiKey != null && !geminiApiKey.isBlank() && !geminiApiKey.equals("YOUR_GEMINI_API_KEY")) {
            try {
                ParsedResumeResponse aiResult = callGeminiApi(rawText);
                if (aiResult != null && aiResult.fullName() != null && !aiResult.fullName().isBlank()) {
                    return aiResult;
                }
            } catch (Exception e) {
                log.warn("Gọi Gemini API không thành công ({}), tự động chuyển sang Smart Local Engine: ", e.getMessage());
            }
        }

        // 2. Fallback sang Smart Local Engine
        return parseWithSmartRuleEngine(rawText);
    }

    /**
     * Bóc tách thông tin từ file CV (PDF hoặc Text)
     */
    public ParsedResumeResponse parseResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng tải lên file CV hợp lệ (không được để trống)!");
        }

        String rawText = extractTextFromFile(file);

        if (rawText == null || rawText.isBlank()) {
            throw new IllegalArgumentException("Không thể đọc được nội dung văn bản từ file tải lên. Hãy thử file PDF hoặc TXT rõ chữ.");
        }

        // 1. Thử gọi Google Gemini API nếu có API Key
        if (geminiApiKey != null && !geminiApiKey.isBlank() && !geminiApiKey.equals("YOUR_GEMINI_API_KEY")) {
            try {
                ParsedResumeResponse aiResult = callGeminiApi(rawText);
                if (aiResult != null && aiResult.fullName() != null && !aiResult.fullName().isBlank()) {
                    return aiResult;
                }
            } catch (Exception e) {
                log.warn("Gọi Gemini API không thành công ({}), tự động chuyển sang Smart Local Engine: ", e.getMessage());
            }
        }

        // 2. Fallback sang Smart Local Engine (Regex & Heuristic NLP)
        return parseWithSmartRuleEngine(rawText);
    }

    /**
     * Trích xuất văn bản từ file (PDF hoặc Text thông thường)
     */
    private String extractTextFromFile(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        try {
            if (fileName.endsWith(".pdf")) {
                try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setSortByPosition(true);
                    return stripper.getText(document);
                }
            } else {
                // Đọc file dạng text thuần (.txt, markdown, v.v.)
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("Lỗi khi đọc file CV: ", e);
            throw new RuntimeException("Lỗi khi trích xuất nội dung từ file CV: " + e.getMessage());
        }
    }

    /**
     * Gọi Google Gemini Flash API với Structured Prompt
     */
    private ParsedResumeResponse callGeminiApi(String rawText) throws Exception {
        String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                geminiModel, geminiApiKey);

        String prompt = """
                Bạn là một chuyên gia nhân sự và hệ thống AI ATS thông minh. Hãy phân tích nội dung CV dưới đây và trích xuất thông tin thành ĐÚNG định dạng JSON sau (không thêm bất kỳ văn bản nào khác ngoài JSON):
                {
                  "fullName": "Họ và tên ứng viên",
                  "email": "Email",
                  "phone": "Số điện thoại",
                  "skills": ["Kỹ năng 1", "Kỹ năng 2", "Kỹ năng 3"],
                  "experienceYears": 3.5,
                  "education": "Tên trường đại học / cao đẳng hoặc bằng cấp cao nhất",
                  "summary": "Tóm tắt ngắn gọn 2-3 câu về năng lực nổi bật của ứng viên",
                  "suggestedRole": "Vị trí công việc phù hợp nhất (VD: Senior Java Developer, Flutter Mobile Dev...)"
                }
                
                Nội dung CV cần phân tích:
                \"\"\"
                %s
                \"\"\"
                """.formatted(truncateText(rawText, 8000));

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        "responseMimeType", "application/json"
                )
        );

        String requestJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");
            if (!textNode.isMissingNode()) {
                String aiResponseText = textNode.asText();
                // Làm sạch markdown nếu có
                aiResponseText = aiResponseText.replaceAll("```json", "").replaceAll("```", "").trim();
                JsonNode parsedJson = objectMapper.readTree(aiResponseText);

                List<String> skills = new ArrayList<>();
                if (parsedJson.has("skills") && parsedJson.get("skills").isArray()) {
                    for (JsonNode s : parsedJson.get("skills")) {
                        skills.add(s.asText());
                    }
                }

                return new ParsedResumeResponse(
                        parsedJson.path("fullName").asText(""),
                        parsedJson.path("email").asText(""),
                        parsedJson.path("phone").asText(""),
                        skills,
                        parsedJson.path("experienceYears").asDouble(0.0),
                        parsedJson.path("education").asText(""),
                        parsedJson.path("summary").asText(""),
                        parsedJson.path("suggestedRole").asText(""),
                        "GEMINI_AI (Google Flash)",
                        createPreview(rawText)
                );
            }
        } else {
            log.warn("Gemini API returned status {}: {}", response.statusCode(), response.body());
        }

        return null;
    }

    /**
     * Bóc tách thông minh bằng Regular Expression & Heuristic NLP (Không cần mạng hay API Key)
     */
    private ParsedResumeResponse parseWithSmartRuleEngine(String rawText) {
        String email = extractPattern(rawText, "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        String phone = extractPattern(rawText, "(?:\\+84|0)(?:\\s*\\d){9,10}");

        // 1. Nhận diện kỹ năng (Skills)
        Set<String> matchedSkills = new LinkedHashSet<>();
        String lowerText = rawText.toLowerCase();
        for (String skill : TECH_SKILLS) {
            Pattern p = Pattern.compile("\\b" + Pattern.quote(skill.toLowerCase()) + "\\b");
            if (p.matcher(lowerText).find()) {
                matchedSkills.add(skill);
            }
        }

        // 2. Nhận diện số năm kinh nghiệm
        Double experienceYears = 1.0;
        Pattern expPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:năm|year|years|yr|yrs)\\s*(?:kinh nghiệm|experience)?", Pattern.CASE_INSENSITIVE);
        Matcher expMatcher = expPattern.matcher(rawText);
        if (expMatcher.find()) {
            try {
                experienceYears = Double.parseDouble(expMatcher.group(1));
            } catch (Exception ignored) {}
        }

        // 3. Nhận diện học vấn (Education)
        String education = "Đại học / Chuyên ngành CNTT";
        List<String> universities = List.of(
                "Bách Khoa", "Đại học Công nghệ", "Quốc gia", "Khoa học Tự nhiên",
                "Bưu chính Viễn thông", "Kinh tế Quốc dân", "FPT", "Công nghiệp", "Giao thông Vận tải"
        );
        for (String uni : universities) {
            if (rawText.toLowerCase().contains(uni.toLowerCase())) {
                education = "ĐH " + uni;
                break;
            }
        }

        // 4. Đoán Họ tên từ các dòng đầu tiên của CV
        String fullName = "Ứng viên Tiềm năng";
        String[] lines = rawText.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() >= 3 && trimmed.length() <= 35
                    && !trimmed.toUpperCase().contains("CV")
                    && !trimmed.toUpperCase().contains("RESUME")
                    && !trimmed.toUpperCase().contains("CURRICULUM")
                    && !trimmed.toUpperCase().contains("THÔNG TIN")
                    && !trimmed.contains("@")
                    && !trimmed.matches(".*\\d{5,}.*")) {
                fullName = trimmed;
                break;
            }
        }

        // 5. Gợi ý vị trí phù hợp dựa trên skills
        String suggestedRole = "Chuyên viên Công nghệ";
        if (matchedSkills.contains("Java") || matchedSkills.contains("Spring Boot")) {
            suggestedRole = (experienceYears >= 3.0 ? "Senior" : "Junior/Mid") + " Java Backend Engineer";
        } else if (matchedSkills.contains("Flutter") || matchedSkills.contains("Dart")) {
            suggestedRole = "Flutter Mobile Developer";
        } else if (matchedSkills.contains("React") || matchedSkills.contains("Vue.js")) {
            suggestedRole = "Frontend Web Developer";
        } else if (matchedSkills.contains("Figma") || matchedSkills.contains("UI/UX")) {
            suggestedRole = "UI/UX Product Designer";
        }

        String summary = String.format("Ứng viên có định hướng %s với khoảng %.1f năm kinh nghiệm. Nắm vững %d kỹ năng công nghệ chính.",
                suggestedRole, experienceYears, matchedSkills.size());

        return new ParsedResumeResponse(
                fullName,
                email,
                phone,
                new ArrayList<>(matchedSkills),
                experienceYears,
                education,
                summary,
                suggestedRole,
                "SMART_LOCAL_ENGINE (Offline Heuristic Parser)",
                createPreview(rawText)
        );
    }

    private String extractPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(0).trim();
        }
        return "";
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    private String createPreview(String rawText) {
        String clean = rawText.replaceAll("\\s+", " ").trim();
        return clean.length() > 300 ? clean.substring(0, 300) + "..." : clean;
    }
}
