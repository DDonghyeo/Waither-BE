package com.waither.weatherservice.dto;

import org.apache.kafka.common.protocol.types.Field;

public record AeroTestRequest(
	String searchDate
) {
}
