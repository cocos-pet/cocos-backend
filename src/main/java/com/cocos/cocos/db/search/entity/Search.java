package com.cocos.cocos.db.search.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "search")
public class Search extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "member_id", nullable = false)
    private Long memberId;
}
