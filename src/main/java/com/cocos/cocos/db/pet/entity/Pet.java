package com.cocos.cocos.db.pet.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.pet.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "age", nullable = false)
    @Min(1)
    private int age;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "breed_id", nullable = false)
    private Long breedId;

    @Column(name = "image",nullable = false)
    private String image;

    @Builder
    public Pet(final String name, final Gender gender, final int age, final Long memberId, final Long breedId, final String image) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.memberId = memberId;
        this.breedId = breedId;
        this.image = image;
    }

    public void updateFields(final String name, final Gender gender, final Integer age, final Long breedId) {
        //ToDo: {}작성하는 것이 의미를 더 명확하고 이후 예기치 못한 오류를 예방할 수 있어 보임
        if (name != null) this.name = name;
        if (gender != null) this.gender = gender;
        if (age != null) this.age = age;
        if (breedId != null) this.breedId = breedId;
    }
}
