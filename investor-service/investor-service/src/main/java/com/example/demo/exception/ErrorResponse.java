package com.example.demo.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime time;
    private int status;
    private String error;
    private String path;

    public ErrorResponse(LocalDateTime time, int status, String error, String path) {
        this.time = time;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}