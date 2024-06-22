package com.waither.notiservice.api.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record LocationDto (
        @NotBlank(message = " 위도(lat) 값은 필수입니다.")
        @DecimalMax(value = "132.0", inclusive = true, message = "위도(lat)는 대한민국 내에서만 가능합니다.")
        @DecimalMin(value = "124.0", inclusive = true, message = "위도(lat)는 대한민국 내에서만 가능합니다.")
        double lat,

        @NotBlank(message = " 경도(y) 값은 필수입니다.")
        @DecimalMax(value = "43.0", inclusive = true, message = "경도(lon)는 대한민국 내에서만 가능합니다.")
        @DecimalMin(value = "33.0", inclusive = true, message = "경도(lon)는 대한민국 내에서만 가능합니다.")
        double lon
) {


}
