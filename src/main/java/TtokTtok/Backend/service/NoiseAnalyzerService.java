package TtokTtok.Backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NoiseAnalyzerService {

    // 세션별로 오디오 데이터를 누적하는 스트림 맵
    private final ConcurrentHashMap<String, ByteArrayOutputStream> recordedDataStreams = new ConcurrentHashMap<>();

    // ----------------- WebSocket Lifecycle -----------------

    public void startAnalysisSession(WebSocketSession session) {
        recordedDataStreams.put(session.getId(), new ByteArrayOutputStream());
    }

    public byte[] finishAnalysisSession(WebSocketSession session) {
        String sessionId = session.getId();
        ByteArrayOutputStream stream = recordedDataStreams.remove(sessionId);

        if (stream != null) {
            try {
                byte[] fullAudioData = stream.toByteArray();
                stream.close();
                // ⚠️ 녹음이 완료되면, 이 데이터(fullAudioData)를 FileStorageService로 넘겨 영구 저장해야 합니다.
                // 💡 최종 저장(5단계)은 클라이언트의 HTTP 요청으로 이루어지므로, 이 데이터는 임시 메모리에 남겨두거나
                // 클라이언트가 최종 파일로 저장 후 HTTP 요청 시 다시 전송해야 합니다.
                return fullAudioData;
            } catch (IOException e) {
                System.err.println("스트림 종료 중 오류 발생: " + e.getMessage());
                return new byte[0];
            }
        }
        return new byte[0];
    }

    // ----------------- Real-Time Processing -----------------

    public void processAudioChunk(WebSocketSession session, ByteBuffer chunk) {
        ByteArrayOutputStream stream = recordedDataStreams.get(session.getId());

        if (stream != null) {
            try {
                // 데이터 누적
                stream.write(chunk.array());

                // 실시간 데시벨 계산
                double currentDecibel = calculateDecibel(chunk);
                // 💡 필요하다면 여기에 session.sendMessage(new TextMessage("..."))로 클라이언트에 피드백 전송 로직 추가
            } catch (IOException e) {
                System.err.println("오디오 데이터 쓰기 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private double calculateDecibel(ByteBuffer chunk) {
        // TODO: 실제 RMS 계산 로직 구현 필요
        return 50.0 + (Math.random() * 20); // Placeholder
    }
}