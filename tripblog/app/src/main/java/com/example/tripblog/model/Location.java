package com.example.tripblog.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {
    private Integer id;
    private String name;
    private String address;
    @SerializedName("formatted_address")
    private String formattedAddress;
    private String photo;
    @SerializedName("SchedulesLocations")
    private SchedulesLocations schedulesLocations;
    private Geo geo;
    public Geo getGeo() {
        return geo;
    }
    private Boolean isExpandable = false;

    public Double getLatitude() {
        if (geo != null) {
            return geo.getLatitude();
        }
        return 0.0;
    }

    public Double getLongitude() {
        if (geo != null) {
            return geo.getLongitude();
        }
        return 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getPosition() {
        if ( schedulesLocations == null) return -1;
        return schedulesLocations.getPosition();
    }

    public void setPosition(Integer position) {
        if ( schedulesLocations != null) {
            schedulesLocations.setPosition(position);
        }
    }

    public String getNote() {
        if ( schedulesLocations == null) return "";
        return schedulesLocations.getNote();
    }

    public void setNote(String note) {
        if ( schedulesLocations != null) {
            schedulesLocations.setNote(note);
        }
    }
}
