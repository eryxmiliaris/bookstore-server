package com.vb.bookstore.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private boolean success;
    private String message;
    private Map<String, String> errors = null;

    public MessageResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}