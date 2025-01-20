package com.cocos.cocos.api.search.controller;

import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.api.search.service.SearchService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/search")
@RequiredArgsConstructor
public class SearchController implements SearchControllerSwagger {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<BaseResponse<SearchResponse>> getSearch() {
        return SuccessResponse.success(SuccessMessage.OK, searchService.getSearch(PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> addSearch(
            @RequestParam(name = "keyword") final String keyword
    ) {
        return SuccessResponse.success(SuccessMessage.OK, searchService.addSearch(PrincipalHandler.getMemberIdFromPrincipal(), keyword));
    }
}
