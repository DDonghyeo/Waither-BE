package com.waither.weatherservice.openapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MsgOpenApiResponse {
	private List<MsgData> DisasterMsg2;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class MsgData {
		private List<HeadData> head;
		private List<RowData> row;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class HeadData {
		private int totalCount;
		private String numOfRows;
		private String pageNo;
		private String type;
		private ResultData RESULT;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResultData {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RowData {
		private String create_date;
		private String location_id;
		private String location_name;
		private String md101_sn;
		private String msg;
		private String send_platform;
	}
}
