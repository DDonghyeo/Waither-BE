package com.waither.userservice.controller;

import com.waither.userservice.dto.request.SurveyReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.global.annotation.AuthUser;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.service.commandService.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user/survey")
public class SurveyController {

    private final SurveyService surveyService;
    @PostMapping("/submit")
    public ApiResponse<String> createSurvey(@AuthUser User user, @RequestBody SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        surveyService.createSurvey(user, surveyRequestDto);
        return ApiResponse.onSuccess("survey 생성완료");
    }

//    @PostMapping("/reset")
//    public ApiResponse<String> resetServeyData(@AuthUser User user) {
//        surveyService.resetSurveyData(user);
//        return ApiResponse.onSuccess("사용자의 설문 정보를 초기화 하였습니다.");
//    }

}
