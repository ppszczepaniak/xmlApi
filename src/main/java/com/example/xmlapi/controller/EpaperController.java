package com.example.xmlapi.controller;

import com.example.xmlapi.exception.XmlApiException;
import com.example.xmlapi.exception.XmlApiExceptionDTO;
import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.service.EpaperService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Controller
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/api")
public class EpaperController {

    EpaperService epaperService;

    @PostMapping(value = "/epapers")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("XmlApiLog: Request processing started.");

        Epaper epaper = epaperService.persistEpaperFrom(file);

        log.info("XmlApiLog: File successfully uploaded.");

        return ResponseEntity.status(CREATED).body(epaper.getId());
    }

    @GetMapping(value = "/epapers")
    public ResponseEntity<Map<String, Object>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("XmlApiLog: Getting all entities with settings: page = {}, size = {}", page, size);
        Page<Epaper> pagedEpapers = epaperService.findAll(pageable);
        List<Epaper> epaperList = pagedEpapers.getContent();
        if (epaperList.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("epapers", epaperList);
        response.put("currentPage", pagedEpapers.getNumber());
        response.put("totalItems", pagedEpapers.getTotalElements());
        response.put("totalPages", pagedEpapers.getTotalPages());
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping(value = "/epapers/{id}")
    public ResponseEntity<Epaper> findById(@PathVariable Long id) {
        log.info("XmlApiLog: Getting all entities.");
        return ResponseEntity.status(OK).body(epaperService.findById(id));
    }

    @ExceptionHandler(MultipartException.class)
    ResponseEntity<XmlApiExceptionDTO> handleException(MultipartException exception) {
        log.debug("MultipartException: " + exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(
                XmlApiExceptionDTO.from(new XmlApiException(exception.getMessage())));
    }

    @ExceptionHandler(ServletException.class)
    ResponseEntity<XmlApiExceptionDTO> handleException(ServletException exception) {
        log.debug("ServletException: " + exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(
                XmlApiExceptionDTO.from(new XmlApiException(exception.getMessage())));
    }

    @ExceptionHandler(XmlApiException.class)
    ResponseEntity<XmlApiExceptionDTO> handleException(XmlApiException exception) {
        log.debug("XmlApiLog: XmlApiException: " + exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(XmlApiExceptionDTO.from(exception));
    }
}
