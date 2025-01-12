package com.cocos.cocos.db.pet.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.pet.Gender;
import jakarta.persistence.*;
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
}
