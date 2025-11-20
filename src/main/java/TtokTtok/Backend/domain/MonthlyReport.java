package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MonthlyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id", nullable = false)
    private Apartment apartment;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer totalReportCount;

    @Column(precision = 5, scale = 2)
    private BigDecimal changeRate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String hourlyStatsJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String categoryStatsJson;

    @Column(columnDefinition = "TEXT")
    private String aiAnalysisText;

    public void updateReportData(Integer totalCount, BigDecimal changeRate,
                                 String hourlyStatsJson, String categoryStatsJson,
                                 String aiSummary) {
        this.totalReportCount = totalCount;
        this.changeRate = changeRate;
        this.hourlyStatsJson = hourlyStatsJson;
        this.categoryStatsJson = categoryStatsJson;
        this.aiAnalysisText = aiSummary;
    }

}
