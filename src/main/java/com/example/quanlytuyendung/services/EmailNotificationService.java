package com.example.quanlytuyendung.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    /**
     * Gửi email xác nhận nộp hồ sơ thành công
     */
    public void sendApplicationConfirmation(String toEmail, String candidateName, String jobTitle) {
        log.info("============== [EMAIL SENT: XÁC NHẬN NỘP CV] ==============");
        log.info("To: {}", toEmail);
        log.info("Subject: [TalentFlow] Xác nhận nhận hồ sơ ứng tuyển vị trí {}", jobTitle);
        log.info("Nội dung: Chào {}, chúng tôi đã nhận được hồ sơ của bạn cho vị trí {}. Bộ phận Tuyển dụng sẽ phản hồi trong 2-3 ngày làm việc.", candidateName, jobTitle);
        log.info("=============================================================");
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
