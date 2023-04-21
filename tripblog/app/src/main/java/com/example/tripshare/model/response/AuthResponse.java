package com.example.tripshare.model.response;

import com.google.gson.JsonElement;

public class AuthResponse {
    private String status;
    private JsonElement data;
    private JsonElement error;

    @Override
    public String toString() {
        return "AuthResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", error=" + error +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public JsonElement getError() {
        return error;
    }

    public void setError(JsonElement error) {
        this.error = error;
    }
}
