package TtokTtok.Backend.domain.complex;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "APARTMENT")
public class Apartment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_id")
    private Long id;

    @Column(name = "apt_name", length = 100, nullable = false)
    private String aptName;

    @Column(name = "apt_code", length = 50, nullable = false, unique = true)
    private String aptCode;

    @OneToMany(mappedBy = "apartment")
    private List<AptUnit> aptUnits;

    public Long getId() { return id; }
    public String getAptName() { return aptName; }
    public String getAptCode() { return aptCode; }
    public List<AptUnit> getAptUnits() { return aptUnits; }
    
    public void setId(Long id) { this.id = id; }
    public void setAptName(String aptName) { this.aptName = aptName; }
    public void setAptCode(String aptCode) { this.aptCode = aptCode; }
    public void setAptUnits(List<AptUnit> aptUnits) { this.aptUnits = aptUnits; }
}

