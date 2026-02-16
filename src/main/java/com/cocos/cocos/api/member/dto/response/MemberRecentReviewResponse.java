package com.cocos.cocos.api.member.dto.response;

import com.cocos.cocos.enums.hospital.VisitTimeUnit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record MemberRecentReviewResponse(
        String diseaseBody,
        TimeSinceVisit timeSinceVisit
) {

    public static MemberRecentReviewResponse from(
            final String diseaseBody,
            final LocalDateTime visitedAt,
            final Clock clock
    ) {
        return new MemberRecentReviewResponse(
                diseaseBody,
                TimeSinceVisit.from(visitedAt, clock)
        );
    }

    public record TimeSinceVisit(
            int value,
            VisitTimeUnit unit
    ) {
        public static TimeSinceVisit from(final LocalDateTime visitedAt, final Clock clock) {
            if (visitedAt == null) {
                return null;
            }

            final LocalDateTime now = LocalDateTime.now(clock);
            final long days = Math.max(0, ChronoUnit.DAYS.between(visitedAt, now));

            if (days < VisitTimeUnit.MONTH.getDays()) {
                return new TimeSinceVisit((int) days, VisitTimeUnit.DAY);
            }

            return new TimeSinceVisit(
                    (int) (days / VisitTimeUnit.MONTH.getDays()),
                    VisitTimeUnit.MONTH
            );
        }
    }
}
