package TtokTtok.Backend.common.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    
    // 첫 글자 영문 대문자, 8자 이상, 숫자 1개 이상, 기호 1개 이상
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // 첫 글자가 대문자인지 확인
        if (!Character.isUpperCase(password.charAt(0))) {
            return false;
        }
        
        // 나머지 패턴 검증
        return pattern.matcher(password).matches();
    }
    
    public static String getValidationMessage() {
        return "비밀번호는 첫 글자가 영문 대문자로 시작하고, 8자 이상이며, 숫자와 기호를 각각 1개 이상 포함해야 합니다";
    }
}

