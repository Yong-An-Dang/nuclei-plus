package com.g3g4x5x6.nuclei.http.models.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class SyncUploadRequest {
    private String action;
    private int count;
    private List<SyncTemplate> templates;

    // Getters and Setters
}