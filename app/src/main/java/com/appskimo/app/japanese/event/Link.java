package com.appskimo.app.japanese.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Link {
    private String url;

    public Link(String url) {
        this.url = url;
    }
}

