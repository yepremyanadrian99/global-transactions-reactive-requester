package am.adrianyepremyan.global.transactions.requester.config;

import static am.adrianyepremyan.global.transactions.requester.util.UriUtil.normalizeUrl;

import java.time.Duration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@Log4j2
public class WebClientConfig {

    private static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    @Value("${api.requestTimeoutMillis}")
    private Long requestTimeoutMillis;

    @Value("${api.textValidationUrl}")
    private String textValidationUrl;

    @Value("${api.userServiceUrl}")
    private String userServiceUrl;

    @Bean
    public WebClient textValidationClient() {
        return createWebClient(textValidationUrl);
    }

    @Bean
    public WebClient userServiceClient() {
        return createWebClient(userServiceUrl);
    }

    private WebClient createWebClient(String baseUrl) {
        log.info("Creating web client with url: {}", baseUrl);
        final var client = HttpClient.create().responseTimeout(Duration.ofMillis(requestTimeoutMillis));
        return WebClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_UTF8_VALUE)
            .clientConnector(new ReactorClientHttpConnector(client))
            .baseUrl(normalizeUrl(baseUrl))
            .build();
    }
}
