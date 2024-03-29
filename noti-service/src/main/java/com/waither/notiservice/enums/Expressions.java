package com.waither.notiservice.enums;

import com.waither.notiservice.domain.type.Season;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Expressions {
    VERY_COLD("매우 추우실 거예요."),
    COLD("추우실거예요."),
    LITTLE_COLD("조금 추우실거예요."),
    COOL("시원하실거예요."),
    GOOD("적당하실거예요."),
    WARM("따뜻하실거예요."),
    LITTLE_HOT("조금 더우실거예요."),
    HOT("더우실거예요."),
    VERY_HOT("매우 더우실거예요."),
    ;

    String expression;


}
