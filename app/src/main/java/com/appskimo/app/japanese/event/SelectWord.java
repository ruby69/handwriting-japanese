package com.appskimo.app.japanese.event;

import lombok.Data;

@Data
public class SelectWord {
    private int position;

    public SelectWord(int position) {
        this.position = position;
    }
}
