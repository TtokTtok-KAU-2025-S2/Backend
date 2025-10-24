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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @Column(name = "dong", nullable = false)
    private Integer dong;

    @Column(name = "ho", nullable = false)
    private Integer ho;

    @OneToMany(mappedBy = "aptUnit")
    private List<TtokTtok.Backend.domain.user.User> users;

    public Long getId() { return id; }
    public Apartment getApartment() { return apartment; }
    public Integer getDong() { return dong; }
    public Integer getHo() { return ho; }
    public List<TtokTtok.Backend.domain.user.User> getUsers() { return users; }
    
    public void setId(Long id) { this.id = id; }
    public void setApartment(Apartment apartment) { this.apartment = apartment; }
    public void setDong(Integer dong) { this.dong = dong; }
    public void setHo(Integer ho) { this.ho = ho; }
    public void setUsers(List<TtokTtok.Backend.domain.user.User> users) { this.users = users; }
}


