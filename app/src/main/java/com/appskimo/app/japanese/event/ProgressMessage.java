package com.appskimo.app.japanese.event;

import lombok.Data;

@Data
public class ProgressMessage {
    private String message;

    public ProgressMessage(String message) {
        this.message = message;
    }
}
