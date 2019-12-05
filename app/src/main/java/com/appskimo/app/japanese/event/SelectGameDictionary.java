package com.appskimo.app.japanese.event;

import lombok.Data;

@Data
public class SelectGameDictionary {
    private int position;

    public SelectGameDictionary(int position) {
        this.position = position;
    }
}
