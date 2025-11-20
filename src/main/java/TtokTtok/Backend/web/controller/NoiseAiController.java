package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.service.AnalysisService;
import TtokTtok.Backend.web.dto.ai.NoiseAiRequest;
import TtokTtok.Backend.web.dto.ai.NoiseAiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noise/ai")
public class NoiseAiController {

    private final AnalysisService analysisService;

    @PostMapping("/category")
    public ApiResponse<NoiseAiResponse.CategoryAnalysisDto> analyzeCategory(
            @Valid @RequestBody NoiseAiRequest.CategoryRequest request
    ) {
        return ApiResponse.onSuccess(analysisService.analyzeCategory(request));
    }

    @PostMapping("/summary")
    public ApiResponse<NoiseAiResponse.SummaryDto> generateSummary(
            @Valid @RequestBody NoiseAiRequest.SummaryRequest request
    ) {
        return ApiResponse.onSuccess(analysisService.generateSummary(request));
    }
}


