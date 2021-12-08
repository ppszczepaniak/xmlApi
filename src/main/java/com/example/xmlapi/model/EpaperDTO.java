package com.example.xmlapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpaperDTO {
    Long id;
    String newspaperName;
    Integer width;
    Integer height;
    Integer dpi;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Timestamp uploadTime;
    String fileName;
}
