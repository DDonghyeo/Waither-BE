package com.waither.notiservice.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @NotBlank(message = " 위도(x) 값은 필수입니다.")
    @DecimalMax(value = "132.0", inclusive = true, message = "위도(x)는 대한민국 내에서만 가능합니다.")
    @DecimalMin(value = "124.0", inclusive = true, message = "위도(x)는 대한민국 내에서만 가능합니다.")
    public double x;

    @NotBlank(message = " 경도(y) 값은 필수입니다.")
    @DecimalMax(value = "43.0", inclusive = true, message = "경도(y)는 대한민국 내에서만 가능합니다.")
    @DecimalMin(value = "33.0", inclusive = true, message = "경도(y)는 대한민국 내에서만 가능합니다.")
    public double y;
}
