package com.sam.mini_plm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "parts")
@Data



public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String partNumber;

    private String name;
    private String description;
    private String version;
    private String lifecycleState;
}
