package TtokTtok.Backend.domain.community;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "REACTIONS")
public class Reaction extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_num", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "views")
    private Integer views;

    public Long getId() { return id; }
    public Article getArticle() { return article; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public Integer getLikes() { return likes; }
    public Integer getViews() { return views; }
    public void setId(Long id) { this.id = id; }
    public void setArticle(Article article) { this.article = article; }
    public void setUser(User user) { this.user = user; }
    public void setContent(String content) { this.content = content; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public void setViews(Integer views) { this.views = views; }
}


