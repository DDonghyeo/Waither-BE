package com.waither.userservice.service.commandService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, TokenDto> kafkaTokenTemplate;

    public void sendTokenMessage(TokenDto tokenDto) {
        kafkaTokenTemplate.send("firebase-token", tokenDto);
    }
}