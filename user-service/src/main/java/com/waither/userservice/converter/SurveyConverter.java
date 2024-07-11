package com.waither.userservice.converter;

import com.waither.userservice.dto.request.SurveyReqDto;
import com.waither.userservice.entity.*;
import com.waither.userservice.entity.enums.Season;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.waither.userservice.global.util.CalculateUtil.calculateMedian;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyConverter {

    public static Survey toSurvey(SurveyReqDto.SurveyRequestDto surveyRequestDto, Double temp, Season season) {
        return Survey.builder()
                .temp(temp)
                .ans(surveyRequestDto.ans())
                .time(surveyRequestDto.time())
                .season(season)
                .build();
    }

}
