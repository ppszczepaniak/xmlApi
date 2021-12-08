package com.example.xmlapi.service;

import com.example.xmlapi.model.Epaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EpaperService {

    Epaper persistEpaperFrom(MultipartFile file);

    Page<Epaper> findAll(Pageable pageable);

    Epaper findById(Long id);
}
