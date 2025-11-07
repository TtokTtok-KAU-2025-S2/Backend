package TtokTtok.Backend.config;

import TtokTtok.Backend.handler.NoiseAnalysisHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final NoiseAnalysisHandler noiseAnalysisHandler;

    public WebSocketConfig(NoiseAnalysisHandler noiseAnalysisHandler) {
        this.noiseAnalysisHandler = noiseAnalysisHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 🚨 ttokttok.kro.kr 도메인 허용 설정
        registry.addHandler(noiseAnalysisHandler, "/ws/noise/stream")
                .setAllowedOrigins("http://ttokttok.kro.kr", "https://ttokttok.kro.kr", "http://localhost:3000");
        // 개발 환경을 위해 localhost도 추가합니다.
    }
}
