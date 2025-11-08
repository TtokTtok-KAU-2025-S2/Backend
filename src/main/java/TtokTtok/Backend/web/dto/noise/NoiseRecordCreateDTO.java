package TtokTtok.Backend.web.dto.noise;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoiseRecordCreateDTO {
        private Long userId;

        // 1단계 데이터
        private Integer duration;
        private NoiseGrade grade;               //소음 등급(QUIET, NORMAL, LOUD)
        private BigDecimal dbHigh;              // 최대 데시벨
        private BigDecimal dbAvg;              // 평균 데시벨

        // 2단계 데이터
        private LocalDateTime occuredAt;          // 소음 생성 시각
        private NoiseCategory category;           // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )
        private String description;             //사용자가 소음 관련 내용을 입력

        // 3, 4단계 데이터
        private String summary;            // AI가 생성한 소음 메모

        /// 5단계 소음기록 DB에 생성
        public NoiseDiary toEntity(User user, String audioFilePath) { // User 객체를 외부(Service)에서 받아와 설정
            NoiseDiary record = new NoiseDiary();

            // 🚨 Service에서 조회된 User 객체를 사용
            record.setUser(user);

            // 1단계 데이터
            record.setDuration(this.duration);
            record.setGrade(this.grade);
            record.setDbHigh(this.dbHigh);
            record.setDbAvg(this.dbAvg);
            record.setOccuredAt(this.occuredAt);

            // 2단계 데이터
            record.setCategory(this.category);
            record.setDescription(this.description);

            // 3, 4단계 데이터
            record.setSummary(this.summary);

            // 5단계: 누락된 필수 필드 기본값 설정 (현황판에 등록하지 않는 상태)
            record.setReportYn(false);
            record.setReportedAt(null);

            // NOTE: createdAt은 Service에서 설정

            return record;
        }

}
