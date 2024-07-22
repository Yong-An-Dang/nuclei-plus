package com.g3g4x5x6.nuclei.http.models.sync;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncDownloadRequest {
    private String action;
    private SyncFilter filter;
    private int pageSize;
    private int currentPage;

    // Getters and Setters
}