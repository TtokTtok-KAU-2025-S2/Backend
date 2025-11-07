package TtokTtok.Backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final Client client;

    //Text 기반의 처리
    public String fromTextInput(String question) {
        GenerateContentResponse response =
                client.models.generateContent("gemini-2.5-flash", question, null);
        return response.text();
    }


}
