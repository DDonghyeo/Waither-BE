package com.waither.weatherservice.openapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForeCastOpenApiResponse {
	private Response response;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Response {
		private Header header;
		private Body body;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Body {
		private String dataType;
		private Items items;
		private int pageNo;
		private int numOfRows;
		private int totalCount;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Items {
		private List<Item> item;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Item {
		private String baseDate;
		private String baseTime;
		private String category;
		private String fcstDate;
		private String fcstTime;
		private String fcstValue;
		private int nx;
		private int ny;

		public String toString() {
			return "Item{" +
				"baseDate='" + baseDate + '\'' +
				", baseTime='" + baseTime + '\'' +
				", category='" + category + '\'' +
				", fcstDate='" + fcstDate + '\'' +
				", fcstTime='" + fcstTime + '\'' +
				", fcstValue='" + fcstValue + '\'' +
				", nx=" + nx +
				", ny=" + ny +
				'}';
		}
	}
}
