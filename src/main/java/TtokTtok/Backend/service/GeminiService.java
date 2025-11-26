package TtokTtok.Backend.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final Client client;

    // 기존: Text 기반 처리
    public String fromTextInput(String question) {
        GenerateContentResponse response =
                client.models.generateContent("gemini-2.5-flash", question, null);
        return response.text();
    }

    // ✨ [수정] Audio + Text 기반 처리 (멀티모달)
    public String analyzeAudio(byte[] audioData, String mimeType, String prompt) {
        try {
            // ✨ [수정 포인트] Base64 인코딩 불필요. 라이브러리가 byte[]를 직접 받습니다.
            // 인라인 데이터 파트 생성 (오디오)
            Part audioPart = Part.builder()
                    .inlineData(com.google.genai.types.Blob.builder()
                            .data(audioData) // byte[] 그대로 전달
                            .mimeType(mimeType)
                            .build())
                    .build();

            // 텍스트 파트 생성 (프롬프트)
            Part textPart = Part.builder()
                    .text(prompt)
                    .build();

            // 컨텐츠 조립
            Content content = Content.builder()
                    .parts(List.of(audioPart, textPart))
                    .role("user")
                    .build();

            // Gemini 호출
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    content,
                    null
            );

            return response.text();

        } catch (Exception e) {
            log.error("Gemini 오디오 분석 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Gemini API 호출 실패", e);
        }
    }
}