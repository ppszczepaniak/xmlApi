package com.example.xmlapi.service;

import com.example.xmlapi.model.Epaper;
import org.springframework.web.multipart.MultipartFile;

public interface EpaperService {

    Epaper processFile(MultipartFile file);
}
