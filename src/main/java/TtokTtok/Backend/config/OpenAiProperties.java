package TtokTtok.Backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    /**
     * OpenAI API Key (환경 변수에 저장된 값을 주입)
     */
    private String apiKey;

    /**
     * OpenAI HTTP Base URL (기본: https://api.openai.com/v1)
     */
    private String baseUrl;

    /**
     * Whisper / gpt-4o-mini-transcribe 등 음성 인식 모델명
     */
    private String transcriptionModel;

    /**
     * ChatCompletion 기반 텍스트 생성 모델명 (예: gpt-4o-mini)
     */
    private String chatModel;
}


