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

    @Column(nullable = false)
    private String speclmanageInfo;

    @Column(nullable = false)
    private LocalDateTime dDay;

    @Lob
    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private boolean representative;

    @Column(nullable = false)
    private float solidHumidity;

    @Column(nullable = false)
    private float airHumidity;

    @Column(nullable = false)
    private float temperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
