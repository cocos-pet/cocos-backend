package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.api.post.facade.PostLikeFacade;
import com.cocos.cocos.api.post.service.PostLikeService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.util.validation.EntityExistsValidator;
import com.cocos.cocos.validation.post.PostIdConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${api.prefix}/likes")
@RequiredArgsConstructor
public class PostLikeController implements PostLikeControllerSwagger {

    private final PostLikeService postLikeService;
    private final EntityExistsValidator entityExistsValidator;
    private final PostLikeFacade postLikeFacade;

    @PostMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> addPostLike(
            @PathVariable(name = "postId") @PostIdConstraint final Long postId
    ) {
        entityExistsValidator.validatePetByMemberId(PrincipalHandler.getMemberIdFromPrincipal());
        postLikeFacade.addPostLikeAndNotification(PrincipalHandler.getMemberIdFromPrincipal(), postId);
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePostLike(
            @PathVariable(name = "postId") @PostIdConstraint final Long postId
    ) {
        entityExistsValidator.validatePetByMemberId(PrincipalHandler.getMemberIdFromPrincipal());
        postLikeService.deletePostLike(PrincipalHandler.getMemberIdFromPrincipal(), postId);
        return SuccessResponse.success(SuccessMessage.OK, null);
    }
}
