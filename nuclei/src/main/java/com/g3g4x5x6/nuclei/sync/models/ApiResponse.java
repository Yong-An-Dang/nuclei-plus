package com.g3g4x5x6.nuclei.sync.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponse {
    private int code;
    private String reason;
    private List<Template> templates;
    private int total;
    private int pageSize;
    private int currentPage;

    // Getters and Setters
}