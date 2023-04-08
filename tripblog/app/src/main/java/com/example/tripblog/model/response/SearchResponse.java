package com.example.tripblog.model.response;

import com.example.tripblog.model.Location;
import com.example.tripblog.model.User;

import java.io.Serializable;
import java.util.List;

public class SearchResponse implements Serializable {
    private List<User> users;
    private List<Location> locations;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
