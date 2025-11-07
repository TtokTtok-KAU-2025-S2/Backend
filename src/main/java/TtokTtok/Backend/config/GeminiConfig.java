package TtokTtok.Backend.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${GOOGLE_API_KEY}")
    private String apiKEY;

    @Bean
    public Client getClient() {
        return Client.builder()
                .apiKey(apiKEY)
                .build();
    }
}