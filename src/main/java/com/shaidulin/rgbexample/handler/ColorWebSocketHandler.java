package com.shaidulin.rgbexample.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaidulin.rgbexample.domain.Color;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@AllArgsConstructor
public class ColorWebSocketHandler implements WebSocketHandler {

    private final MongoRepository<Color, String> colorRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> output = session
                .receive()
                .log("received on server")
                .map(webSocketMessage -> Objects.requireNonNull(convertJsonToColor(webSocketMessage.getPayloadAsText())))
                .flatMap(color -> {
                    Color existingColor = colorRepository.findAll().get(0); // получаем единственный доступный цвет из бд
                    Color updatedColor = updateColor(existingColor, color);
                    existingColor = colorRepository.save(updatedColor);
                    return Mono.just(session.textMessage(convertColorToJson(existingColor)));
                });
        return session.send(output);
    }

    private Color updateColor(Color existingColor, Color newColor) {
        Integer newRed = newColor.getRed();
        Integer newGreen = newColor.getGreen();
        Integer newBlue = newColor.getBlue();
        if (newRed != null) {
            existingColor.setRed(newRed);
        } else if (newGreen != null) {
            existingColor.setGreen(newGreen);
        } else if (newBlue != null) {
            existingColor.setBlue(newBlue);
        }
        return existingColor;
    }

    @SneakyThrows
    private String convertColorToJson(Color color) {
        return objectMapper.writeValueAsString(color);
    }

    @SneakyThrows
    private Color convertJsonToColor(String json) {
        return objectMapper.readValue(json, Color.class);
    }
}
