package com.waither.userservice.service.commandService;

import com.waither.userservice.converter.SurveyConverter;
import com.waither.userservice.dto.request.SurveyReqDto;
import com.waither.userservice.entity.*;
import com.waither.userservice.entity.enums.Season;
import com.waither.userservice.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public void createSurvey(User user, SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        Survey survey = SurveyConverter.toSurvey(surveyRequestDto, getTemp(surveyRequestDto.time()), getCurrentSeason());
        survey.setUser(user);
        surveyRepository.save(survey);
    }

    public static double calculateMedian(double value1, double value2) {
        return (value1 + value2) / 2;
    }

    // Todo: 해당 시각의 체감 온도 받아오기 (Weather-Service)
    public Double getTemp(LocalDateTime time) {
        return 18.0;
    }

    // Todo: 봄여름가을겨울 기준 변경 가능성 있음
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

}
