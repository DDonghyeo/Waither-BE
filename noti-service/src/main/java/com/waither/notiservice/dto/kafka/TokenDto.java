package com.waither.notiservice.dto.kafka;

import com.waither.notiservice.domain.FireBaseToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    public Long userId;
    public String token;

    public FireBaseToken toEntity() {
        return FireBaseToken.builder()
                .userId(userId)
                .token(token)
                .build();
    }
}
