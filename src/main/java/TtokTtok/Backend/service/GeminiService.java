package TtokTtok.Backend.service;

import TtokTtok.Backend.common.enums.NoiseCategory;
// 실제 SDK import는 주석 처리 (Gemini API 키 설정 및 클라이언트 생성 로직 필요)
// import com.google.genai.client.GenerativeModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class GeminiService {

    // ⚠️ 실제 구현 시 GenerativeModel 클라이언트를 Bean으로 등록하고 주입받아야 합니다.
    // private final GenerativeModel geminiModel;

    /**
     * AI 소음 분석 텍스트를 생성/재생성합니다. (3, 4단계)
     * * @param audioFile 클라이언트가 업로드한 녹음 파일
     * @param category 소음 카테고리
     * @param description 사용자 입력 설명
     * @param dbAvg 평균 데시벨
     * @return AI가 생성한 분석 텍스트 (summary)
     */
    public String generateNoiseAnalysis(
            MultipartFile audioFile,
            NoiseCategory category,
            String description,
            BigDecimal dbAvg) {

        // 1. 프롬프트 구성
        String prompt = String.format(
                "평균 데시벨 %s, 카테고리 '%s', 사용자 설명 '%s'입니다. 오디오 파일을 분석하여 소음 분석 일기 텍스트를 150자 이내로 작성해 주세요.",
                dbAvg.toString(), category.toString(), description
        );

        // 2. 오디오 파일 바이트 변환 (IOException 처리)
        byte[] audioBytes;
        try {
            // MultipartFile.getBytes()는 IOException을 던집니다.
            audioBytes = audioFile.getBytes();
        } catch (IOException e) {
            // 파일을 읽지 못하는 치명적인 오류는 RuntimeException으로 감싸서 전파합니다.
            throw new RuntimeException("AI 분석 중 오디오 파일 읽기 실패.", e);
        }

        // 3. (AI 호출 프레임워크)
        // ⚠️ 실제 Gemini API 호출 로직은 여기에 구현되어야 합니다.
        /*
        // 멀티모달 파트 구성 (텍스트 + 오디오)
        List<Part> parts = List.of(
            new Part.Builder().setText(prompt).build(),
            new Part.Builder().setInlineData(audioBytes).setMimeType(audioFile.getContentType()).build()
        );
        GenerateContentResponse response = geminiModel.generateContent(parts);
        return response.getText();
        */


        // 4. Placeholder 응답 반환 (실제 API 호출이 없을 때의 임시 반환)
        return String.format(
                "✨ AI 분석 텍스트 (완성) ✨\n[평균 dB: %s], [카테고리: %s]. 오디오(%d bytes) 분석을 완료했습니다.",
                dbAvg.toString(), category.toString(), audioBytes.length
        );
    }
}