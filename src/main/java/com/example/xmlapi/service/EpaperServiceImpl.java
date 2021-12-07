package com.example.xmlapi.service;

import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.model.EpaperRequest;
import com.example.xmlapi.repository.EpaperRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpaperServiceImpl implements EpaperService {

    EpaperRepository epaperRepository;
    XmlMapper xmlMapper;

    @Override
    public Epaper processFile(MultipartFile file) {

        final EpaperRequest epaperRequest = parseXmlFile(file);

        final EpaperRequest.ScreenInfo screenInfo = epaperRequest.getDeviceInfo().getScreenInfo();

        return epaperRepository.saveAndFlush(Epaper.builder()
                .newspaperName(epaperRequest.getDeviceInfo().getAppInfo().getNewspaperName())
                .width(screenInfo.getWidth())
                .height(screenInfo.getHeight())
                .dpi(screenInfo.getDpi())
                .fileName(file.getResource().getFilename())
                .uploadTime(Timestamp.valueOf(LocalDateTime.now()))
                .build()
        );
    }

    private EpaperRequest parseXmlFile(MultipartFile file) {
        EpaperRequest epaperRequest = null;
        try {
            String xmlString = new String((file.getInputStream().readAllBytes()), StandardCharsets.UTF_8);
            epaperRequest = xmlMapper.readValue(xmlString, EpaperRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return epaperRequest;
    }
}
