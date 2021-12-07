package com.example.xmlapi.controller;

import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.service.EpaperService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@Controller
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/api")
public class EpaperController {

    EpaperService epaperService;

    @PostMapping(value = "/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("XmlApiLog: Request processing started.");

        Epaper epaper = epaperService.processFile(file);

        log.info("XmlApiLog: File successfully uploaded.");

        return ResponseEntity.status(CREATED).body(epaper.getId());
    }
}
