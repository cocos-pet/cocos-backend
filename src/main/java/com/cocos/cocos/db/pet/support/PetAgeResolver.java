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
        if (age == null && birthDate == null) {
            throw new IllegalArgumentException("age 또는 birthDate 중 하나는 필수입니다.");
        }

        if (birthDate != null) {
            age = PetAgeCalculator.calculate(birthDate, clock);
        } else {
            birthDate = LocalDate.of(
                    LocalDate.now(clock).getYear() - age,
                    1,
                    1
            );
        }

        return new AgeAndBirthDate(age, birthDate);
    }

    public record AgeAndBirthDate(Integer age, LocalDate birthDate) {
    }
}
