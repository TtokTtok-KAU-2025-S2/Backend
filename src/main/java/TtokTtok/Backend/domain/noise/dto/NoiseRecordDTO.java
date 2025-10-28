package TtokTtok.Backend.domain.noise.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NoiseRecordDTO {
    private Long recordId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate logDate; //소음 발생 날짜

    @JsonFormat(pattern = "HH:mm")
    private LocalTime logTime; //소음 발생 시간(HH:MM)

    private NoiseCategory noiseType; //enum 파일 생성함
    private Integer noiseLevel; //소음의 강도(1-5)
    private String memo;  //세부 내용

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    //Json -> Java 객체로 변화하는 Jackson 라이브러리의 역질렬화를 위해 필요
    public NoiseRecordDTO() {

    }

    public NoiseRecordDTO(Long recordId, LocalDate logDate, LocalTime logTime, NoiseCategory noiseType, Integer noiseLevel, String memo, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.recordId = recordId;
        this.logDate = logDate;
        this.logTime = logTime;
        this.noiseType = noiseType;
        this.noiseLevel = noiseLevel;
        this.memo = memo;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    // getter / setter
   public  Long getRecordId() {
        return recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }


    public LocalDate getLogDate() {
        return logDate;
    }
    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }



    public LocalTime getLogTime() {
        return logTime;
    }
    public void setLogTime(LocalTime logTime) {
        this.logTime = logTime;
    }



    public NoiseCategory getNoiseType() {
        return noiseType;
    }
    public void setNoiseType(NoiseCategory noiseType) {
        this.noiseType = noiseType;
    }



    public Integer getNoiseLevel() {
        return noiseLevel;
    }
    public void setNoiseLevel(Integer noiseLevel) {
        this.noiseLevel = noiseLevel;
    }



    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }

}
