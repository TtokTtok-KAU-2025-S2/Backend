package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "OFFICE_RECOMMENDATION")
public class OfficeRecommendation extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false, unique = true)
    private MonthlyNoiseReport report;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    public Long getId() { return id; }
    public MonthlyNoiseReport getReport() { return report; }
    public String getContent() { return content; }
    public void setId(Long id) { this.id = id; }
    public void setReport(MonthlyNoiseReport report) { this.report = report; }
    public void setContent(String content) { this.content = content; }
}


