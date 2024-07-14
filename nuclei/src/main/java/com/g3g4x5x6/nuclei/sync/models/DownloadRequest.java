package com.g3g4x5x6.nuclei.sync.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadRequest {
    private String action;
    private Filter filter;
    private int pageSize;
    private int currentPage;

    // Getters and Setters
}