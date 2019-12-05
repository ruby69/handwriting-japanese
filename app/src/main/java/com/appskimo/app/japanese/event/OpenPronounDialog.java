package com.appskimo.app.japanese.event;

import com.appskimo.app.japanese.domain.Word;

import lombok.Data;

@Data
public class OpenPronounDialog {
    private Word word;

    public OpenPronounDialog(Word word) {
        this.word = word;
    }
}
