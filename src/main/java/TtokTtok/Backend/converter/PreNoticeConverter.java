package TtokTtok.Backend.converter;

import TtokTtok.Backend.domain.PreNotice;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.web.dto.PreNoticeRequest;
import TtokTtok.Backend.web.dto.PreNoticeResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PreNoticeConverter {

    public static PreNotice toPreNotice(PreNoticeRequest.CreatePreNoticeDto request, User user) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate eventDate = LocalDate.parse(request.getEventDate(), dateFormatter);

        String[] times = request.getEventTime().split(" ~ ");
        LocalTime startTime = LocalTime.parse(times[0].trim());
        LocalTime endTime = LocalTime.parse(times[1].trim());

        return PreNotice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .eventDate(eventDate)
                .eventStartTime(startTime)
                .eventEndTime(endTime)
                .eventReason(request.getEventReason())
                .user(user)
                .apartment(user.getApartment())
                .build();
    }

    public static PreNoticeResponse.PreNoticeDetailDto toPreNoticeDetailDto(PreNotice preNotice) {
        String eventDate = preNotice.getEventDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String eventTime = preNotice.getEventStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                " ~ " +
                preNotice.getEventEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        return PreNoticeResponse.PreNoticeDetailDto.builder()
                .preNoticeId(preNotice.getId())
                .authorDong(preNotice.getUser().getDong())
                .authorHosu(preNotice.getUser().getHosu())
                .title(preNotice.getTitle())
                .content(preNotice.getContent())
                .eventDate(eventDate)
                .eventTime(eventTime)
                .eventReason(preNotice.getEventReason())
                .createdAt(preNotice.getCreatedAt())
                .modifiedAt(preNotice.getModifiedAt())
                .build();
    }

    public static PreNoticeResponse.PreNoticePreviewDto toPreNoticePreviewDto(PreNotice preNotice) {
        String eventDate = preNotice.getEventDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String eventTime = preNotice.getEventStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                " ~ " +
                preNotice.getEventEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        return PreNoticeResponse.PreNoticePreviewDto.builder()
            .preNoticeId(preNotice.getId())
            .authorDong(preNotice.getUser().getDong())
            .authorHosu(preNotice.getUser().getHosu())
            .title(preNotice.getTitle())
            .eventDate(eventDate)
            .eventTime(eventTime)
            .eventReason(preNotice.getEventReason())
            .createdAt(preNotice.getCreatedAt())
            .build();
    }

    public static PreNoticeResponse.PreNoticeListResponse toPreNoticeListResponse(Page<PreNotice> preNoticeList) {
        List<PreNoticeResponse.PreNoticePreviewDto> preNoticePreviewDtoList = preNoticeList.stream()
                .map(PreNoticeConverter::toPreNoticePreviewDto).collect(Collectors.toList());

        return PreNoticeResponse.PreNoticeListResponse.builder()
                .preNotices(preNoticePreviewDtoList)
                .listSize(preNoticePreviewDtoList.size())
                .totalPage(preNoticeList.getTotalPages())
                .totalElements(preNoticeList.getTotalElements())
                .isFirst(preNoticeList.isFirst())
                .isLast(preNoticeList.isLast())
                .build();
    }
}




















