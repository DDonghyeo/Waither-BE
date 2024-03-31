package com.waither.weatherservice.openapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MsgOpenApiResponse {

	@JsonProperty("DisasterMsg2")
	private List<MsgData> disasterMsg;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class MsgData {
		@JsonProperty("head")
		private List<HeadData> head;
		@JsonProperty("row")
		private List<RowData> row;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class HeadData {
		@JsonProperty("totalCount")
		private int totalCount;
		@JsonProperty("numOfRows")
		private String numOfRows;
		@JsonProperty("pageNo")
		private String pageNo;
		@JsonProperty("type")
		private String type;
		@JsonProperty("RESULT")
		private ResultData resultData;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResultData {
		@JsonProperty("resultCode")
		private String resultCode;
		@JsonProperty("resultMsg")
		private String resultMsg;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RowData {
		@JsonProperty("create_date")
		private String createDate;
		@JsonProperty("location_id")
		private String locationId;
		@JsonProperty("location_name")
		private String locationName;
		@JsonProperty("md101_sn")
		private String md101Sn;
		@JsonProperty("msg")
		private String msg;
		@JsonProperty("send_platform")
		private String sendPlatform;

		public String toString() {
			return "RowData{" +
				"createDate='" + createDate + '\'' +
				", locationId='" + locationId + '\'' +
				", locationName='" + locationName + '\'' +
				", md101Sn='" + md101Sn + '\'' +
				", msg='" + msg + '\'' +
				", sendPlatform='" + sendPlatform + '\'' +
				'}';
		}
	}
}
