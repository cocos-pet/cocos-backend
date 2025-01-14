package com.cocos.cocos.db.pet.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.pet.Gender;
import jakarta.persistence.*;
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
    private int age;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "breed_id", nullable = false)
    private Long breedId;

    @Builder
    public Pet(final String name, final Gender gender, final int age, final Long memberId, final Long breedId) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.memberId = memberId;
        this.breedId = breedId;
    }
}
