package com.waither.userservice.controller;

import com.waither.userservice.dto.request.SurveyReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.global.annotation.AuthUser;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.service.queryService.SurveyQueryService;
import com.waither.userservice.service.commandService.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/survey")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyQueryService surveyQueryService;

    @PostMapping("/submit")
    public ApiResponse<String> createSurvey(@AuthUser User user, @RequestBody SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        surveyService.createSurvey(user, surveyRequestDto);
        return ApiResponse.onSuccess("survey 생성완료");
    }

}
