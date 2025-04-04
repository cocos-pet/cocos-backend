package com.cocos.cocos.report;

import com.cocos.cocos.api.post.service.PostService;
import com.cocos.cocos.api.report.service.ReportService;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.db.report.entity.Report;
import com.cocos.cocos.db.report.repository.ReportRepository;
import com.cocos.cocos.enums.report.ReportType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("신고 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ReportServiceTest {

    @InjectMocks
    ReportService reportService;
    @Mock
    private ReportRepository reportRepository;


    @Test
    @DisplayName("게시글을 신고할 수 있다.")
    void addPostReport() {
        //given
        final Long targetId = 1L;
        final ReportType reportType = ReportType.POST;
        final Long memberId = 1L;
        final Report report = Report.builder()
                .reporterId(memberId)
                .reportType(reportType)
                .targetId(targetId)
                .build();

        //when
        reportService.addReport(memberId, targetId, reportType);

        //then
        BDDMockito.verify(reportRepository, times(1)).save(Mockito.any(Report.class));
    }
}
