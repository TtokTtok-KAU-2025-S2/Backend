package TtokTtok.Backend.domain.complex;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "APT_UNIT")
public class AptUnit extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apt_id")
    private Long id;

    @Column(name = "dong", nullable = false)
    private Integer dong;

    @Column(name = "ho", nullable = false)
    private Integer ho;

    @Column(name = "apt_code", length = 100, nullable = false)
    private String aptCode;

    @OneToMany(mappedBy = "aptUnit")
    private List<TtokTtok.Backend.domain.user.User> users;

    public Long getId() { return id; }
    public Integer getDong() { return dong; }
    public Integer getHo() { return ho; }
    public String getAptCode() { return aptCode; }
    public List<TtokTtok.Backend.domain.user.User> getUsers() { return users; }
    public void setId(Long id) { this.id = id; }
    public void setDong(Integer dong) { this.dong = dong; }
    public void setHo(Integer ho) { this.ho = ho; }
    public void setAptCode(String aptCode) { this.aptCode = aptCode; }
    public void setUsers(List<TtokTtok.Backend.domain.user.User> users) { this.users = users; }
}


