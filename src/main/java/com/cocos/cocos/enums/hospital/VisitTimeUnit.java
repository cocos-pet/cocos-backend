package com.cocos.cocos.enums.hospital;

import lombok.Getter;

@Getter
public enum VisitTimeUnit {

    DAY(1),
    MONTH(30);

    private final int days;

    VisitTimeUnit(final int days) {
        this.days = days;
    }

}
