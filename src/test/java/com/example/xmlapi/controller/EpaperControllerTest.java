package com.example.xmlapi.controller;

import com.example.xmlapi.TestBase;
import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.service.EpaperService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EpaperControllerTest extends TestBase {

    MockMvc mockMvc;

    @Mock
    EpaperService epaperService;

    @InjectMocks
    EpaperController epaperController;

    final static String INVALID_PARAM = "invalid_param";

    @SneakyThrows
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(epaperController).build();
    }

    @SneakyThrows
    @Test
    void postingWithValidFileShouldReturnEpaperId() {
        when(epaperService.persistEpaperFrom(any(MultipartFile.class))).thenReturn(Epaper.builder().id(69L).build());
        mockMvc.perform(multipart("/api/epapers")
                        .file(VALID_FILE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("69"));
    }

    @SneakyThrows
    @Test
    void postingWithNoFileAttachedShouldReturnBadRequestWithProperExceptionMessage() {
        MvcResult result = mockMvc.perform(multipart("/api/epapers"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Required request part 'file' is not present"));
    }

    @SneakyThrows
    @Test
    void gettingEpapersWithinvalidSortParameterShouldReturnBadRequestWithProperExceptionMessage() {
        MvcResult result = mockMvc.perform(get("/api/epapers").param("sort", INVALID_PARAM))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Invalid value(s) in sort parameter: [" + INVALID_PARAM + "]"));
    }

    @SneakyThrows
    @Test
    void gettingEpapersThatReturnsEmptyPageShouldReturnNoConentAndEmptyBody() {
        when(epaperService.findAllMatchingEpapersBy(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/epapers"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @SneakyThrows
    @Test
    void gettingEpapersThatReturnsValidPageShouldReturnOKAndListInTheBodyAndProperMapKeys() {
        when(epaperService.findAllMatchingEpapersBy(any(), any())).thenReturn(new PageImpl<>(List.of(SOME_EPAPER, OTHER_EPAPER)));

        MvcResult result = mockMvc.perform(get("/api/epapers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertAll(
                () -> assertTrue(result.getResponse().getContentAsString().contains(EXPECTED_JSON_LIST)),
                () -> assertTrue(result.getResponse().getContentAsString().contains("epapers")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("totalPages")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("totalItems")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("currentPage"))
        );
    }
}