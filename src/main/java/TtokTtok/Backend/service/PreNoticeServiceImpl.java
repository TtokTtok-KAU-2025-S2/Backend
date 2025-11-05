package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.converter.PreNoticeConverter;
import TtokTtok.Backend.domain.PreNotice;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.PreNoticeRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.PreNoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PreNoticeServiceImpl implements PreNoticeService {

    private final PreNoticeRepository preNoticeRepository;
    private final UserRepository userRepository;

    @Override
    public PreNotice createPreNotice(PreNoticeRequest.CreatePreNoticeDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        PreNotice newPreNotice = PreNoticeConverter.toPreNotice(request, user);
        return preNoticeRepository.save(newPreNotice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PreNotice> getPreNoticeList(Pageable pageable, Boolean filterByMyDong) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        if (filterByMyDong) {
            return preNoticeRepository.findAllByApartmentAndUser_Dong(user.getApartment(), user.getDong(), pageable);
        } else {
            return preNoticeRepository.findAllByApartment(user.getApartment(), pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PreNotice getPreNotice(Long preNoticeId) {
        return preNoticeRepository.findById(preNoticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    @Override
    public PreNotice updatePreNotice(Long preNoticeId, PreNoticeRequest.UpdatePreNoticeDto request) {
        PreNotice preNotice = getPreNotice(preNoticeId);
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->  new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!preNotice.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        preNotice.update(request);
        return preNotice;
    }

    @Override
    public void deletePreNotice(Long preNoticeId) {
        PreNotice preNotice = getPreNotice(preNoticeId);
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!preNotice.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN); // 권한 없음 에러
        }

         preNoticeRepository.delete(preNotice);
    }
}

















