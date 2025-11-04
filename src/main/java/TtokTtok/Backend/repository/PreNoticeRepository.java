package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.PreNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreNoticeRepository extends JpaRepository<PreNotice, Long> {
    Page<PreNotice> findAllByApartment(Apartment apartment, Pageable pageable);
    Page<PreNotice> findAllByApartmentAndUser_Dong(Apartment apartment, Integer dong, Pageable pageable);
}
