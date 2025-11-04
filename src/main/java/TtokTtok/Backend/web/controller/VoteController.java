package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.service.VoteService;
import TtokTtok.Backend.web.dto.VoteRequest;
import TtokTtok.Backend.web.dto.VoteResponse;
import TtokTtok.Backend.converter.VoteConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/noise-reports/{reportId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ApiResponse<VoteResponse.VoteDto> createOrUpdateVote(
            @PathVariable Long reportId,
            @RequestBody @Valid VoteRequest.CreateVoteDto request) {
        VoteResponse.VoteDto response = VoteConverter.toVoteDto(voteService.createOrUpdateVote(reportId, request));
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping
    public ApiResponse<String> deleteVote(
            @PathVariable Long reportId) {
        voteService.deleteVote(reportId);
        return ApiResponse.onSuccess("투표가 성공적으로 취소되었습니다.");
    }
}