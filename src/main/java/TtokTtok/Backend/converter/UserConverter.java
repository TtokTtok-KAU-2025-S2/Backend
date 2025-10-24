package TtokTtok.Backend.converter;

import TtokTtok.Backend.domain.complex.Apartment;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.web.dto.UserResponse;

public class UserConverter {

    public static UserResponse.SignUpResultDTO toSignUpResultDTO(User user) {
        return UserResponse.SignUpResultDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .aptName(user.getAptUnit().getApartment().getAptName())
                .dong(user.getDong())
                .ho(user.getHo())
                .build();
    }

    public static UserResponse.LoginResultDTO toLoginResultDTO(User user) {
        return UserResponse.LoginResultDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .aptName(user.getAptUnit().getApartment().getAptName())
                .dong(user.getDong())
                .ho(user.getHo())
                .isManager(user.getIsManager())
                .build();
    }

    public static UserResponse.ManagerContactResultDTO toManagerContactResultDTO(String aptName, String phoneNum) {
        return UserResponse.ManagerContactResultDTO.builder()
                .aptName(aptName)
                .phoneNum(phoneNum)
                .build();
    }
}

