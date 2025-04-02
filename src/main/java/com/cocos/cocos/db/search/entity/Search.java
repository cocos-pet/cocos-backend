package com.cocos.cocos.db.search.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.search.SearchType;
import jakarta.persistence.*;
import lombok.Builder;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "search_type", nullable = false)
    private SearchType searchType;

    @Builder
    public Search(String keyword, Long memberId, SearchType searchType) {
        this.keyword = keyword;
        this.memberId = memberId;
        this.searchType = searchType;
    }
}
