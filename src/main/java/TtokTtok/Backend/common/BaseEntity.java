// TtokTtok.Backend.domain.base.BaseEntity.java

package TtokTtok.Backend.common; // 단일화된 패키지 경로

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate // 생성 시간 자동 주입
    @Column(name = "created_at", nullable = false, updatable = false) // 생성 시간은 업데이트 불가로 설정하는 것이 일반적
    protected LocalDateTime createdAt;

    @LastModifiedDate // 수정 시간 자동 주입
    @Column(name = "modified_at", nullable = false)
    protected LocalDateTime modifiedAt;

    // 수동 getters/setters는 @Getter 어노테이션이 대신하므로 제거
}