package com.example.xmlapi.service;

import com.example.xmlapi.model.Epaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EpaperService {
    Epaper persistEpaperFrom(MultipartFile file);

    Epaper findById(Long id);

    Page<Epaper> findAll(Pageable pageable);

    Page<Epaper> findAllByNewspaperName(String newspaperName, Pageable pageable);
}
