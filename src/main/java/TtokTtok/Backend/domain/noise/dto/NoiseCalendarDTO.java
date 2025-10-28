package TtokTtok.Backend.domain.noise.dto;

import java.time.LocalDate;

public class NoiseCalendarDTO {
    private LocalDate date;        // 조회 날짜
    private boolean hasNoiseLog;   // 해당 날짜에 소음 기록 존재 여부

    public NoiseCalendarDTO(LocalDate date, boolean hasNoiseLog) {
        this.date = date;
        this.hasNoiseLog = hasNoiseLog;
    }

    // getter / setter
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isHasNoiseLog() {
        return hasNoiseLog;
    }

    public void setHasNoiseLog(boolean hasNoiseLog) {
        this.hasNoiseLog = hasNoiseLog;
    }
}
