package TtokTtok.Backend.handler;

import TtokTtok.Backend.service.NoiseAnalyzerService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;

@Component
public class NoiseAnalysisHandler extends BinaryWebSocketHandler {

    private final NoiseAnalyzerService noiseAnalyzerService;

    public NoiseAnalysisHandler(NoiseAnalyzerService noiseAnalyzerService) {
        this.noiseAnalyzerService = noiseAnalyzerService;
    }

    /**
     * 연결 수립 시, 세션 시작 처리 (녹음 시작)
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket 연결 수립: Session ID " + session.getId());
        noiseAnalyzerService.startAnalysisSession(session);
    }

    /**
     * 클라이언트로부터 오디오 데이터 청크를 실시간으로 수신하여 처리합니다.
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        ByteBuffer audioChunk = message.getPayload();
        // 실시간 분석 서비스로 데이터 전달
        noiseAnalyzerService.processAudioChunk(session, audioChunk);
    }

    /**
     * 연결 종료 시, 최종 데이터 처리 및 정리 (녹음 중지)
     * 이 시점에 누적된 최종 오디오 데이터(byte[])가 NoiseAnalyzerService에서 반환됩니다.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: Session ID " + session.getId() + ", Status " + status.getCode());
        byte[] finalAudioData = noiseAnalyzerService.finishAnalysisSession(session);
        // 💡 finalAudioData를 사용하여 3단계(AI 분석 요청)를 트리거할 수 있지만,
        // 클라이언트에서 2단계 데이터를 채운 후 HTTP POST로 요청하는 것이 일반적이므로,
        // 현재 시스템은 HTTP POST를 주 통신 채널로 사용합니다.
    }

}