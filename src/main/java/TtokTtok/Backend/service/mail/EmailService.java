package TtokTtok.Backend.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject("[똑똑] 임시 비밀번호 안내");
            mimeMessageHelper.setText("안녕하세요. 똑똑입니다. \n\n요청하신 임시 비밀번호는 " + temporaryPassword + "입니다." +
                    "\n로그인 후 비밀번호를 변경해주세요.", false);
            javaMailSender.send(mimeMessage);
            log.info("임시 비밀번호 이메일 발송 완료: {}", toEmail);
        } catch (MessagingException e) {
            log.error("임시 비밀번호 이메일 발송 실패: {}", toEmail,e);
            throw new RuntimeException("이메일 발송에 실패했습니다.",e);
        }
    }

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject("[똑똑] 회원가입 이메일 인증 안내");

            // 여기에 프론트엔드 인증 처리 페이지 URL을 넣어주세요
            String verificationUrl = "http://localhost:8080/api/users/verify-email?email=" + toEmail + "&code=" + verificationCode;

            String emailBody = "안녕하세요. 똑똑입니다. \n\n" + "회원가입을 완료하려면 아래 링크를 클릭하여 이메일 인증을 진행해주세요.\n" + verificationUrl;

            mimeMessageHelper.setText(emailBody, false);
            javaMailSender.send(mimeMessage);
            log.info("인증 이메일 발송 완료: {}, 인증 URL: {}", toEmail, verificationUrl);
        } catch (MessagingException e) {
            log.error("인증 이메일 발송 실패: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
}