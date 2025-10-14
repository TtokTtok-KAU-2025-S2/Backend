package TtokTtok.Backend.domain.user;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "TRUST_INDEX")
public class TrustIndex extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "park_score")
    private Integer parkScore;

    @Column(name = "care_score")
    private Integer careScore;

    @Column(name = "communication_score")
    private Integer communicationScore;

    @Column(name = "consideration_score")
    private Integer considerationScore;

    @Column(name = "compliance_score")
    private Integer complianceScore;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Integer getParkScore() { return parkScore; }
    public Integer getCareScore() { return careScore; }
    public Integer getCommunicationScore() { return communicationScore; }
    public Integer getConsiderationScore() { return considerationScore; }
    public Integer getComplianceScore() { return complianceScore; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setParkScore(Integer parkScore) { this.parkScore = parkScore; }
    public void setCareScore(Integer careScore) { this.careScore = careScore; }
    public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }
    public void setConsiderationScore(Integer considerationScore) { this.considerationScore = considerationScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }
}


