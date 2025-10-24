package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.converter.UserConverter;
import TtokTtok.Backend.domain.complex.Manager;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.ManagerRepository;
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
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse.LoginResultDTO login(UserRequest.LoginDTO request) {
        // 1. 닉네임으로 사용자 찾기
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        return UserConverter.toLoginResultDTO(user);
    }

    @Override
    public UserResponse.ManagerContactResultDTO getManagerContact(UserRequest.ManagerContactDTO request) {
        Manager manager = managerRepository.findByAptName(request.getAptName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MANAGER_NOT_FOUND));

        return UserConverter.toManagerContactResultDTO(manager.getAptName(), manager.getPhoneNum());
    }
}

