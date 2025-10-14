package TtokTtok.Backend.domain.knock;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "KNOCK_RESULT")
public class KnockResult extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private KnockRequest request;

    @Column(name = "total_responder")
    private Integer totalResponder;

    @Column(name = "heard_count")
    private Integer heardCount;

    @Column(name = "quiet_count")
    private Integer quietCount;

    @Column(name = "peaceful_count")
    private Integer peacefulCount;

    @Lob
    @Column(name = "analysis")
    private String analysis;

    public Long getId() { return id; }
    public KnockRequest getRequest() { return request; }
    public Integer getTotalResponder() { return totalResponder; }
    public Integer getHeardCount() { return heardCount; }
    public Integer getQuietCount() { return quietCount; }
    public Integer getPeacefulCount() { return peacefulCount; }
    public String getAnalysis() { return analysis; }
    public void setId(Long id) { this.id = id; }
    public void setRequest(KnockRequest request) { this.request = request; }
    public void setTotalResponder(Integer totalResponder) { this.totalResponder = totalResponder; }
    public void setHeardCount(Integer heardCount) { this.heardCount = heardCount; }
    public void setQuietCount(Integer quietCount) { this.quietCount = quietCount; }
    public void setPeacefulCount(Integer peacefulCount) { this.peacefulCount = peacefulCount; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
}


