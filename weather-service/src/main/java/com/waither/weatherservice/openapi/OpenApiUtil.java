package com.waither.weatherservice.openapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OpenApiUtil {

	@Value("${openapi.short.key}")
	private String shortForecastKey;

	@Value("${openapi.disasterMsg.key}")
	private String disasterMsgKey;

	// TODO 위도, 경도 -> x, y 좌표 변환 메서드 추가

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
			"?serviceKey=" + shortForecastKey +
			"&numOfRows=" + numOfRows +
			"&pageNo=" + pageNo +
			"&dataType=" + dataType +
			"&base_date=" + baseDate +
			"&base_time=" + baseTime +
			"&nx=" + nx +
			"&ny=" + ny;

		URI uri = new URI(uriString);

		log.info("URI : {}", uri);

		ForeCastOpenApiResponse.Response response = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(ForeCastOpenApiResponse.class)
			.blockOptional().orElseThrow(() -> new OpenApiException("Response is null")).getResponse();

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

	public void callDisasterMsgApi(String location) throws
		URISyntaxException,
		IOException {
		int pageNo = 1;
		int numOfRows = 2;
		String dataType = "JSON";

		WebClient webClient = WebClient.create();

		String uriString = "http://apis.data.go.kr/1741000/DisasterMsg4/getDisasterMsg2List" +
			"?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + disasterMsgKey +
			"&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") +
			"&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8") +
			"&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8") +
			"&" + URLEncoder.encode("location_name","UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");


		URI uri = new URI(uriString);

		log.info("URI : {}", uri);

		String responseString = webClient.get()
			.uri(uri)
			// .accept(MediaType.APPLICATION_JSON)
			.headers(httpHeaders -> {
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				httpHeaders.set("Accept", "*/*;q=0.9");
			})
			.retrieve().bodyToMono(String.class)
			.blockOptional()
			.orElseThrow(() -> new OpenApiException("Response is null"));

		log.info("response string : {}", responseString);

		ObjectMapper objectMapper = new ObjectMapper();
		MsgOpenApiResponse response = objectMapper.readValue(responseString, MsgOpenApiResponse.class);
		log.info("response : {}", response.getDisasterMsg2());
	}
}
