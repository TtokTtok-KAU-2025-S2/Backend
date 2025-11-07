package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.web.dto.PreNoticeRequest;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PreNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id", nullable = false)
    private Apartment apartment;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDate eventDate; // ERD의 event_datetime
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private String eventReason; // ERD의 event_reason


    public void update(PreNoticeRequest.UpdatePreNoticeDto request) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate eventDate = LocalDate.parse(request.getEventDate(), dateFormatter);

        String[] times = request.getEventTime().split(" ~ ");
        LocalTime startTime = LocalTime.parse(times[0].trim());
        LocalTime endTime = LocalTime.parse(times[1].trim());

        this.title = request.getTitle();
        this.content = request.getContent();
        this.eventDate = eventDate;
        this.eventStartTime = startTime;
        this.eventEndTime = endTime;
        this.eventReason = request.getEventReason();
    }
}