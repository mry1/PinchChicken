package com.example.util;

/**
 * File Description:
 * Author:weisc
 * Create Date:18-1-3
 * Change List:
 */

public class Animation {
    private final String name;
    private final String path;
    private String[] resList;

    public Animation(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setResList(String[] resList) {
        this.resList = resList;
    }

    public String[] getResList() {
        return resList;
    }
}
