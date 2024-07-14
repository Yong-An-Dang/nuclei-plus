package com.g3g4x5x6.nuclei.sync.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UploadRequest {
    private String action;
    private int count;
    private List<Template> templates;

    // Getters and Setters
}