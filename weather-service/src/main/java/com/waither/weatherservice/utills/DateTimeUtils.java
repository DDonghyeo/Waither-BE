package com.waither.weatherservice.utills;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

	public static LocalDateTime convertLocalDateTimeToDailyWeatherTime(LocalDateTime time) {

		// DailyWeather 정보는 3시간마다
		List<Integer> scheduledHours = Arrays.asList(0, 3, 6, 9, 12, 15, 18, 21);

		int currentHour = time.getHour();
		int adjustedHour = scheduledHours.stream()
			.filter(hour -> hour <= currentHour)
			.reduce((first, second) -> second)
			.orElse(scheduledHours.get(scheduledHours.size() - 1)); // 이전 날의 마지막 스케줄 시간(21시) 반환

		// 현재 시간이 첫 스케줄 시간(0시)보다 작을 경우, 전날의 마지막 스케줄 시간으로 설정
		if (currentHour < scheduledHours.get(0)) {
			time = time.minusDays(1);
		}

		return time.withHour(adjustedHour).withMinute(0).withSecond(0).withNano(0);
	}

	public static String convertLocalDateTimeToString(LocalDateTime time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = time.format(formatter);

		String[] lst = formattedDateTime.split(" ");
		String baseDate = lst[0].replace("-", "");

		String[] temp = lst[1].split(":");
		String baseTime = temp[0] + "00";

		return baseDate + "_" + baseTime;
	}

	public static String convertLocalDateToString(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return localDate.format(formatter);
	}
}
