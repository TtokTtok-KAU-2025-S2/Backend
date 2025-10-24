package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.common.util.PasswordValidator;
import TtokTtok.Backend.converter.UserConverter;
import TtokTtok.Backend.domain.complex.Apartment;
import TtokTtok.Backend.domain.complex.AptUnit;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.ApartmentRepository;
import TtokTtok.Backend.repository.AptUnitRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.UserRequest;
import TtokTtok.Backend.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final AptUnitRepository aptUnitRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse.SignUpResultDTO signUp(UserRequest.SignUpDTO request) {
        // 1. 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new GeneralException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
        }

        // 2. 비밀번호 일치 체크
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new GeneralException(ErrorStatus.PASSWORD_MISMATCH);
        }

        // 3. 비밀번호 형식 검증
        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD_FORMAT);
        }

        // 4. 아파트 고유 코드로 아파트 찾기
        Apartment apartment = apartmentRepository.findByAptCode(request.getAptCode())
                .orElseThrow(() -> new GeneralException(ErrorStatus.APARTMENT_NOT_FOUND));

        // 5. 동/호수로 세대 찾기 또는 생성
        AptUnit aptUnit = aptUnitRepository.findByApartmentAndDongAndHo(apartment, request.getDong(), request.getHo())
                .orElseGet(() -> {
                    AptUnit newAptUnit = new AptUnit();
                    newAptUnit.setApartment(apartment);
                    newAptUnit.setDong(request.getDong());
                    newAptUnit.setHo(request.getHo());
                    return aptUnitRepository.save(newAptUnit);
                });

        // 6. 사용자 생성
        User user = new User();
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAptUnit(aptUnit);
        user.setDong(request.getDong());
        user.setHo(request.getHo());
        user.setIsManager(false); // 기본값은 일반 사용자
        user.setPoints(0);
        user.setTrustIndex(100);

        User savedUser = userRepository.save(user);

        return UserConverter.toSignUpResultDTO(savedUser);
    }
}

