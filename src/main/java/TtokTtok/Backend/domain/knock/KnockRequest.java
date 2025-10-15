package TtokTtok.Backend.domain.knock;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "KNOCK_REQUEST")
public class KnockRequest extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;

    @Column(name = "request_time", nullable = false)
    private java.time.LocalDateTime requestTime;

    @Column(name = "noise_category", length = 30, nullable = false)
    private String noiseCategory;

    @Column(name = "location_scope")
    private Integer locationScope;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private KnockResult result;

    public Long getId() { return id; }
    public User getRequester() { return requester; }
    public java.time.LocalDateTime getRequestTime() { return requestTime; }
    public String getNoiseCategory() { return noiseCategory; }
    public Integer getLocationScope() { return locationScope; }
    public Integer getDuration() { return duration; }
    public Boolean getIsActive() { return isActive; }
    public KnockResult getResult() { return result; }
    public void setId(Long id) { this.id = id; }
    public void setRequester(User requester) { this.requester = requester; }
    public void setRequestTime(java.time.LocalDateTime requestTime) { this.requestTime = requestTime; }
    public void setNoiseCategory(String noiseCategory) { this.noiseCategory = noiseCategory; }
    public void setLocationScope(Integer locationScope) { this.locationScope = locationScope; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setResult(KnockResult result) { this.result = result; }
}


