package com.example.xmlapi;

import com.example.xmlapi.model.Epaper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestBase {

    protected static final Epaper SOME_EPAPER = Epaper.builder()
            .id(1L)
            .fileName("someFileName")
            .newspaperName("someName")
            .dpi(123)
            .height(234)
            .width(456)
            .build();

    protected static final Epaper OTHER_EPAPER = Epaper.builder()
            .id(77L)
            .fileName("otherFileName")
            .newspaperName("otherName")
            .dpi(789)
            .height(987)
            .width(333)
            .build();

    protected static final String EXPECTED_JSON_LIST = "[{\"id\":" + SOME_EPAPER.getId() +
            ",\"newspaperName\":\"" + SOME_EPAPER.getNewspaperName() +
            "\",\"width\":" + SOME_EPAPER.getWidth() +
            ",\"height\":" + SOME_EPAPER.getHeight() +
            ",\"dpi\":" + SOME_EPAPER.getDpi() +
            ",\"uploadTime\":" + SOME_EPAPER.getUploadTime() +
            ",\"fileName\":\"" + SOME_EPAPER.getFileName() +
            "\"},{\"id\":" + OTHER_EPAPER.getId() +
            ",\"newspaperName\":\"" + OTHER_EPAPER.getNewspaperName() +
            "\",\"width\":" + OTHER_EPAPER.getWidth() +
            ",\"height\":" + OTHER_EPAPER.getHeight() +
            ",\"dpi\":" + OTHER_EPAPER.getDpi() +
            ",\"uploadTime\":" + OTHER_EPAPER.getUploadTime() +
            ",\"fileName\":\"" + OTHER_EPAPER.getFileName() +
            "\"}]";

    protected final MockMultipartFile VALID_FILE;
    protected final MockMultipartFile EMPTY_FILE;

    @SneakyThrows
    public TestBase() {
        this.VALID_FILE = new MockMultipartFile("file", "validXmlFile.xml", "multipart/form-data",
                IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/validXmlFile.xml")),
                        StandardCharsets.UTF_8).getBytes());
        this.EMPTY_FILE = new MockMultipartFile("file", "emptyFile.xml", "multipart/form-data",
                IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/emptyFile.xml")),
                        StandardCharsets.UTF_8).getBytes());
    }
}
