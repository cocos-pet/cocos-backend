package com.cocos.cocos.db.report.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.report.ReportType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.tool.schema.TargetType;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "report")
public class Report extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Builder
    public Report(Long reporterId, Long targetId, ReportType reportType) {
        this.reporterId = reporterId;
        this.targetId = targetId;
        this.reportType = reportType;
    }
}
