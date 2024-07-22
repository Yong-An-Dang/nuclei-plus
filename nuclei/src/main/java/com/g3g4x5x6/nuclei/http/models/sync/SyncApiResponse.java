package com.g3g4x5x6.nuclei.http.models.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SyncApiResponse {
    private int code;
    private String reason;
    private List<SyncTemplate> templates;
    private int total;
    private int pageSize;
    private int currentPage;

    // Getters and Setters
}