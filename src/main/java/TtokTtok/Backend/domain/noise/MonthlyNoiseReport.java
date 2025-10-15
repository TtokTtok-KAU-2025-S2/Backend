package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "MONTHLY_NOISE_REPORT")
public class MonthlyNoiseReport extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id", nullable = false)
    private TtokTtok.Backend.domain.complex.AptUnit aptUnit;

    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(name = "total_log", nullable = false)
    private Integer totalLog;

    @Column(name = "avg_level", precision = 5, scale = 2, nullable = false)
    private BigDecimal avgLevel;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoiseType> noiseTypes;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoiseTime> noiseTimes;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoiseWeekly> noiseWeeklies;

//    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<NoiseFloor> noiseFloors;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private OfficeRecommendation officeRecommendation;

    public Long getId() { return id; }
    public TtokTtok.Backend.domain.complex.AptUnit getAptUnit() { return aptUnit; }
    public Integer getReportYear() { return reportYear; }
    public Integer getReportMonth() { return reportMonth; }
    public Integer getTotalLog() { return totalLog; }
    public BigDecimal getAvgLevel() { return avgLevel; }
    public List<NoiseType> getNoiseTypes() { return noiseTypes; }
    public List<NoiseTime> getNoiseTimes() { return noiseTimes; }
    public List<NoiseWeekly> getNoiseWeeklies() { return noiseWeeklies; }
//    public List<NoiseFloor> getNoiseFloors() { return noiseFloors; }
    public OfficeRecommendation getOfficeRecommendation() { return officeRecommendation; }
    public void setId(Long id) { this.id = id; }
    public void setAptUnit(TtokTtok.Backend.domain.complex.AptUnit aptUnit) { this.aptUnit = aptUnit; }
    public void setReportYear(Integer reportYear) { this.reportYear = reportYear; }
    public void setReportMonth(Integer reportMonth) { this.reportMonth = reportMonth; }
    public void setTotalLog(Integer totalLog) { this.totalLog = totalLog; }
    public void setAvgLevel(BigDecimal avgLevel) { this.avgLevel = avgLevel; }
    public void setNoiseTypes(List<NoiseType> noiseTypes) { this.noiseTypes = noiseTypes; }
    public void setNoiseTimes(List<NoiseTime> noiseTimes) { this.noiseTimes = noiseTimes; }
    public void setNoiseWeeklies(List<NoiseWeekly> noiseWeeklies) { this.noiseWeeklies = noiseWeeklies; }
    //public void setNoiseFloors(List<NoiseFloor> noiseFloors) { this.noiseFloors = noiseFloors; }
    public void setOfficeRecommendation(OfficeRecommendation officeRecommendation) { this.officeRecommendation = officeRecommendation; }
}


