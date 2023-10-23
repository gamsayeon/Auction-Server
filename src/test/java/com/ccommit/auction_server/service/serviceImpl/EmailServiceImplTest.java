package com.ccommit.auction_server.service.serviceImpl;

import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("EmailServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @InjectMocks
    private EmailServiceImpl emailService;
    @Mock
    private SdkHttpMetadata httpMetadata;
    @Mock
    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Test
    @DisplayName("이메일 인증 메일 전송 성공 테스트")
    void sendTokenToUser() {
        //given
        SendEmailResult successResult = new SendEmailResult();
        when(httpMetadata.getHttpStatusCode()).thenReturn(200);
        successResult.setSdkHttpMetadata(httpMetadata);
        when(amazonSimpleEmailService.sendEmail(any(SendEmailRequest.class))).thenReturn(successResult);

        //when
        emailService.sendTokenToUser(any(String.class), "test@example.co.kr");

        //then
        assertEquals(200, successResult.getSdkHttpMetadata().getHttpStatusCode());
    }

    @Test
    @DisplayName("이메일 알림 성공 테스트")
    void notifyAuction() {
        //given
        SendEmailResult successResult = new SendEmailResult();
        when(httpMetadata.getHttpStatusCode()).thenReturn(200);
        successResult.setSdkHttpMetadata(httpMetadata);
        when(amazonSimpleEmailService.sendEmail(any(SendEmailRequest.class))).thenReturn(successResult);

        //when, then
        assertDoesNotThrow(() -> emailService.notifyAuction("recipient@example.com", "Test Subject", "Test Message"));
    }

}