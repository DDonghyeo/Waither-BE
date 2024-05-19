package com.waither.weatherservice.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waither.weatherservice.dto.response.MainWeatherResponse;
import com.waither.weatherservice.entity.DailyWeather;
import com.waither.weatherservice.entity.ExpectedWeather;
import com.waither.weatherservice.entity.WeatherAdvisory;
import com.waither.weatherservice.exception.WeatherExceptionHandler;
import com.waither.weatherservice.gps.GpsTransfer;
import com.waither.weatherservice.gps.LatXLngY;
import com.waither.weatherservice.kafka.Producer;
import com.waither.weatherservice.openapi.ForeCastOpenApiResponse;
import com.waither.weatherservice.openapi.MsgOpenApiResponse;
import com.waither.weatherservice.openapi.OpenApiUtil;
import com.waither.weatherservice.redis.DailyWeatherRepository;
import com.waither.weatherservice.redis.ExpectedWeatherRepository;
import com.waither.weatherservice.redis.WeatherAdvisoryRepository;
import com.waither.weatherservice.response.WeatherErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeatherService {

	private final OpenApiUtil openApiUtil;
	private final DailyWeatherRepository dailyWeatherRepository;
	private final ExpectedWeatherRepository expectedWeatherRepository;
	private final WeatherAdvisoryRepository weatherAdvisoryRepository;
	private final Producer producer;
	private final GpsTransfer gpsTransfer;

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

		ExpectedWeather expectedWeather = ExpectedWeather.builder()
			.id(key)
			.expectedTemp(expectedTempList)
			.expectedRain(expectedRainList)
			.expectedPty(expectedPtyList)
			.expectedSky(expectedSkyList)
			.build();

		ExpectedWeather save = expectedWeatherRepository.save(expectedWeather);
		log.info("[*] 예상 기후 : {}", save);
	}

	public void createDailyWeather(int nx,
		int ny,
		String baseDate,
		String baseTime) throws URISyntaxException, JsonProcessingException {

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

		DailyWeather dailyWeather = DailyWeather.builder()
			.id(key)
			.pop(pop)
			.tempMin(tmn)
			.tempMax(tmx)
			.humidity(reh)
			.windVector(vec)
			.windDegree(wsd)
			.build();

		// DailyWeatherKafkaMessage kafkaMessage = DailyWeatherKafkaMessage.from(dailyWeather);

		// 바람 세기 Kafka 전송
		producer.produceMessage(wsd);

		DailyWeather save = dailyWeatherRepository.save(dailyWeather);
		log.info("[*] 하루 온도 : {}", save);

	}

	public void createWeatherAdvisory(double latitude, double longitude) throws URISyntaxException, IOException {
		LocalDate now = LocalDate.now();
		String today = openApiUtil.convertLocalDateToString(now);

		String location = gpsTransfer.convertGpsToRegionCode(latitude, longitude);

		List<MsgOpenApiResponse.Item> items = openApiUtil.callAdvisoryApi(location, today);

		String msg = items.get(0).getTitle();

		String key = location + "_" + today;
		WeatherAdvisory weatherAdvisory = WeatherAdvisory.builder()
			.id(key)
			.message(msg)
			.build();

		WeatherAdvisory save = weatherAdvisoryRepository.save(weatherAdvisory);
		log.info("[*] 기상 특보 : {}", save);
	}

	public void createAirKorea(String searchTime) throws URISyntaxException {
		openApiUtil.callAirKorea(searchTime);
	}

	public void convertLocation(double latitude, double longitude) throws URISyntaxException, JsonProcessingException {
		openApiUtil.callAccuweatherLocationApi(latitude, longitude);
	}

	public MainWeatherResponse getMainWeather(double latitude, double longitude) {
		LatXLngY latXLngY = gpsTransfer.convertGpsToGrid(latitude, longitude);

		LocalDateTime now = LocalDateTime.now();
		String key = (int)latXLngY.x() + "_" + (int)latXLngY.y() + "_" + convertLocalDateTimeToString(now);

		// 테스트 키 : "55_127_20240508_1500"

		DailyWeather dailyWeather = dailyWeatherRepository.findById(key)
			.orElseThrow(() -> new WeatherExceptionHandler(WeatherErrorCode.WEATHER_MAIN_ERROR));

		ExpectedWeather expectedWeather = expectedWeatherRepository.findById(key)
			.orElseThrow(() -> new WeatherExceptionHandler(WeatherErrorCode.WEATHER_MAIN_ERROR));

		MainWeatherResponse weatherMainResponse = MainWeatherResponse.from(
			dailyWeather.getPop(), dailyWeather.getTempMin(),
			dailyWeather.getTempMax(), dailyWeather.getHumidity(),
			dailyWeather.getWindVector(), dailyWeather.getWindDegree(),
			expectedWeather.getExpectedTemp(),
			expectedWeather.getExpectedRain(), expectedWeather.getExpectedPty(),
			expectedWeather.getExpectedSky()
		);
		log.info("{}", weatherMainResponse);

		return weatherMainResponse;
	}

	public String convertLocalDateTimeToString(LocalDateTime time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = time.format(formatter);

		String[] lst = formattedDateTime.split(" ");
		String baseDate = lst[0].replace("-", "");

		String[] temp = lst[1].split(":");
		String baseTime = temp[0] + "00";

		return baseDate + "_" + baseTime;
	}
}
