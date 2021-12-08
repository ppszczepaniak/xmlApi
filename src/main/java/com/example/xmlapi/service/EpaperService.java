package com.example.xmlapi.service;

import com.example.xmlapi.model.Epaper;
import com.example.xmlapi.model.EpaperDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EpaperService {
    Epaper persistEpaperFrom(MultipartFile file);

    Page<Epaper> findAllMatchingEpapersBy(EpaperDTO epaperFilter, Pageable pageable);
}
