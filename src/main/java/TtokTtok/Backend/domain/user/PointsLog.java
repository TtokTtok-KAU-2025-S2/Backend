package TtokTtok.Backend.domain.user;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "POINTS_LOG")
public class PointsLog extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "points_gained", nullable = false)
    private Integer pointsGained;

    @Column(name = "activity_type", length = 50, nullable = false)
    private String activityType;

    @Column(name = "activity_detail", length = 255)
    private String activityDetail;

    @Column(name = "activity_date", nullable = false)
    private java.time.LocalDateTime activityDate;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Integer getPointsGained() { return pointsGained; }
    public String getActivityType() { return activityType; }
    public String getActivityDetail() { return activityDetail; }
    public java.time.LocalDateTime getActivityDate() { return activityDate; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setPointsGained(Integer pointsGained) { this.pointsGained = pointsGained; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public void setActivityDetail(String activityDetail) { this.activityDetail = activityDetail; }
    public void setActivityDate(java.time.LocalDateTime activityDate) { this.activityDate = activityDate; }
}


