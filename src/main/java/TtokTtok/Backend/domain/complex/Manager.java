package TtokTtok.Backend.domain.complex;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "MANAGER")
public class Manager extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "office_id")
    private Long id;

    @Column(name = "apt_name", length = 50, nullable = false)
    private String aptName;

    @Column(name = "phone_num", length = 20, nullable = false)
    private String phoneNum;

    public Long getId() { return id; }
    public String getAptName() { return aptName; }
    public String getPhoneNum() { return phoneNum; }
    public void setId(Long id) { this.id = id; }
    public void setAptName(String aptName) { this.aptName = aptName; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
}


