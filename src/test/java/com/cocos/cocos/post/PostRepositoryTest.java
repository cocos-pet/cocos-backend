package com.cocos.cocos.post;

import com.cocos.cocos.config.JpaAuditingConfig;
import com.cocos.cocos.config.QuerydslConfig;
import com.cocos.cocos.db.post.entity.Post;
import com.cocos.cocos.db.post.repository.PostRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("게시글 레포지토리 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({JpaAuditingConfig.class, QuerydslConfig.class})
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("저장 후 반환하는 엔티티에는 아이디가 있다.")
    void hasIdAfterSave() {
        //given, when
        final Post actual = postRepository.save(Post.builder()
                .title("title")
                .content("content")
                .memberId(1L)
                .categoryId(1L)
                .build());

        //then
        Assertions.assertNotNull(actual.getId());
    }

}
