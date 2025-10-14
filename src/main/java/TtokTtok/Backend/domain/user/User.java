package TtokTtok.Backend.domain.user;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.complex.AptUnit;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "USER")
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id")
    private AptUnit aptUnit;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "trust_index", nullable = false)
    private Integer trustIndex;

    @OneToMany(mappedBy = "user")
    private List<TtokTtok.Backend.domain.community.Article> articles;

    @OneToMany(mappedBy = "user")
    private List<TtokTtok.Backend.domain.community.Reaction> reactions;

    @OneToMany(mappedBy = "user")
    private List<TtokTtok.Backend.domain.noise.NoiseLog> noiseLogs;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private TrustIndex trustIndexDetail;

    @OneToMany(mappedBy = "user")
    private List<PointsLog> pointsLogs;

    @OneToMany(mappedBy = "writer")
    private List<TtokTtok.Backend.domain.community.PreNotice> preNotices;

    @OneToMany(mappedBy = "requester")
    private List<TtokTtok.Backend.domain.knock.KnockRequest> knockRequests;

    @OneToMany(mappedBy = "responder")
    private List<TtokTtok.Backend.domain.knock.KnockResponse> knockResponses;

    public Long getId() { return id; }
    public AptUnit getAptUnit() { return aptUnit; }
    public String getEmail() { return email; }
    public Integer getPoints() { return points; }
    public Integer getTrustIndex() { return trustIndex; }
    public TrustIndex getTrustIndexDetail() { return trustIndexDetail; }
    public List<PointsLog> getPointsLogs() { return pointsLogs; }
    public void setId(Long id) { this.id = id; }
    public void setAptUnit(AptUnit aptUnit) { this.aptUnit = aptUnit; }
    public void setEmail(String email) { this.email = email; }
    public void setPoints(Integer points) { this.points = points; }
    public void setTrustIndex(Integer trustIndex) { this.trustIndex = trustIndex; }
    public void setTrustIndexDetail(TrustIndex trustIndexDetail) { this.trustIndexDetail = trustIndexDetail; }
    public void setPointsLogs(List<PointsLog> pointsLogs) { this.pointsLogs = pointsLogs; }
}


