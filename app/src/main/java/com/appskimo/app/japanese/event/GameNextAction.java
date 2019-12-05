package com.appskimo.app.japanese.event;

import lombok.Getter;

@Getter
public class GameNextAction {
    private Kind kind;

    public enum Kind {
        MAIN, DUMMY1, PLAY, DUMMY2, HISTORY;
    }

    public GameNextAction(Kind kind) {
        this.kind = kind;
    }
}
