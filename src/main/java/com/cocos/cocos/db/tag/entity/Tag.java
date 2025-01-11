package com.cocos.cocos.db.tag.entity;

import com.cocos.cocos.enums.tag.TagType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "tag_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TagType tagType;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;
}
