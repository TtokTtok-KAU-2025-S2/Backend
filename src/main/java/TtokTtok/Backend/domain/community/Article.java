package TtokTtok.Backend.domain.community;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.common.enums.ArticleCategory;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ARTICLE")
public class Article extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_num")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private ArticleCategory category;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "hashtag", length = 255)
    private String hashtag;

    @OneToMany(mappedBy = "article")
    private List<Reaction> reactions;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public ArticleCategory getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getHashtag() { return hashtag; }
    public List<Reaction> getReactions() { return reactions; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setCategory(ArticleCategory category) { this.category = category; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setHashtag(String hashtag) { this.hashtag = hashtag; }
    public void setReactions(List<Reaction> reactions) { this.reactions = reactions; }
}


