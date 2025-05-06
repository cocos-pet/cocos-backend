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

    List<Hospital> findAllByNameContainingAndDistrictIdInAndIdLessThan(final String name, final List<Long> districtIds, final Long id, final Pageable pageable);

    List<Hospital> findAllByNameContainingAndIdLessThan(final String name, final Long cursorId, final Pageable pageable);

    List<Hospital> findAllByNameContainingAndDistrictIdIn(final String name, final List<Long> districtIds, final Pageable pageable);

    List<Hospital> findAllByNameContaining(final String name, final Pageable pageable);

    //TODO: 추후 queryDSL 도입 고민 필요
    @Query("""
            SELECT h FROM Hospital h
            WHERE h.districtId IN :districtIds
              AND (
                h.reviewCount < :reviewCount
                OR (h.reviewCount = :reviewCount AND h.id < :cursorId)
              )
            """)
    List<Hospital> findAllByDistrictIdInWithCursor(@Param("districtIds") final List<Long> districtIds, @Param("cursorId") final Long cursorId, @Param("reviewCount") final Integer reviewCount, final Pageable pageable);

    @Query("""
            SELECT h FROM Hospital h
            WHERE (
                h.reviewCount < :reviewCount
                OR (h.reviewCount = :reviewCount AND h.id < :cursorId)
              )
            """)
    List<Hospital> findAllWithCursor(final Long cursorId, final Integer reviewCount, final Pageable pageable);

    List<Hospital> findAllByDistrictIdIn(final List<Long> districtIds, final Pageable pageable);
}
