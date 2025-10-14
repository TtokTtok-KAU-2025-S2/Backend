package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "NOISE_FLOOR")
public class NoiseFloor extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floor_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyNoiseReport report;

    @Column(name = "floor_range", length = 20, nullable = false)
    private String floorRange;

    @Column(name = "log_count", nullable = false)
    private Integer logCount;

    public Long getId() { return id; }
    public MonthlyNoiseReport getReport() { return report; }
    public String getFloorRange() { return floorRange; }
    public Integer getLogCount() { return logCount; }
    public void setId(Long id) { this.id = id; }
    public void setReport(MonthlyNoiseReport report) { this.report = report; }
    public void setFloorRange(String floorRange) { this.floorRange = floorRange; }
    public void setLogCount(Integer logCount) { this.logCount = logCount; }
}


