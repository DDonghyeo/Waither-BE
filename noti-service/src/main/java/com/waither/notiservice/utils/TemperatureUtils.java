package com.waither.notiservice.utils;

import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.enums.Season;
import com.waither.notiservice.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.waither.notiservice.enums.Expressions.*;

@RequiredArgsConstructor
@Component
public class TemperatureUtils {

    public static Expressions[] winterExpressions = {WARM, GOOD, LITTLE_COLD, COLD, VERY_COLD };
    public static Expressions[] springAndAutumnExpressions = {HOT, LITTLE_HOT, GOOD, LITTLE_COLD, COLD};
    public static Expressions[] summerExpressions = {COOL, GOOD, LITTLE_HOT, HOT, VERY_HOT};
    public static double WEIGHT_ADJUSTMENT_FACTOR = 0.5;

    public static String createUserDataMessage(UserMedian userMedian, double temperature, double weight) {
        double medianBetween1And2 = userMedian.getMedianOf1And2() + weight * WEIGHT_ADJUSTMENT_FACTOR;
        double medianBetween2And3 = userMedian.getMedianOf2And3() + weight * WEIGHT_ADJUSTMENT_FACTOR;
        double medianBetween3And4 = userMedian.getMedianOf3And4() + weight * WEIGHT_ADJUSTMENT_FACTOR;
        double medianBetween4And5 = userMedian.getMedianOf4And5() + weight * WEIGHT_ADJUSTMENT_FACTOR;

        if (temperature < medianBetween1And2) {
            return getExpression(getCurrentSeason(), 1);
        } else if (medianBetween1And2 <= temperature && temperature < medianBetween2And3) {
            return getExpression(getCurrentSeason(), 2);
        }else if (medianBetween2And3 <= temperature && temperature < medianBetween3And4) {
            return getExpression(getCurrentSeason(), 3);
        }else if (medianBetween3And4 <= temperature && temperature < medianBetween4And5) {
            return getExpression(getCurrentSeason(), 4);
        }else if (medianBetween4And5 <= temperature) {
            return getExpression(getCurrentSeason(), 5);
        }
        return null;
    }

    public static Season getCurrentSeason() {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        if (3 <= month && month <= 5) { //봄 3, 4, 5
            return Season.SPRING;
        } else if (6 <= month && month <= 8) { //여름 6, 7, 8
            return Season.SUMMER;
        }else if (9 <= month && month <= 11) { //여름 9, 10, 11
            return Season.AUTUMN;
        } else return Season.WINTER; //겨울 12, 1, 2
    }

    public static String getExpression(Season season, int level) {
        if (season == Season.WINTER) { //겨울
            return winterExpressions[level - 1].getExpression();
        } else if (season == Season.SUMMER) { //여름
            return summerExpressions[level - 1].getExpression();
        } else { //봄, 가을
            return springAndAutumnExpressions[level - 1].getExpression();
        }
    }
}
