package com.cocos.cocos.db.heart.repository;

import com.cocos.cocos.db.heart.entity.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
}
