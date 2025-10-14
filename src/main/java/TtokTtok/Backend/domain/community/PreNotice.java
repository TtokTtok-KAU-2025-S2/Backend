package TtokTtok.Backend.domain.community;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "PRE_NOTICE")
public class PreNotice extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "post_date")
    private java.time.LocalDateTime postDate;

    @Column(name = "post_time")
    private java.time.LocalDateTime postTime;

    public Long getId() { return id; }
    public User getWriter() { return writer; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public java.time.LocalDateTime getPostDate() { return postDate; }
    public java.time.LocalDateTime getPostTime() { return postTime; }
    public void setId(Long id) { this.id = id; }
    public void setWriter(User writer) { this.writer = writer; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setPostDate(java.time.LocalDateTime postDate) { this.postDate = postDate; }
    public void setPostTime(java.time.LocalDateTime postTime) { this.postTime = postTime; }
}


