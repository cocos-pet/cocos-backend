package com.cocos.cocos.db.hospital.repository;

import com.cocos.cocos.db.hospital.entity.Hospital;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findAllByNameContainingAndTownIdAndIdLessThan(final String name, final Long townId, final Long id, final Pageable pageable);

    List<Hospital> findAllByNameContainingAndTownId(final String name, final Long townId, final Pageable pageable);

    //TODO: 추후 queryDSL 도입 고민 필요
    @Query("""
            SELECT h FROM Hospital h
            WHERE h.townId = :townId
              AND (
                h.reviewCount < :reviewCount
                OR (h.reviewCount = :reviewCount AND h.id < :cursorId)
              )
            """)
    List<Hospital> findAllByTownIdWithCursor(@Param("townId") final Long townId, @Param("cursorId") final Long cursorId, @Param("reviewCount") final Integer reviewCount, final Pageable pageable);

    List<Hospital> findAllByTownId(final Long townId, final Pageable pageable);
}
