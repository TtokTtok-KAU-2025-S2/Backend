package TtokTtok.Backend.domain.knock;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.common.enums.ResponseType;
import jakarta.persistence.*;

@Entity
@Table(name = "KNOCK_RESPONSE")
public class KnockResponse extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private KnockRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User responder;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_type", length = 30, nullable = false)
    private ResponseType responseType;

    @Column(name = "response_time")
    private Integer responseTime;

    public Long getId() { return id; }
    public KnockRequest getRequest() { return request; }
    public User getResponder() { return responder; }
    public ResponseType getResponseType() { return responseType; }
    public Integer getResponseTime() { return responseTime; }
    public void setId(Long id) { this.id = id; }
    public void setRequest(KnockRequest request) { this.request = request; }
    public void setResponder(User responder) { this.responder = responder; }
    public void setResponseType(ResponseType responseType) { this.responseType = responseType; }
    public void setResponseTime(Integer responseTime) { this.responseTime = responseTime; }
}


