package TtokTtok.Backend.web.dto.noise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoiseMonthlyCalendarDTO {
    private Long userId;
    private int year;
    private int month;
    private List<DateInfo> dates;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateInfo {
        private String date;       // "2025-10-27"
        private boolean hasNoiseLog;
    }

}
