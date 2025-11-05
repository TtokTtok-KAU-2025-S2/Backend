package TtokTtok.Backend.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final VertexAiGeminiChatModel chatModel;

    public String getMonthlyReportSummary(String statsData) {

        String systemMessage = """
                당신은 아파트 소음 관리 매니저 '똑똑'입니다.
                제공된 월간 소음 통계 데이터를 기반으로, 아파트 주민들에게 공지할 'AI 분석 요약' 텍스트를 작성해야 합니다.
                [작성 규칙]
                1. 친절하고 전문적인 어조를 사용합니다.
                2. 전월 대비 증감, 주요 소음 유형, 주요 발생 시간대를 반드시 언급합니다.
                3. 통계를 바탕으로 주민들에게 소음 인식 개선을 위한 제안이나 격려의 말을 포함합니다.
                4. 200자 내외의 한국어로 요약합니다.
                """;

        String userMessage = String.format("[지난 달 통계 데이터]\n%s\n\n[분석 요약 작성 시작]", statsData);

        // ✅ 새 방식 — system + user 메시지를 Prompt에 함께 전달
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemMessage),
                new UserMessage(userMessage)
        ));

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }
}
