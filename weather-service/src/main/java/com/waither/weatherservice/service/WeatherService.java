package com.waither.weatherservice.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waither.weatherservice.entity.DailyWeather;
import com.waither.weatherservice.entity.ExpectedWeather;
import com.waither.weatherservice.openapi.ForeCastOpenApiResponse;
import com.waither.weatherservice.openapi.OpenApiUtil;
import com.waither.weatherservice.redis.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeatherService {

	private final OpenApiUtil openApiUtil;

	private final RedisUtil redisUtil;

	public void createExpectedWeather(
		int nx,
		int ny,
		String baseDate,
		String baseTime
	) throws URISyntaxException {

		// 1시간마다 업데이트 (1일 24회)
		List<ForeCastOpenApiResponse.Item> items = openApiUtil.callForeCastApi(nx, ny, baseDate, baseTime, 60,
			"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst");

		List<String> expectedTempList = openApiUtil.apiResponseListFilter(items, "T1H");
		List<String> expectedRainList = openApiUtil.apiResponseListFilter(items, "RN1");
		List<String> expectedPtyList = openApiUtil.apiResponseListFilter(items, "PTY");
		List<String> expectedSkyList = openApiUtil.apiResponseListFilter(items, "SKY");

		ForeCastOpenApiResponse.Item item = items.get(0);
		String key = item.getNx() + "_" + item.getNy() + "_" + item.getFcstDate() + "_" + item.getFcstTime();

		redisUtil.saveAsList(key + ":expectedTemp", expectedTempList, 6L, TimeUnit.HOURS);
		redisUtil.saveAsList(key + ":expectedRain", expectedRainList, 6L, TimeUnit.HOURS);
		redisUtil.saveAsList(key + ":expectedPty", expectedPtyList, 6L, TimeUnit.HOURS);
		redisUtil.saveAsList(key + ":expectedSky", expectedSkyList, 6L, TimeUnit.HOURS);

		// TODO 조회 테스트 후 삭제 예정
		ExpectedWeather expectedWeather = ExpectedWeather.builder()
			.expectedTemp(redisUtil.getAsList(key + ":expectedTemp"))
			.expectedRain(redisUtil.getAsList(key + ":expectedRain"))
			.expectedPty(redisUtil.getAsList(key + ":expectedPty"))
			.expectedSky(redisUtil.getAsList(key + ":expectedSky"))
			.build();

		log.info("ExpectedWeather : {}", expectedWeather);
	}

	public void createDailyWeather(int nx,
		int ny,
		String baseDate,
		String baseTime) throws URISyntaxException {

		// Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 업데이트 (1일 8회)
		List<ForeCastOpenApiResponse.Item> items = openApiUtil.callForeCastApi(nx, ny, baseDate, baseTime, 350,
			"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");

		String pop = openApiUtil.apiResponseStringFilter(items, "POP");
		String tmn = openApiUtil.apiResponseStringFilter(items, "TMN");
		String tmx = openApiUtil.apiResponseStringFilter(items, "TMX");
		String reh = openApiUtil.apiResponseStringFilter(items, "REH");
		String vec = openApiUtil.apiResponseStringFilter(items, "VEC");
		String wsd = openApiUtil.apiResponseStringFilter(items, "WSD");

		ForeCastOpenApiResponse.Item item = items.get(0);
		String key = item.getNx() + "_" + item.getNy() + "_" + item.getFcstDate() + "_" + item.getFcstTime();

		redisUtil.saveAsValue(key + "pop", pop, 8L, TimeUnit.HOURS);
		redisUtil.saveAsValue(key + "tmn", tmn, 8L, TimeUnit.HOURS);
		redisUtil.saveAsValue(key + "tmx", tmx, 8L, TimeUnit.HOURS);
		redisUtil.saveAsValue(key + "reh", reh, 8L, TimeUnit.HOURS);
		redisUtil.saveAsValue(key + "vec", vec, 8L, TimeUnit.HOURS);
		redisUtil.saveAsValue(key + "wsd", wsd, 8L, TimeUnit.HOURS);

		// TODO 조회 테스트 후 삭제 예정
		DailyWeather dailyWeather = DailyWeather.builder()
			.pop(redisUtil.getAsValue(key + "pop"))
			.tempMin(redisUtil.getAsValue(key + "tmn"))
			.tempMax(redisUtil.getAsValue(key + "tmx"))
			.humidity(redisUtil.getAsValue(key + "reh"))
			.windVector(redisUtil.getAsValue(key + "vec"))
			.windDegree(redisUtil.getAsValue(key + "wsd"))
			.build();

		log.info("{} : ", dailyWeather);

	}

	public void createDisasterMsg(String location) throws URISyntaxException, IOException {
		openApiUtil.callDisasterMsgApi(location);
	}
}
