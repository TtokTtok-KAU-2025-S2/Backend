package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "NOISE_TIME")
public class NoiseTime extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyNoiseReport report;

    @Column(name = "time", nullable = false)
    private Integer time;

    @Column(name = "log_count", nullable = false)
    private Integer logCount;

    public Long getId() { return id; }
    public MonthlyNoiseReport getReport() { return report; }
    public Integer getTime() { return time; }
    public Integer getLogCount() { return logCount; }
    public void setId(Long id) { this.id = id; }
    public void setReport(MonthlyNoiseReport report) { this.report = report; }
    public void setTime(Integer time) { this.time = time; }
    public void setLogCount(Integer logCount) { this.logCount = logCount; }
}


