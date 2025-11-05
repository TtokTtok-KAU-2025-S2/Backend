package TtokTtok.Backend.common.enums;

/**
 * 아파트 앱 활성화 상태
 * (LOW, MEDIUM, HIGH 3단계로 변경)
 */
public enum ActivationStatus {
    LOW,    // (예: 월 10건 미만)
    MEDIUM, // (예: 월 10건 ~ 49건)
    HIGH    // (예: 월 50건 이상)
}