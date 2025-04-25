package com.cocos.cocos.db.review.db;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.pet.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review")
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "visited_at", nullable = false)
    private String visitedAt;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "weight", nullable = false)
    private int weight;

    @Column(name = "purpose_id", nullable = false)
    private Long purposeId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "breed_id", nullable = false)
    private Long breedId;

    @Column(name = "disease_id", nullable = false)
    private Long diseaseId;

    @Builder
    public Review(final String visitedAt, final Gender gender, final int weight, final Long purposeId, final String content, final Long hospitalId, final Long memberId, final Long breedId, final Long diseaseId) {
        this.visitedAt = visitedAt;
        this.gender = gender;
        this.weight = weight;
        this.purposeId = purposeId;
        this.content = content;
        this.hospitalId = hospitalId;
        this.memberId = memberId;
        this.breedId = breedId;
        this.diseaseId = diseaseId;
    }
}
