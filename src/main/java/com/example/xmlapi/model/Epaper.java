package com.example.xmlapi.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@Table(name = "e_paper")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Epaper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String newspaperName;
    Integer width;
    Integer height;
    Integer dpi;
    Timestamp uploadTime;
    String fileName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Epaper epaper = (Epaper) o;
        return id != null && Objects.equals(id, epaper.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
