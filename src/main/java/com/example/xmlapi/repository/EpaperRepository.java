package com.example.xmlapi.repository;

import com.example.xmlapi.model.Epaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpaperRepository extends JpaRepository<Epaper, Long> {
    Page<Epaper> findAllByNewspaperName(String newspaperName, Pageable pageable);
}
