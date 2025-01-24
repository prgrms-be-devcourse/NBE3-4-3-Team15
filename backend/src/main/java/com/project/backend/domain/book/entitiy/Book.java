package com.project.backend.domain.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    private int id;

    private String title;

    private String author;

    private String kind;

    private String discription;

    private String image;
}
