package com.waither.weatherservice.openapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiUtil {

	public static final String ENCODING = "UTF-8";
	public static final String RESPONSE_EXCEPTION_MSG = "Response is null";
	@Value("${openapi.forecast.key}")
	private String forecastKey;
	@Value("${openapi.disasterMsg.key}")
	private String disasterMsgKey;

	@Value("${openapi.accuweather.key}")
	private String accuweatherKey;

	// 기상청 Api (초단기, 단기)
	public List<ForeCastOpenApiResponse.Item> callForeCastApi(
		int nx,
		int ny,
		String baseDate,
		String baseTime,
		int numOfRows,
		String apiUrl
	) throws URISyntaxException {

		int pageNo = 1;
		String dataType = "JSON";

		WebClient webClient = WebClient.create();
		String uriString = apiUrl +
			"?serviceKey=" + forecastKey +
			"&numOfRows=" + numOfRows +
			"&pageNo=" + pageNo +
			"&dataType=" + dataType +
			"&base_date=" + baseDate +
			"&base_time=" + baseTime +
			"&nx=" + nx +
			"&ny=" + ny;

		URI uri = new URI(uriString);

		log.info("[*] 기상청 Api : {}", uri);

		ForeCastOpenApiResponse.Response response = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(ForeCastOpenApiResponse.class)
			.onErrorResume(throwable -> {
				throw new OpenApiException(RESPONSE_EXCEPTION_MSG);
			})
			.block().getResponse();

		if (response.getHeader().getResultCode().equals("00")) {
			return response.getBody().getItems().getItem();
		} else {
			throw new OpenApiException(response.getHeader().getResultMsg());
		}
	}

	// 기상청 OpenApi 반환값 팔터링 작업 (리스트, 반환받은 날짜 + 시간 기준으로 오름차순)
	public List<String> apiResponseListFilter(List<ForeCastOpenApiResponse.Item> items, String category) {
		return items.stream()
			.filter(item -> item.getCategory().equals(category))
			.sorted(Comparator.comparing(item -> item.getFcstDate() + item.getFcstTime()))
			.map(ForeCastOpenApiResponse.Item::getFcstValue)
			.toList();
	}

	// 기상청 OpenApi 반환값 추출 작업 (가장 가까운 시간대의 값 추출)
	public String apiResponseStringFilter(List<ForeCastOpenApiResponse.Item> items, String category) {
		return items.stream()
			.sorted(Comparator.comparing(item -> item.getFcstDate() + item.getFcstTime()))
			.filter(item -> item.getCategory().equals(category))
			.map(ForeCastOpenApiResponse.Item::getFcstValue)
			.findFirst().orElse(null);
	}

	// 재난 문자 Api
	public List<MsgOpenApiResponse.RowData> callDisasterMsgApi(String location) throws
		URISyntaxException,
		IOException {

		WebClient webClient = WebClient.create();

		String uriString = "http://apis.data.go.kr/1741000/DisasterMsg4/getDisasterMsg2List" +
			"?" + URLEncoder.encode("serviceKey", ENCODING) + "=" + disasterMsgKey +
			"&" + URLEncoder.encode("pageNo", ENCODING) + "=" + URLEncoder.encode("1", ENCODING) +
			"&" + URLEncoder.encode("numOfRows", ENCODING) + "=" + URLEncoder.encode("2", ENCODING) +
			"&" + URLEncoder.encode("type", ENCODING) + "=" + URLEncoder.encode("json", ENCODING) +
			"&" + URLEncoder.encode("location_name", ENCODING) + "=" + URLEncoder.encode(location, ENCODING);

		URI uri = new URI(uriString);

		log.info("[*] 재난 문자 Api : {}", uri);

		String responseString = webClient.get()
			.uri(uri)
			.headers(httpHeaders -> {
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				httpHeaders.set("Accept", "*/*;q=0.9");
			})
			.retrieve().bodyToMono(String.class)
			.onErrorResume(throwable -> {
				throw new OpenApiException(RESPONSE_EXCEPTION_MSG);
			})
			.block();

		ObjectMapper objectMapper = new ObjectMapper();
		MsgOpenApiResponse response = objectMapper.readValue(responseString, MsgOpenApiResponse.class);

		if (response.getDisasterMsg().get(0).getHead().get(2).getResultData().getResultCode().equals("INFO-0")) {
			return response.getDisasterMsg().get(1).getRow();
		} else {
			String resultMsg = response.getDisasterMsg().get(0).getHead().get(2).getResultData().getResultMsg();
			throw new OpenApiException(resultMsg);
		}
	}

	// TODO 재난 문자 필터 (날씨 정보만 추출)

	public List<AirKoreaOpenApiResponse.Items> callAirKorea(String searchDate) throws URISyntaxException {
		int pageNo = 1;
		int numOfRows = 10;
		String dataType = "JSON";

		WebClient webClient = WebClient.create();
		String uriString = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth" +
			"?serviceKey=" + forecastKey +
			"&numOfRows=" + numOfRows +
			"&pageNo=" + pageNo +
			"&returnType=" + dataType +
			"&searchDate=" + searchDate;

		URI uri = new URI(uriString);

		log.info("[*] 에어코리아 Api : {}", uri);

		AirKoreaOpenApiResponse.Response response = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(AirKoreaOpenApiResponse.class)
			.blockOptional().orElseThrow(() -> new OpenApiException(RESPONSE_EXCEPTION_MSG)).getResponse();

		if (response.getHeader().getResultCode().equals("00")) {

			List<AirKoreaOpenApiResponse.Items> items = response.getBody()
				.getItems()
				.stream()
				.sorted(Comparator.comparing(AirKoreaOpenApiResponse.Items::getInformData).reversed())
				.toList();

			String data = items.get(0).getInformGrade();

			parseAirKoreaResponseToMap(data);

			return items;
		} else {
			throw new OpenApiException(response.getHeader().getResultMsg());
		}
	}

	public Map<String, String> parseAirKoreaResponseToMap(String data) {
		Map<String, String> map = Arrays.stream(data.split(","))
			.map(s -> s.split(" : "))
			.collect(HashMap::new, (m, arr) -> m.put(arr[0], arr[1]), HashMap::putAll);

		// TODO 삭제 예정
		map.forEach((key, value) -> log.info(key + " -> " + value));

		return map;
	}

	// Accuweather Api 호출
	public String callAccuweatherLocationApi(double latitude, double longitude) throws
		URISyntaxException,
		JsonProcessingException {

		WebClient webClient = WebClient.create();
		String uriString = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search" +
			"?apikey=" + accuweatherKey +
			"&q=" + latitude + "," + longitude +
			"&language=" + "ko-kr" +
			"&details=" + "false" +
			"&toplevel=" + "false";

		URI uri = new URI(uriString);

		log.info("[*] Accuweather Location Api : {}", uri);

		String jsonString = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(String.class)
			.onErrorResume(throwable -> {
				throw new OpenApiException(RESPONSE_EXCEPTION_MSG);
			})
			.block();

		ObjectMapper objectMapper = new ObjectMapper();
		AccuweatherLocationApiResponse response = objectMapper.readValue(jsonString,
			AccuweatherLocationApiResponse.class);

		log.info("[*] 위도: " + latitude + " 경도: " + longitude + " -> 지역명 : {}",
			response.getAdministrativeArea().getLocalizedName());
		return response.getAdministrativeArea().getLocalizedName();
	}
}
