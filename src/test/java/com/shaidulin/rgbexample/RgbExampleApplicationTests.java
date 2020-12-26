package com.shaidulin.rgbexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaidulin.rgbexample.domain.Color;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RgbExampleApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private WebSocketClient client;

    @BeforeEach
    public void setUp() {
        client = new ReactorNettyWebSocketClient();
    }

    @Test
    public void test() throws URISyntaxException {
        Color color1ToSend = new Color();
        color1ToSend.setRed(128);

        Color color2ToSend = new Color();
        color2ToSend.setGreen(56);

        Color color3ToSend = new Color();
        color3ToSend.setBlue(88);

        Color expectedColor1 = new Color();
        expectedColor1.setRed(128);

        Color expectedColor2 = new Color();
        expectedColor2.setRed(128);
        expectedColor2.setGreen(56);

        Color expectedColor3 = new Color();
        expectedColor3.setRed(128);
        expectedColor3.setGreen(56);
        expectedColor3.setBlue(88);

        List<Color> receivedColors = new ArrayList<>();

        URI url = new URI(String.format("ws://localhost:%d/colors", port));

        Mono<Void> operation = client.execute(url, session ->
                session.send(Flux.just(
                        session.textMessage(convertColorToJson(color1ToSend)),
                        session.textMessage(convertColorToJson(color2ToSend)),
                        session.textMessage(convertColorToJson(color3ToSend))))
                        .thenMany(session.receive()
                                .log("received in client")
                                .map(WebSocketMessage::getPayloadAsText)
                                .map(this::convertJsonToColor)
                                .doOnNext(receivedColors::add))
                        .then());
        try {
            operation.block(Duration.ofSeconds(3L));
        } catch (IllegalStateException exception) {
            assertAll(() -> {
                assertEquals(3, receivedColors.size());
                assertTrue(receivedColors.contains(expectedColor1));
                assertTrue(receivedColors.contains(expectedColor2));
                assertTrue(receivedColors.contains(expectedColor3));
            });
        }

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
