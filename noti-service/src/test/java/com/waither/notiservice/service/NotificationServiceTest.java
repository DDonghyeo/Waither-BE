package com.waither.notiservice.service;

import com.waither.notiservice.api.response.NotificationResponse;
import com.waither.notiservice.domain.Notification;
import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.enums.Season;
import com.waither.notiservice.repository.jpa.NotificationRepository;
import com.waither.notiservice.repository.jpa.UserDataRepository;
import com.waither.notiservice.repository.jpa.UserMedianRepository;
import com.waither.notiservice.repository.redis.NotificationRecordRepository;
import com.waither.notiservice.utils.TemperatureUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class NotificationServiceTest {


    NotificationService notificationService;

    @MockBean // 가짜 객체 주입
    UserMedianRepository userMedianRepository;

    @MockBean
    UserDataRepository userDataRepository;

    @MockBean
    NotificationRepository notificationRepository;

    @MockBean
    NotificationRecordRepository notificationRecordRepository;

    @BeforeEach
    void setUp() {
        //가짜 객체 주입
        notificationService = new NotificationService(notificationRepository, userMedianRepository, userDataRepository, notificationRecordRepository);
    }

    @Test
    @DisplayName("외출 알림 생성 테스트")
    void goOutAlarm() {

        //Given
        System.out.println("DB Mock 데이터 생성.. userid : 0");

        String tempEmail = "serviceTest@gmail.com";
        Season currentSeason = TemperatureUtils.getCurrentSeason();

        UserData newUser = UserData.builder()
                .email(tempEmail)
                .nickName("추워하는 곰탱이")
                .climateAlert(true)
                .regionReport(true)
                .snowAlert(true)
                .userAlert(true)
                .windAlert(true)
                .windDegree(0)
                .build();
        Mockito.when(userDataRepository.findByEmail(tempEmail)).thenReturn(Optional.of(newUser)); // (Mock) find시 Return

        UserMedian newUserMedian = UserMedian.builder()
                        .email(tempEmail)
                        .medianOf1And2(12.0)
                        .medianOf2And3(15.0)
                        .medianOf3And4(20.0)
                        .medianOf4And5(25.0)
                        .season(TemperatureUtils.getCurrentSeason()) //현재 계절로 저장
                        .build();
        Mockito.when(userMedianRepository.findByEmailAndSeason(tempEmail, currentSeason)).thenReturn(Optional.of(newUserMedian)); // (Mock) find시 Return

        //when
        String resultMessage = notificationService.sendGoOutAlarm(tempEmail);

        //then
        System.out.println("[ Notification Service Test ] result Message --> "+resultMessage);
        assertThat(userDataRepository.findByEmail(tempEmail)).isNotNull();
        assertThat(userMedianRepository.findByEmailAndSeason(tempEmail, currentSeason)).isNotNull();
        assertThat(resultMessage).isNotBlank();
    }

    @Test
    @DisplayName("알림 조회 테스트")
    void getAlarm() {
        //given
        String tempEmail = "serviceTest@gmail.com";
        Notification newNotification = Notification.builder()
                .email(tempEmail)
                .title("test title")
                .content("test content")
                .build();
        notificationRepository.save(newNotification);
        Mockito.when(notificationRepository.findAllByEmail(tempEmail)).thenReturn(List.of(newNotification)); // (Mock) find시 Return

        //when
        List<NotificationResponse> notifications = notificationService.getNotifications(tempEmail);

        //then
        assertEquals(1, notifications.size()); // 예상되는 알림 개수가 맞는지 확인
        NotificationResponse notification = notifications.get(0);
        assertEquals("test content", notification.getMessage()); // 내용이 예상과 일치하는지 확인



    }
}
