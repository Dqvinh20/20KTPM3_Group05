package com.example.tripblog.ui.search;

public class Suggest_Search_Object {
    private String title;
    private String subtitle;
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Suggest_Search_Object(String title, String subtitle, String type) {
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "Suggest_Search_Object{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
