package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.common.enums.ActivationStatus;
import TtokTtok.Backend.common.enums.NoiseGrade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApartmentStat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_stat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id", nullable = false)
    private Apartment apartment;

    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String mainNoiseTypesJson;

    // --- (이 메서드를 추가하세요) ---
    /**
     * 배치 서비스가 통계 데이터를 업데이트할 때 사용합니다.
     * @param status (LOW, MEDIUM, HIGH)
     * @param mainNoiseTypesJson (카테고리 통계 JSON)
     */
    public void updateStats(ActivationStatus status, String mainNoiseTypesJson) {
        this.activationStatus = status;
        this.mainNoiseTypesJson = mainNoiseTypesJson;
    }
    // --- (여기까지) ---
}