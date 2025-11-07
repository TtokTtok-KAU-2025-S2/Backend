package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Apartment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apt_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 255, nullable = false)
    private String addr;

    @Column(length = 50, nullable = false)
    private String city;

    @Column(length = 50, nullable = false)
    private String district;

    // --- 양방향 연관관계 ---

    @OneToMany(mappedBy = "apartment")
    @Builder.Default
    private List<User> userList = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Builder.Default
    private List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Builder.Default
    private List<PreNotice> preNoticeList = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Builder.Default
    private List<MonthlyReport> monthlyReportList = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Builder.Default
    private List<ApartmentStat> apartmentStatList = new ArrayList<>();
    }