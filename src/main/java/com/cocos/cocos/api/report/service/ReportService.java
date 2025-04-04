package com.cocos.cocos.api.report.service;

import com.cocos.cocos.db.report.entity.Report;
import com.cocos.cocos.db.report.repository.ReportRepository;
import com.cocos.cocos.enums.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public void addReport(final Long memberId, final Long targetId, final ReportType reportType) {
        reportRepository.save(
                Report.builder()
                        .reporterId(memberId)
                        .targetId(targetId)
                        .reportType(reportType)
                        .build()
        );
    }
}
