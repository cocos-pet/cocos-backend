package com.cocos.cocos.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

public final class PetAgeCalculator {

    private PetAgeCalculator() {
    }

    public static int calculate(final LocalDate birthDate, final Clock clock) {
        return Period.between(
                birthDate,
                LocalDate.now(clock)
        ).getYears();
    }
}
