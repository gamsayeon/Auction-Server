package com.ccommit.auction_server.service.serviceImpl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.ccommit.auction_server.service.EmailService;
import com.ccommit.auction_server.exception.CacheTTLOutException;
import com.ccommit.auction_server.exception.EmailSendFailedException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${expireUrl}")
    private String url;
    private final Logger logger = LogManager.getLogger(EmailServiceImpl.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Cacheable(key = "#token", value = "userId")
    public String putCacheToken(String token, String userId) {
        return userId;
    }

    @Override
    public void sendTokenToUser(String token, String email) {
        Destination destination = new Destination().withToAddresses(email);
        Content subjectContent = new Content().withData("Email Verification");

        String htmlContent = "<html><body>" +
                "<h1>Amazon SES Test (HTML)</h1>" +
                "<p>This email contains a clickable link:</p>" +
                "<a href='" + url + token + "'>Click here to visit Example.com</a>" +
                "</body></html>";

        Content bodyContent = new Content().withData(htmlContent);
        Body body = new Body().withHtml(bodyContent);
        Message emailMessage = new Message().withSubject(subjectContent).withBody(body);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(destination)
                .withMessage(emailMessage)
                .withSource("aud4551@naver.com"); // 발신자 이메일 주소

        SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(request);
        if (sendEmailResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
            logger.info("[AWS SES] 메일전송완료");
        } else {
            logger.warn("email 전송 실패");
            throw new EmailSendFailedException("EMAIL_SEND_FAILED");
        }
    }

    @Override
    public void notifyAuction(String recipientEmail, String subject, String message) {
        Destination destination = new Destination().withToAddresses(recipientEmail);
        Content subjectContent = new Content().withData(subject);
        Content textBodyContent = new Content().withData(message);
        Body body = new Body().withHtml(textBodyContent);
        Message emailMessage = new Message().withSubject(subjectContent).withBody(body);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(destination)
                .withMessage(emailMessage)
                .withSource("aud4551@naver.com"); // 발신자 이메일 주소

        SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(request);
        if (sendEmailResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
            logger.info("[AWS SES] 메일전송완료");
        } else {
            logger.warn("email 전송 실패");
            throw new EmailSendFailedException("EMAIL_SEND_FAILED");
        }
    }

    public String verifyEmail(String token) {
        String cachedUserId = this.getCachedToken(token);
        if (cachedUserId != null) {
            logger.info("redis value 값을 정상적으로 가지고 왔습니다.");
            return cachedUserId;
        } else {
            logger.warn("만료시간이 지나 다시시도해주세요");
            throw new CacheTTLOutException("EMAIL_CACHE_TTL_OUT");
        }
    }

    public String getCachedToken(String token) {
        String key = "userId::" + token;
        String cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue;
    }

}