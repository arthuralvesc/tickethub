package com.tickethub.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_concerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    public Concert(String artist, String description, String location, LocalDateTime dateTime) {
        this.artist = artist;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
    }
}
