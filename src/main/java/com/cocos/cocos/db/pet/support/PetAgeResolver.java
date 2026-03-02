package com.cocos.cocos.db.pet.support;

import java.time.Clock;
import java.time.LocalDate;

public final class PetAgeResolver {

    private PetAgeResolver() {
    }

    public static AgeAndBirthDate resolve(
            Integer age,
            LocalDate birthDate,
            Clock clock
    ) {
        if (age != null && birthDate == null) {
            birthDate = LocalDate.of(
                    LocalDate.now(clock).getYear() - age,
                    1,
                    1
            );
        } else if (age == null) {
            age = PetAgeCalculator.calculate(birthDate, clock);
        }

        return new AgeAndBirthDate(age, birthDate);
    }

    public record AgeAndBirthDate(Integer age, LocalDate birthDate) {
    }
}
