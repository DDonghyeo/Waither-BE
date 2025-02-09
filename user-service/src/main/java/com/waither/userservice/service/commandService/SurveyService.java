package com.waither.userservice.service.commandService;

import com.waither.userservice.converter.SurveyConverter;
import com.waither.userservice.dto.request.SurveyReqDto;
import com.waither.userservice.entity.*;
import com.waither.userservice.entity.enums.Season;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.kafka.KafkaConverter;
import com.waither.userservice.kafka.KafkaDto;
import com.waither.userservice.kafka.KafkaService;
import com.waither.userservice.repository.SurveyRepository;
import com.waither.userservice.repository.UserDataRepository;
import com.waither.userservice.repository.UserMedianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserDataRepository userDataRepository;
    private final UserMedianRepository userMedianRepository;

    private final KafkaService kafkaService;

    @Transactional
    public void createSurvey(User user, SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        Double temp = getTemp(surveyRequestDto.time());
        Survey survey = SurveyConverter.toSurvey(surveyRequestDto, temp, getCurrentSeason());
        survey.setUser(user);
        user.addSurvey(survey);

        UserData userData = userDataRepository.findByUserAndSeason(user, survey.getSeason())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_DATA_FOUND));
        UserMedian userMedian = userMedianRepository.findByUserAndSeason(user, survey.getSeason())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_MEDIAN_FOUND));

        updateUserData(userData, surveyRequestDto.ans(), temp);
        updateUserMedian(userData, userMedian);

        // Kafka 전송
        KafkaDto.UserMedianDto userMedianDto = KafkaConverter.toUserMedianDto(user, userMedian);
        kafkaService.sendUserMedian(userMedianDto);

        surveyRepository.save(survey);
    }

    private void updateUserData(UserData userData, Integer ans, Double temp) {

        double newValue = (userData.getLevel(ans) + temp) / 2 ;

        if (!isValidLevelValue(userData, ans, newValue)) {
            throw new CustomException(ErrorCode.IGNORE_SURVEY_ANSWER);
        }

        userData.updateLevelValue(ans, newValue);

        userDataRepository.save(userData);
    }

    private void updateUserMedian(UserData userData, UserMedian userMedian) {
        userMedian.updateMedianValue(userData);
        userMedianRepository.save(userMedian);
    }

    // Todo: 해당 시각의 체감 온도 받아오기 (Weather-Service로 부터)
    public Double getTemp(LocalDateTime time) {
        return 18.0;
    }

    public static Season getCurrentSeason() {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();

            // 봄, 가을 3, 4, 5, 9, 10, 11
        if ((3 <= month && month <= 5) || (9 <= month && month <= 11)) {
            return Season.SPRING_AUTUMN;
            // 여름 6, 7, 8
        } else if (6 <= month && month <= 8) {
            return Season.SUMMER;
            // 겨울 12, 1, 2
        } else {
            return Season.WINTER;
        }
    }

    // 상위, 하위 온도가 해당 온도를 넘는가
    private boolean isValidLevelValue(UserData userData, Integer ans, Double newValue) {
        Double lowerLevelValue = ans > 1 ? userData.getLevel(ans - 1) : Double.MIN_VALUE;
        Double upperLevelValue = ans < 5 ? userData.getLevel(ans + 1) : Double.MAX_VALUE;

        return newValue >= lowerLevelValue && newValue <= upperLevelValue;
    }

}
