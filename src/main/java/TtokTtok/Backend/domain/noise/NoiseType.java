package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "NOISE_TYPE")
public class NoiseType extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyNoiseReport report;

    @Column(name = "noise_type", length = 30, nullable = false)
    private String noiseType;

    @Column(name = "log_count", nullable = false)
    private Integer logCount;

    @Column(name = "percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;

    public Long getId() { return id; }
    public MonthlyNoiseReport getReport() { return report; }
    public String getNoiseType() { return noiseType; }
    public Integer getLogCount() { return logCount; }
    public BigDecimal getPercentage() { return percentage; }
    public void setId(Long id) { this.id = id; }
    public void setReport(MonthlyNoiseReport report) { this.report = report; }
    public void setNoiseType(String noiseType) { this.noiseType = noiseType; }
    public void setLogCount(Integer logCount) { this.logCount = logCount; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
}


