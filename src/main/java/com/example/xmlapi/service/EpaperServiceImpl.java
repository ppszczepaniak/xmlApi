package com.example.xmlapi.service;

import com.example.xmlapi.exception.XmlApiException;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpaperServiceImpl implements EpaperService {

    EpaperRepository epaperRepository;
    XmlMapper xmlMapper;

    @Override
    public Epaper processFile(MultipartFile file) {

        if (file.isEmpty()) {
            log.debug("XmlApiLog: No or empty file uploaded.");
            throw new XmlApiException("No file uploaded or uploaded file is empty.", BAD_REQUEST);
        }

        EpaperRequest epaperRequest = parseXmlFile(file);

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
        EpaperRequest epaperRequest;
        try {
            String xmlString = new String((file.getInputStream().readAllBytes()), StandardCharsets.UTF_8);
            epaperRequest = xmlMapper.readValue(xmlString, EpaperRequest.class);
        } catch (IOException e) {
            log.info("XmlApiLog: Error while parsing: " + e.getMessage());
            throw new XmlApiException("Error while parsing.");
        }
        log.info("XmlApiLog: file successfully parsed.");
        return epaperRequest;
    }
}
