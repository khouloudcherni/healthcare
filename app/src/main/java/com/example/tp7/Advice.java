package com.example.tp7;

public class Advice {
    public String id;
    public String title;
    public String description;
    public String createdBy;
    public String imageUrl;

    public Advice() {}

    public Advice(String title, String description, String createdBy) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Advice(String title, String description, String createdBy, String imageUrl) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.imageUrl = imageUrl;
    } public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
