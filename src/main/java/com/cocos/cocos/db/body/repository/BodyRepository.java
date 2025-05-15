package com.cocos.cocos.db.body.repository;

import com.cocos.cocos.db.body.entity.Body;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyRepository extends JpaRepository<Body, Long> {
    long countByIdIn(final List<Long> ids);
}
