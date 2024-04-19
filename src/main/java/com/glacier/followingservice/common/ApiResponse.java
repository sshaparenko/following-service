package com.glacier.followingservice.common;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ApiResponse {
    private final boolean success;
    private final String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp(){
        return LocalDateTime.now().toString();
    }
}
