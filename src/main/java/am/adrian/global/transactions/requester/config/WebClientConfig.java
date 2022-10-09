package am.adrian.global.transactions.requester.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.text-validation-url}")
    private String textValidationUrl;

    @Bean
    public WebClient textValidationClient() {
        return WebClient.create(textValidationUrl + "/internal/validate/text");
    }
}
