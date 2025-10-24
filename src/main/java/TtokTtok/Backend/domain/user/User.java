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

    @Column(name = "nickname", length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "dong", nullable = false)
    private Integer dong;

    @Column(name = "ho", nullable = false)
    private Integer ho;

    @Column(name = "is_manager", nullable = false)
    private Boolean isManager = false;

    @Column(name = "points", nullable = false)
    private Integer points = 0;

    @Column(name = "trust_index", nullable = false)
    private Integer trustIndex = 100;

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
    public String getNickname() { return nickname; }
    public String getPassword() { return password; }
    public Integer getDong() { return dong; }
    public Integer getHo() { return ho; }
    public Boolean getIsManager() { return isManager; }
    public Integer getPoints() { return points; }
    public Integer getTrustIndex() { return trustIndex; }
    public TrustIndex getTrustIndexDetail() { return trustIndexDetail; }
    public List<PointsLog> getPointsLogs() { return pointsLogs; }
    
    public void setId(Long id) { this.id = id; }
    public void setAptUnit(AptUnit aptUnit) { this.aptUnit = aptUnit; }
    public void setEmail(String email) { this.email = email; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPassword(String password) { this.password = password; }
    public void setDong(Integer dong) { this.dong = dong; }
    public void setHo(Integer ho) { this.ho = ho; }
    public void setIsManager(Boolean isManager) { this.isManager = isManager; }
    public void setPoints(Integer points) { this.points = points; }
    public void setTrustIndex(Integer trustIndex) { this.trustIndex = trustIndex; }
    public void setTrustIndexDetail(TrustIndex trustIndexDetail) { this.trustIndexDetail = trustIndexDetail; }
    public void setPointsLogs(List<PointsLog> pointsLogs) { this.pointsLogs = pointsLogs; }
}


