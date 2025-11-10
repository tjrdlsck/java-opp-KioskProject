package mainpage.util;

import java.util.regex.Pattern;

/**
 * [4단계 신규 유틸리티]
 * 입력 값 유효성 검사를 담당하는 static 헬퍼 클래스.
 * (Kiosk.java의 getValidPhoneNumber 로직 이관)
 */
public class InputValidator {

    /**
     * [정규 표현식]
     * ^     : 문자열의 시작
     * 010   : "010" 문자 그대로
     * \d{8} : \d(숫자)가 {8}(8번) 반복
     * $     : 문자열의 끝
     */
    private static final String PHONE_REGEX = "^010\\d{8}$";

    /**
     * 입력된 전화번호 문자열이 유효한 형식(01012345678)인지 검사합니다.
     * @param phone 검사할 문자열
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // 'Pattern.matches(regex, input)'
        // : 입력 문자열이 정규식과 *완전히* 일치하는지 검사
        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 입력된 이름이 유효한지 검사합니다.
     * (여기서는 간단히 비어있지 않은지만 확인)
     * @param name 검사할 이름
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}