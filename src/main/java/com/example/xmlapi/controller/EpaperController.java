package com.example.xmlapi.controller;

import com.example.xmlapi.exception.XmlApiException;
import com.example.xmlapi.exception.XmlApiExceptionDTO;
import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.model.EpaperDTO;
import com.example.xmlapi.service.EpaperService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Controller
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/api")
public class EpaperController {

    EpaperService epaperService;
    static List<String> VALID_SORT_PARAMS = List.of("asc", "desc", "id", "newspaperName", "width", "height", "dpi", "uploadTime", "fileName");

    @PostMapping(value = "/epapers")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("XmlApiLog: Request processing started.");

        Epaper epaper = epaperService.persistEpaperFrom(file);

        log.info("XmlApiLog: File successfully uploaded.");

        return ResponseEntity.status(CREATED).body(epaper.getId());
    }

    @GetMapping(value = "/epapers")
    public ResponseEntity<Map<String, Object>> findAllPaged(@RequestBody(required = false) EpaperDTO epaperFilter,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size,
                                                            @RequestParam(defaultValue = "id,asc") String[] sort) {
        log.info("XmlApiLog: Getting all entities with settings: page = {}, size = {}, sort = {}, filteredBy = {}", page, size, sort, epaperFilter);

        validateSortParameter(sort);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(findDirection(sort[1]), sort[0])));
            Page<Epaper> pagedEpapers = epaperService.findAllMatchingEpapersBy(epaperFilter, pageable);
            List<Epaper> epaperList = pagedEpapers.getContent();
            if (epaperList.isEmpty()) {
                return new ResponseEntity<>(NO_CONTENT);
            } else {
                return ResponseEntity.ok(Map.of(
                        "epapers", epaperList,
                        "currentPage", pagedEpapers.getNumber(),
                        "totalItems", pagedEpapers.getTotalElements(),
                        "totalPages", pagedEpapers.getTotalPages()
                ));
            }

        } catch (Exception e) {
            log.info("XmlApiLog: Error while processing the request: " + e.getMessage());
            throw new XmlApiException("Error while processing the request");
        }
    }

    private static void validateSortParameter(String[] sort) {
        Set<String> sortFromRequest = new HashSet<>(Arrays.asList(sort));
        VALID_SORT_PARAMS.forEach(sortFromRequest::remove);

        if (!sortFromRequest.isEmpty()) {
            log.info("XmlApiLog: invalid value(s) in sort parameter: " + sortFromRequest);
            throw new XmlApiException("Invalid value(s) in sort parameter: " + sortFromRequest, BAD_REQUEST);
        }
    }

    private static Sort.Direction findDirection(String direction) {
        return "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
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

    @ExceptionHandler(HttpMessageConversionException.class)
    ResponseEntity<XmlApiExceptionDTO> handleException(HttpMessageConversionException exception) {
        log.debug("HttpMessageConversionException: " + exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(
                XmlApiExceptionDTO.from(new XmlApiException(exception.getMessage())));
    }

    @ExceptionHandler(XmlApiException.class)
    ResponseEntity<XmlApiExceptionDTO> handleException(XmlApiException exception) {
        log.debug("XmlApiLog: XmlApiException: " + exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(XmlApiExceptionDTO.from(exception));
    }

}
