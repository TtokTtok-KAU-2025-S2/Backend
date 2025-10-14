package TtokTtok.Backend.domain.noise;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.common.enums.NoiseCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "NOISE_LOG")
public class NoiseLog extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_datetime", nullable = false)
    private java.time.LocalDateTime logDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "noise_type", length = 30, nullable = false)
    private NoiseCategory noiseType;

    @Column(name = "noise_level", nullable = false)
    private Integer noiseLevel;

    @Lob
    @Column(name = "memo")
    private String memo;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public java.time.LocalDateTime getLogDatetime() { return logDatetime; }
    public NoiseCategory getNoiseType() { return noiseType; }
    public Integer getNoiseLevel() { return noiseLevel; }
    public String getMemo() { return memo; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setLogDatetime(java.time.LocalDateTime logDatetime) { this.logDatetime = logDatetime; }
    public void setNoiseType(NoiseCategory noiseType) { this.noiseType = noiseType; }
    public void setNoiseLevel(Integer noiseLevel) { this.noiseLevel = noiseLevel; }
    public void setMemo(String memo) { this.memo = memo; }
}


