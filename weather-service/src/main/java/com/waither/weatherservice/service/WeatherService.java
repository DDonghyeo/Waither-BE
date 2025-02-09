package com.waither.weatherservice.service;

import static com.waither.weatherservice.utills.DateTimeUtils.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waither.weatherservice.dto.response.MainWeatherResponse;
import com.waither.weatherservice.entity.DailyWeather;
import com.waither.weatherservice.entity.ExpectedWeather;
import com.waither.weatherservice.entity.Region;
import com.waither.weatherservice.entity.WeatherAdvisory;
import com.waither.weatherservice.exception.WeatherExceptionHandler;
import com.waither.weatherservice.utills.GpsTransferUtils;
import com.waither.weatherservice.kafka.KafkaMessage;
import com.waither.weatherservice.kafka.Producer;
import com.waither.weatherservice.openapi.ForeCastOpenApiResponse;
import com.waither.weatherservice.openapi.MsgOpenApiResponse;
import com.waither.weatherservice.openapi.OpenApiUtils;
import com.waither.weatherservice.repository.DailyWeatherRepository;
import com.waither.weatherservice.repository.ExpectedWeatherRepository;
import com.waither.weatherservice.repository.RegionRepository;
import com.waither.weatherservice.repository.WeatherAdvisoryRepository;
import com.waither.weatherservice.response.WeatherErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeatherService {

	private final OpenApiUtils openApiUtil;
	private final DailyWeatherRepository dailyWeatherRepository;
	private final ExpectedWeatherRepository expectedWeatherRepository;
	private final WeatherAdvisoryRepository weatherAdvisoryRepository;
	private final RegionRepository regionRepository;
	private final Producer producer;

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

		List<Region> region = regionRepository.findRegionByXAndY(item.getNx(), item.getNy());
		String regionName = region.get(0).getRegionName();
		String key = regionName + "_" + item.getFcstDate() + "_" + item.getFcstTime();

		ExpectedWeather expectedWeather = ExpectedWeather.builder()
			.id(key)
			.expectedTemp(expectedTempList)
			.expectedRain(expectedRainList)
			.expectedPty(expectedPtyList)
			.expectedSky(expectedSkyList)
			.build();

		String content = String.join(",", expectedWeather.getExpectedRain());
		KafkaMessage kafkaMessage = KafkaMessage.of(regionName, content);
		producer.produceMessage("alarm-rain", kafkaMessage);

		ExpectedWeather save = expectedWeatherRepository.save(expectedWeather);
		log.info("[*] 예상 기후 : {}", save);
	}

	public void createDailyWeather(
		int nx,
		int ny,
		String baseDate,
		String baseTime
	) throws URISyntaxException {

		// Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 업데이트 (1일 8회)
		List<ForeCastOpenApiResponse.Item> items = openApiUtil.callForeCastApi(nx, ny, baseDate, baseTime, 350,
			"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");

		String pop = openApiUtil.apiResponseStringFilter(items, "POP");
		String tmp = openApiUtil.apiResponseStringFilter(items, "TMP");
		String tmn = openApiUtil.apiResponseStringFilter(items, "TMN");
		String tmx = openApiUtil.apiResponseStringFilter(items, "TMX");
		String reh = openApiUtil.apiResponseStringFilter(items, "REH");
		String vec = openApiUtil.apiResponseStringFilter(items, "VEC");
		String wsd = openApiUtil.apiResponseStringFilter(items, "WSD");

		ForeCastOpenApiResponse.Item item = items.get(0);

		List<Region> region = regionRepository.findRegionByXAndY(item.getNx(), item.getNy());
		String regionName = region.get(0).getRegionName();
		String key = regionName + "_" + item.getFcstDate() + "_" + item.getFcstTime();

		DailyWeather dailyWeather = DailyWeather.builder()
			.id(key)
			.pop(pop)
			.tmp(tmp)
			.tempMin(tmn)
			.tempMax(tmx)
			.humidity(reh)
			.windVector(vec)
			.windDegree(wsd)
			.build();

		KafkaMessage kafkaMessage = KafkaMessage.of(regionName, dailyWeather.getWindDegree());
		producer.produceMessage("alarm-wind", kafkaMessage);

		DailyWeather save = dailyWeatherRepository.save(dailyWeather);
		log.info("[*] 하루 온도 : {}", save);

	}

	public void createWeatherAdvisory(double latitude, double longitude) throws URISyntaxException, IOException {
		LocalDate now = LocalDate.now();
		String today = convertLocalDateToString(now);

		String location = GpsTransferUtils.convertGpsToRegionCode(latitude, longitude);

		List<MsgOpenApiResponse.Item> items = openApiUtil.callAdvisoryApi(location, today);

		String msg = items.get(0).getTitle();

		String key = location + "_" + today;
		WeatherAdvisory weatherAdvisory = WeatherAdvisory.builder()
			.id(key)
			.message(msg)
			.build();

		KafkaMessage kafkaMessage = KafkaMessage.of(location, msg);
		producer.produceMessage("alarm-climate", kafkaMessage);

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
		LocalDateTime now = LocalDateTime.now();

		List<Region> region = regionRepository.findRegionByLatAndLong(latitude, longitude);

		if (region.isEmpty())
			throw new WeatherExceptionHandler(WeatherErrorCode.REGION_NOT_FOUND);

		String regionName = region.get(0).getRegionName();

		log.info("[Main - api] region : {}", regionName);

		String expectedWeatherKey = regionName + "_" + convertLocalDateTimeToString(now);

		LocalDateTime dailyWeatherBaseTime = convertLocalDateTimeToDailyWeatherTime(now.minusHours(1));

		String dailyWeatherKey = regionName + "_" + convertLocalDateTimeToString(dailyWeatherBaseTime);

		log.info("[Main - api] dailyWeatherKey : {}", dailyWeatherKey);
		log.info("[Main - api] expectedWeatherKey : {}", expectedWeatherKey);

		DailyWeather dailyWeather = dailyWeatherRepository.findById(dailyWeatherKey)
			.orElseThrow(() -> new WeatherExceptionHandler(WeatherErrorCode.DAILY_NOT_FOUND));

		log.info(regionName + "[Main - api] DailyWeather : {}", dailyWeather);

		ExpectedWeather expectedWeather = expectedWeatherRepository.findById(expectedWeatherKey)
			.orElseThrow(() -> new WeatherExceptionHandler(WeatherErrorCode.EXPECTED_NOT_FOUND));

		log.info(regionName + "[Main - api] ExpectedWeather : {}", expectedWeather);

		MainWeatherResponse weatherMainResponse = MainWeatherResponse.from(
			dailyWeather.getPop(), dailyWeather.getTmp(), dailyWeather.getTempMin(),
			dailyWeather.getTempMax(), dailyWeather.getHumidity(),
			dailyWeather.getWindVector(), dailyWeather.getWindDegree(),
			expectedWeather.getExpectedTemp(),
			expectedWeather.getExpectedRain(), expectedWeather.getExpectedPty(),
			expectedWeather.getExpectedSky()
		);
		log.info(regionName + "[Main - api] MainWeatherResponse : {}", weatherMainResponse);

		return weatherMainResponse;
	}

	public List<Region> getRegionList() {
		return regionRepository.findAll();
	}

	public String convertGpsToRegionName(double latitude, double longitude) {
		return regionRepository.findRegionByLatAndLong(latitude, longitude).get(0).getRegionName();
	}

	public double calculateWindChill(double temp, double wind) {
		if (temp > 10 || wind < 4.8) {
			return temp;
		}
		return 13.12 + 0.6215 * temp - 11.37 * Math.pow(wind, 0.16) + 0.3965 * temp * Math.pow(wind, 0.16);
	}

	public double getWindChill(double latitude, double longitude, LocalDateTime baseTime) {

		List<Region> region = regionRepository.findRegionByLatAndLong(latitude, longitude);
		if (region.isEmpty())
			throw new WeatherExceptionHandler(WeatherErrorCode.REGION_NOT_FOUND);

		String regionName = region.get(0).getRegionName();
		LocalDateTime dailyWeatherBaseTime = convertLocalDateTimeToDailyWeatherTime(baseTime.minusHours(1));
		String dailyWeatherKey = regionName + "_" + convertLocalDateTimeToString(dailyWeatherBaseTime);

		DailyWeather dailyWeather = dailyWeatherRepository.findById(dailyWeatherKey)
			.orElseThrow(() -> new WeatherExceptionHandler(WeatherErrorCode.DAILY_NOT_FOUND));

		return calculateWindChill(Double.valueOf(dailyWeather.getTmp()), Double.valueOf(dailyWeather.getWindDegree()));
	}
}
