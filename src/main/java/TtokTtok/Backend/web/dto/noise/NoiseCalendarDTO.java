package TtokTtok.Backend.web.dto.noise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


//월간 소음 캘린더 조회 응답을 위한 DTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoiseCalendarDTO {
    private Long userId; // 소음 기록을 조회할 사용자 ID
    private int year;   // 조회할 년도
    private int month;   // 조회할 월
    private List<DateInfo> data;  // 해당 월의 날짜별 소음 기록 정보 리스트


    //월간 캘린더에서 특정 날짜의 소음 기록 존재 유무 및 상세 목록을 나타내는 DTO
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateInfo {
        private String date;
        private boolean hasNoiseLog; // 해당 날짜에 소음 기록이 존재하는지 여부
        private List<NoiseLogInfo> noiseList; // 해당 날짜의 소음일기 상세 목록

        //소음 기록이 없는 날짜를 위한 생성자
        public DateInfo(String date, boolean hasNoiseLog) {
            this.date = date;
            this.hasNoiseLog = hasNoiseLog;
            this.noiseList = Collections.emptyList();
        }
    }

    //특정 날짜의 소음 기록(소음일기) 단건에 대한 상세 정보를 나타내는 DTO
    // 특정 날짜에 생성한 소음일기 목록을 조회 시 사용. 소음일기 단건에서의 필요한 내용 NoiseLogInfo
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoiseLogInfo {
        private String category;           // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )
        private LocalDateTime occuredAt;          // 소음 생성 시각(소음 기록 생성 시각)
        private LocalDateTime updatedAt;          // 소음 기록 수정 시각
        private String grade;               //소음 등급(QUIET, NORMAL, LOUD)
        private BigDecimal dbHigh;              // 최대 데시벨
        private BigDecimal dbAvg;              // 평균 데시벨
        private String summary;             // AI가 생성한 소음 메모
    }
}

