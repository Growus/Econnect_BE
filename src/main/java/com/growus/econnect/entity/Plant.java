package com.growus.econnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "plant")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 50)
    private String cntntsNo;

    @Column(nullable = true)
    private String speclmanageInfo;

    @Column(nullable = false)
    private LocalDateTime dDay;

    @Lob
    @Column(nullable = true)
    private String image;

    @Column(nullable = true)
    private boolean representative;

    @Column(nullable = true)
    private float solidHumidity;

    @Column(nullable = true)
    private float airHumidity;

    @Column(nullable = true)
    private float temperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PlantStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
