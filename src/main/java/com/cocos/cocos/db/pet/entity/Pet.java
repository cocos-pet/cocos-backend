package com.cocos.cocos.db.pet.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.db.pet.support.PetAgeCalculator;
import com.cocos.cocos.enums.pet.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pet")
public class Pet extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "age")
    @Min(1)
    private Integer age;

    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;

    @Column(name = "breed_id", nullable = false)
    private Long breedId;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Builder
    public Pet(final String name, final Gender gender, final Integer age, final LocalDate birthDate, final Long memberId, final Long breedId,
               final String image) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.birthDate = birthDate;
        this.memberId = memberId;
        this.breedId = breedId;
        this.image = image;
    }

    public void updateFields(final String name, final Gender gender, final Integer age, final LocalDate birthDate, final Long breedId) {
        if (name != null) {
            this.name = name;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (age != null) {
            this.age = age;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (breedId != null) {
            this.breedId = breedId;
        }
    }

    public int calculateAge(final Clock clock) {
        return PetAgeCalculator.calculate(this.birthDate, clock);
    }
}
