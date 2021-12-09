package com.example.xmlapi;

import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.repository.EpaperRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class XmlApiApplicationTests extends TestBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EpaperRepository epaperRepository;

    @SneakyThrows
    @Test
    void shouldProperlyUploadAttachedValidFile() {
        mockMvc.perform(multipart("/api/epapers")
                        .file(VALID_FILE))
                .andDo(print())
                .andExpect(status().isCreated());

        Epaper persistedEpaper = epaperRepository.findAll().get(0);

        assertAll(
                () -> assertThat(persistedEpaper.getId()).isEqualTo(1),
                () -> assertThat(persistedEpaper.getNewspaperName()).isEqualTo("abb"),
                () -> assertThat(persistedEpaper.getFileName()).isEqualTo("validXmlFile.xml"),
                () -> assertThat(persistedEpaper.getDpi()).isEqualTo(160),
                () -> assertThat(persistedEpaper.getWidth()).isEqualTo(1280),
                () -> assertThat(persistedEpaper.getHeight()).isEqualTo(752)
        );
    }

    @SneakyThrows
    @Test
    void shouldProperlyIncrementIdOfPersistedEpapers() {
        MvcResult mvcResult1 = mockMvc.perform(multipart("/api/epapers")
                        .file(VALID_FILE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(multipart("/api/epapers")
                        .file(VALID_FILE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertAll(
                () -> assertThat(mvcResult1.getResponse().getContentAsString()).isEqualTo("1"),
                () -> assertThat(mvcResult2.getResponse().getContentAsString()).isEqualTo("2")
        );
    }
}
