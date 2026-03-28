package com.cocos.cocos.api.report.controller;

import com.cocos.cocos.api.report.service.ReportService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.enums.report.ReportType;
import com.cocos.cocos.util.PrincipalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/reports")
@RequiredArgsConstructor
public class ReportController implements ReportControllerSwagger{

    private final ReportService reportService;

    @PostMapping("/{targetId}")
    public ResponseEntity<BaseResponse<Void>> addReport(
            @PathVariable(name = "targetId") final Long targetId,
            @RequestParam(name = "reportType") final ReportType reportType
    ) {
        reportService.addReport(PrincipalHandler.getMemberIdFromPrincipal(), targetId, reportType);
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

}
