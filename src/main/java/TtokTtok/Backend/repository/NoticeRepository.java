package TtokTtok.Backend.repository;
// Notice 객체를 데이터베이스에 저장 조회 삭제
import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
    // 아파트별 공지사항 페이징 조회
    Page<Notice> findAllByApartment(Apartment apartment, Pageable pageable);
}
