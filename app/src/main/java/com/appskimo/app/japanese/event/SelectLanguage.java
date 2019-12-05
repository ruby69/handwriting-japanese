package com.appskimo.app.japanese.event;

import com.appskimo.app.japanese.domain.SupportLanguage;

import lombok.Data;

@Data
public class SelectLanguage {
    private SupportLanguage supportLanguage;

    public SelectLanguage(SupportLanguage supportLanguage) {
        this.supportLanguage = supportLanguage;
    }
}
