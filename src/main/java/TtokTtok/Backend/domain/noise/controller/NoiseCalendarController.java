package TtokTtok.Backend.domain.noise.controller;

import TtokTtok.Backend.domain.noise.dto.NoiseCalendarDTO;
import TtokTtok.Backend.domain.noise.service.NoiseCalendarService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/noise/calendar")
public class NoiseCalendarController {
    private NoiseCalendarService service;

    public NoiseCalendarController(NoiseCalendarService service) {
        this.service = service;
    }

    @Operation(
            summary = "월간 소음 캘린더 조회",
            description = "특정 유저의 월간 소음 기록을 조회합니다.",
            responses = @ApiResponse(
                responseCode = "200",
                description = "OK",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NoiseCalendarDTO.class)
                )
            )
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getMonthlyCalendar(
            @Parameter(description = "조회할 유저 ID", required = true)
            @PathVariable Long userId,

            @Parameter(description = "조회할 연도 (예: 2025)", required = true)
            @RequestParam int year,

            @Parameter(description = "조회할 월 (예: 10)", required = true)
            @RequestParam int month) {

        try {
            List<NoiseCalendarDTO> data = service.getMonthlyCalendar(userId, year, month);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("year", year);
            result.put("month", month);
            result.put("data", data);

            Map<String, Object> response = new HashMap<>();
            response.put("isSuccess", true);
            response.put("code", "COMMON200");
            response.put("message", "OK");
            response.put("result", result);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "MEMBER4001");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("isSuccess", false);
            error.put("code", "CALENDAR4002");
            error.put("message", "올바르지 않은 연도 또는 월 형식입니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
