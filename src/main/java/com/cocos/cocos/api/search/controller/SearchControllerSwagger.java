package com.cocos.cocos.api.search.controller;

import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Search Controller", description = "최근 검색어 관련 API")
public interface SearchControllerSwagger {

    @Operation(summary = "최근 검색어 조회 API", description = "최근 검색어를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "최근 검색어 조회 성공")
    public ResponseEntity<BaseResponse<SearchResponse>> getSearch();
}
