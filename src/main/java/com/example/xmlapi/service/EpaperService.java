package com.example.xmlapi.service;

import com.example.xmlapi.model.Epaper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EpaperService {

    Epaper processFile(MultipartFile file);

    List<Epaper> findAll();

    Epaper findById(Long id);
}
