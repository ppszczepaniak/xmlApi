package com.example.xmlapi.service;

import com.example.xmlapi.exception.XmlApiException;
import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.model.EpaperDTO;
import com.example.xmlapi.model.EpaperRequest;
import com.example.xmlapi.repository.EpaperRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpaperServiceImpl implements EpaperService {

    EpaperRepository epaperRepository;
    XmlMapper xmlMapper;
    File XSD_SCHEMA_FILE = readXsdSchemaFile();

    @Override
    public Epaper persistEpaperFrom(MultipartFile file) {

        //validate
        validate(file);

        //parse
        EpaperRequest epaperRequest = parseXmlFile(file);

        //persist
        final EpaperRequest.ScreenInfo screenInfo = epaperRequest.getDeviceInfo().getScreenInfo();
        return epaperRepository.saveAndFlush(Epaper.builder()
                .newspaperName(epaperRequest.getDeviceInfo().getAppInfo().getNewspaperName())
                .width(screenInfo.getWidth())
                .height(screenInfo.getHeight())
                .dpi(screenInfo.getDpi())
                .fileName(file.getResource().getFilename())
                .uploadTime(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .build()
        );
    }

    @Override
    public Page<Epaper> findAllMatchingEpapersBy(EpaperDTO epaperFilter, Pageable pageable) {
        Page<Epaper> pagedEpapers;
        if (epaperFilter == null) {
            pagedEpapers = epaperRepository.findAll(pageable);
        } else {
            Epaper epaperExample = Epaper.builder()
                    .id(epaperFilter.getId())
                    .newspaperName(epaperFilter.getNewspaperName())
                    .dpi(epaperFilter.getDpi())
                    .height(epaperFilter.getHeight())
                    .width(epaperFilter.getWidth())
                    .uploadTime(epaperFilter.getUploadTime())
                    .fileName(epaperFilter.getFileName())
                    .build();
            pagedEpapers = epaperRepository.findAll(Example.of(epaperExample), pageable);
        }
        log.info("XmlApiLog: " + pagedEpapers.getTotalElements() + " epaper(s) found.");
        return pagedEpapers;
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

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            log.debug("XmlApiLog: No or empty file uploaded.");
            throw new XmlApiException("No file uploaded or uploaded file is empty.", BAD_REQUEST);
        }
        validateWithXsd(file);
    }

    private void validateWithXsd(MultipartFile file) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Validator validator = factory.newSchema(XSD_SCHEMA_FILE).newValidator();
            Source xmlFile = new StreamSource(file.getInputStream());
            validator.validate(xmlFile);
            log.info("XmlApiLog: file " + file.getResource().getFilename() + " is valid.");
        } catch (SAXException e) {
            log.info("XmlApiLog: Error: file " + file.getResource().getFilename() + " is not valid. Reason:" + e);
            throw new XmlApiException("Invalid XML file.", BAD_REQUEST);
        } catch (IOException e) {
            log.info("XmlApiLog: Error during validation: " + e.getMessage());
            throw new XmlApiException("Error while getting access to file content.");
        }
    }

    private static File readXsdSchemaFile() {
        ClassPathResource cpr = new ClassPathResource("xsdSchema/schema.xsd");
        try (InputStream inputStream = cpr.getInputStream()) {
            File tempSchemaFile = File.createTempFile("schema", ".xsd");
            FileUtils.copyInputStreamToFile(inputStream, tempSchemaFile);
            return tempSchemaFile;
        } catch (IOException e) {
            log.info("XmlApiLog: can't read xsd schema file: " + e.getMessage());
            throw new XmlApiException("Internal error.");
        }
    }
}
