package com.g3g4x5x6.nuclei.ultils.os;

public enum OsType {

    Linux("Linux"), Mac_OS("Mac OS"), Mac_OS_X("Mac OS X"), Windows("Windows");

    private OsType(String desc) {
        this.description = desc;
    }

    public String toString() {
        return description;
    }

    private String description;
}