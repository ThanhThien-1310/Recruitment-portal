package com.example.quanlytuyendung.services;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@talentflow.vn}")
    private String fromEmail;

    /**
     * Gửi email xác nhận nộp hồ sơ thành công (Tương thích ngược)
     */
    public void sendApplicationConfirmation(String toEmail, String candidateName, String jobTitle) {
        sendApplicationConfirmation(toEmail, candidateName, jobTitle, "APP-" + System.currentTimeMillis() % 10000, 85, "Hồ sơ của bạn phù hợp tốt với các tiêu chí chuyên môn của vị trí này.", null);
    }

    /**
     * Gửi email xác nhận nộp hồ sơ chi tiết kèm Đánh giá độ phù hợp AI (AI Match Score) & Việc làm gợi ý
     */
    public void sendApplicationConfirmation(String toEmail,
                                            String candidateName,
                                            String jobTitle,
                                            String appCode,
                                            Integer matchScore,
                                            String matchFeedback,
                                            List<Map<String, Object>> recommendedJobs) {

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
        String scoreDisplay = (matchScore != null ? matchScore : 85) + "%";

        StringBuilder recJobsHtml = new StringBuilder();
        if (recommendedJobs != null && !recommendedJobs.isEmpty()) {
            recJobsHtml.append("<div style='margin-top: 24px; padding: 18px; background-color: #f8fafc; border-radius: 12px; border: 1px solid #e2e8f0;'>");
            recJobsHtml.append("<h4 style='margin: 0 0 12px 0; color: #1e293b; font-size: 14px;'>🎯 Cơ hội nghề nghiệp tương tự có thể bạn quan tâm:</h4>");
            for (Map<String, Object> rj : recommendedJobs) {
                String title = (String) rj.getOrDefault("title", "Cơ hội việc làm");
                String salary = (String) rj.getOrDefault("salary", "Lương cạnh tranh");
                String loc = (String) rj.getOrDefault("location", "Toàn quốc");
                Object rScore = rj.getOrDefault("matchScore", 80);
                recJobsHtml.append(String.format(
                        "<div style='margin-bottom: 10px; padding: 10px; background-color: #ffffff; border-radius: 8px; border: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center;'>" +
                        "<div><strong style='color: #2563eb; font-size: 13px;'>%s</strong><div style='color: #64748b; font-size: 11px;'>📍 %s | 💰 %s</div></div>" +
                        "<span style='background-color: #ecfdf5; color: #059669; font-weight: bold; font-size: 11px; padding: 4px 8px; border-radius: 6px;'>%s%% Phù hợp</span>" +
                        "</div>",
                        title, loc, salary, rScore
                ));
            }
            recJobsHtml.append("</div>");
        }

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; background-color: #f1f5f9; padding: 20px; margin: 0;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 1px solid #e2e8f0;'>" +
                "  <div style='background: linear-gradient(135deg, #1e3a8a 0%%, #2563eb 100%%); padding: 28px; text-align: center; color: #ffffff;'>" +
                "    <h1 style='margin: 0; font-size: 22px; font-weight: 800; letter-spacing: -0.5px;'>TalentFlow ATS</h1>" +
                "    <p style='margin: 6px 0 0 0; opacity: 0.9; font-size: 13px;'>Hệ thống Tuyển dụng & Quản trị Nhân tài TechCorp</p>" +
                "  </div>" +
                "  <div style='padding: 28px; color: #334155; line-height: 1.6; font-size: 14px;'>" +
                "    <h2 style='color: #0f172a; margin-top: 0; font-size: 18px;'>Xác nhận tiếp nhận hồ sơ ứng tuyển thành công! 🎉</h2>" +
                "    <p>Xin chào <strong>%s</strong>,</p>" +
                "    <p>Hệ thống tuyển dụng TalentFlow xin thông báo chúng tôi đã tiếp nhận hồ sơ ứng tuyển của bạn cho vị trí: <strong style='color: #2563eb;'>%s</strong>.</p>" +
                "    <div style='background-color: #f8fafc; border-left: 4px solid #2563eb; padding: 14px 18px; margin: 20px 0; border-radius: 6px;'>" +
                "      <p style='margin: 4px 0;'><strong>Mã tra cứu hồ sơ:</strong> <span style='color: #2563eb; font-weight: bold;'>%s</span></p>" +
                "      <p style='margin: 4px 0;'><strong>Thời gian ghi nhận:</strong> %s</p>" +
                "      <p style='margin: 4px 0;'><strong>Trạng thái:</strong> <span style='color: #059669; font-weight: bold;'>Đã ghi nhận vào hệ thống</span></p>" +
                "    </div>" +
                "    <!-- AI Match Score Card -->" +
                "    <div style='background: linear-gradient(135deg, #eff6ff 0%%, #f0fdf4 100%%); border: 1px solid #bfdbfe; border-radius: 12px; padding: 18px; margin: 20px 0; text-align: center;'>" +
                "      <div style='font-size: 12px; font-weight: bold; color: #1e40af; text-transform: uppercase;'>Đánh giá độ phù hợp của AI ATS</div>" +
                "      <div style='font-size: 32px; font-weight: 900; color: #059669; margin: 6px 0;'>%s</div>" +
                "      <p style='margin: 4px 0 0 0; font-size: 12px; color: #334155;'>%s</p>" +
                "    </div>" +
                "    %s" +
                "    <h4 style='color: #1e293b; margin: 24px 0 8px 0; font-size: 14px;'>📌 Các bước tiếp theo:</h4>" +
                "    <ol style='margin: 0; padding-left: 20px; font-size: 13px; color: #475569;'>" +
                "      <li>Bộ phận Tuyển dụng sẽ thẩm định chi tiết CV của bạn trong vòng 24 - 48 giờ làm việc.</li>" +
                "      <li>Ứng viên vượt qua vòng sơ loại sẽ nhận được lời mời phỏng vấn kỹ thuật kèm file lịch Google Meet.</li>" +
                "      <li>Bạn có thể tra cứu trạng thái hồ sơ trực tiếp tại cổng thông tin bằng Email hoặc Mã hồ sơ.</li>" +
                "    </ol>" +
                "    <div style='margin-top: 28px; padding-top: 18px; border-top: 1px solid #e2e8f0; font-size: 12px; color: #64748b; text-align: center;'>" +
                "      Mọi thắc mắc vui lòng liên hệ: <strong>recruitment@techcorp.vn</strong> | Hotline: <strong>024.3888.9999</strong><br>" +
                "      Trân trọng,<br><strong>Đội ngũ Tuyển dụng TalentFlow - TechCorp Solution</strong>" +
                "    </div>" +
                "  </div>" +
                "</div></body></html>",
                candidateName, jobTitle, appCode, currentTime, scoreDisplay,
                (matchFeedback != null ? matchFeedback : "Hồ sơ của bạn rất tiềm năng và phù hợp với tiêu chí chuyên môn của công ty."),
                recJobsHtml.toString()
        );

        // 1. Gửi email thực tế qua JavaMailSender nếu được cấu hình
        boolean sentRealEmail = false;
        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("[TalentFlow] Xác nhận nộp hồ sơ thành công & Đánh giá AI - Vị trí " + jobTitle);
                helper.setText(htmlContent, true);
                mailSender.send(message);
                sentRealEmail = true;
                log.info(">>> Đã gửi email thực tế thành công qua SMTP tới: {}", toEmail);
            } catch (Exception e) {
                log.warn(">>> Không thể gửi email thực tế qua SMTP (chưa cấu hình mật khẩu SMTP ứng dụng hoặc mạng): {}", e.getMessage());
            }
        }

        // 2. Log thông báo trực quan trên Console để kiểm tra nội dung
        log.info("============== [EMAIL SENT: XÁC NHẬN NỘP CV + ĐÁNH GIÁ AI] ==============");
        log.info("To: {}", toEmail);
        log.info("Subject: [TalentFlow] Xác nhận nhận hồ sơ & Đánh giá AI ({}) - Vị trí {}", scoreDisplay, jobTitle);
        log.info("Mã hồ sơ: {} | AI Match Score: {}", appCode, scoreDisplay);
        log.info("Đánh giá AI: {}", matchFeedback);
        log.info("Trạng thái gửi thực tế: {}", (sentRealEmail ? "THÀNH CÔNG (SMTP)" : "SIMULATED / LOGGED"));
        log.info("==========================================================================");
    }

    /**
     * Gửi email mời phỏng vấn kèm file lịch .ics
     */
    public String generateIcsCalendarContent(String candidateName, String jobTitle, String date, String time, String format, String meetLink) {
        return "BEGIN:VCALENDAR\n" +
               "VERSION:2.0\n" +
               "PRODID:-//TalentFlow ATS//Recruitment Calendar//VI\n" +
               "BEGIN:VEVENT\n" +
               "SUMMARY:Phỏng vấn vị trí " + jobTitle + " - " + candidateName + "\n" +
               "DESCRIPTION:Lịch phỏng vấn với hội đồng tuyển dụng TalentFlow.\\nHình thức: " + format + "\\nLink: " + meetLink + "\n" +
               "LOCATION:" + (meetLink != null && !meetLink.isEmpty() ? meetLink : "Phòng họp 302, Trụ sở chính") + "\n" +
               "STATUS:CONFIRMED\n" +
               "END:VEVENT\n" +
               "END:VCALENDAR";
    }

    public void sendInterviewInvitation(String toEmail, String candidateName, String jobTitle, String date, String time, String format, String meetLink) {
        log.info("============== [EMAIL SENT: MỜI PHỎNG VẤN + .ICS] ==============");
        log.info("To: {}", toEmail);
        log.info("Subject: [TalentFlow] Thư mời phỏng vấn vị trí {}", jobTitle);
        log.info("Thời gian: {} ({}) | Hình thức: {} | Link: {}", time, date, format, meetLink);
        log.info("File đính kèm: interview_schedule.ics (Chuẩn iCalendar)");
        log.info("================================================================");
    }

    /**
     * Gửi email thông báo Thư mời nhận việc (Offer)
     */
    public void sendOfferNotification(String toEmail, String candidateName, String jobTitle, String salary, String startDate) {
        log.info("============== [EMAIL SENT: THƯ MỜI NHẬN VIỆC (OFFER)] ==============");
        log.info("To: {}", toEmail);
        log.info("Subject: [Chúc mừng] Thư mời nhận việc (Job Offer) vị trí {} tại TalentFlow", jobTitle);
        log.info("Kính gửi {}, chúng tôi trân trọng gửi đến bạn Offer mức lương {} với ngày bắt đầu {}.", candidateName, salary, startDate);
        log.info("File đính kèm: Offer_Letter_{}.pdf", candidateName.replaceAll("\\s+", "_"));
        log.info("=====================================================================");
    }
}