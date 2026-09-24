package com.cocos.cocos.db.search.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "popular_keyword")
public class PopularKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "display_rank", nullable = false, unique = true)
    private Integer displayRank;
}
