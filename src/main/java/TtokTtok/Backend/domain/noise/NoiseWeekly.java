package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "NOISE_WEEKLY")
public class NoiseWeekly extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyNoiseReport report;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "log_count", nullable = false)
    private Integer logCount;

    public Long getId() { return id; }
    public MonthlyNoiseReport getReport() { return report; }
    public Integer getWeek() { return week; }
    public Integer getLogCount() { return logCount; }
    public void setId(Long id) { this.id = id; }
    public void setReport(MonthlyNoiseReport report) { this.report = report; }
    public void setWeek(Integer week) { this.week = week; }
    public void setLogCount(Integer logCount) { this.logCount = logCount; }
}


