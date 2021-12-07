package com.example.xmlapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpaperRequest {

    DeviceInfo deviceInfo;
    GetPages getPages;

    @NoArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DeviceInfo {
        String name;
        String id;
        ScreenInfo screenInfo;
        OsInfo osInfo;
        AppInfo appInfo;
    }

    @NoArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GetPages {
        Integer editionDefId;
        Date publicationDate;
    }

    @NoArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ScreenInfo {
        Integer width;
        Integer height;
        Integer dpi;
    }

    @NoArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OsInfo {
        String name;
        String version;
    }

    @NoArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AppInfo {
        String newspaperName;
        String version;
    }

}