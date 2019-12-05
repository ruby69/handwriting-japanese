package com.appskimo.app.japanese.event;

import lombok.Data;

@Data
public class RefreshListWord {
    private int position;
    private boolean all;

    public RefreshListWord(int position) {
        this.position = position;
    }

    public RefreshListWord(boolean all) {
        this.all = all;
    }
}
