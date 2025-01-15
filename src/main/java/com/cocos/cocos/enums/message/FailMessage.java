package com.cocos.cocos.enums.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FailMessage {

    /**
     * 400
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 40000, "잘못된 요청입니다. "),
    BAD_REQUEST_REQUEST_BODY_VALID(HttpStatus.BAD_REQUEST, 40001, "request body 검증 실패입니다. "),
    BAD_REQUEST_MISSING_PARAM(HttpStatus.BAD_REQUEST, 40002, "필수 param이 없습니다. "),
    BAD_REQUEST_METHOD_ARGUMENT_TYPE(HttpStatus.BAD_REQUEST, 40003, "메서드 인자타입이 잘못되었습니다. "),
    BAD_REQUEST_NOT_READABLE(HttpStatus.BAD_REQUEST, 40004, "json 오류 혹은 reqeust body 필드 오류 입니다. "),

    /**
     * 401
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증이 필요합니다. "),

    /**
     * 403
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "권한이 없습니다. "),
    FORBIDDEN_COMMENT_DELETE(HttpStatus.FORBIDDEN, 40301, "댓글을 삭제할 권한이 없습니다. "),

    /**
     * 404
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "리소스를 찾을 수 없습니다. "),
    NOT_FOUND_ENTITY(HttpStatus.NOT_FOUND, 40401, "대상을 찾을 수 없습니다. "),
    NOT_FOUND_API(HttpStatus.NOT_FOUND, 40402, "잘못된 API입니다. "),
    NOT_FOUND_POSTLIKE(HttpStatus.NOT_FOUND, 40403, "게시물 공감을 찾을 수 없습니다."),
    NOT_FOUND_BODY(HttpStatus.NOT_FOUND, 40404, "신체 부위를 찾을 수 없습니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, 40405, "게시글을 찾을 수 없습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, 40405, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_BREED(HttpStatus.NOT_FOUND, 40406, "품종을 찾을 수 없습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, 40407, "카테고리를 찾을 수 없습니다."),
    NOT_FOUND_ANIMAL(HttpStatus.NOT_FOUND, 40407, "동물을 찾을 수 없습니다."),
    NOT_FOUND_DISEASE(HttpStatus.NOT_FOUND, 40407, "질병을 찾을 수 없습니다."),
    NOT_FOUND_SYMPTOM(HttpStatus.NOT_FOUND, 40407, "증상을 찾을 수 없습니다."),
    NOT_FOUND_POSTTAG(HttpStatus.NOT_FOUND, 40407, "태그를 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, 40413, "댓글을 찾을 수 없습니다."),

    /**
     * 405
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "잘못된 HTTP method 요청입니다."),

    /**
     * 409
     */
    CONFLICT(HttpStatus.CONFLICT, 40900, "데이터 충돌이 발생했습니다. "),
    INTEGRITY_CONFLICT(HttpStatus.CONFLICT, 40901, "데이터 무결성 위반입니다."),
    CONFLICT_POSTLIKE(HttpStatus.CONFLICT, 40902, "이미 존재하는 게시글 공감입니다."),

    /**
     * 422
     */
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, 42200, "처리할 수 없는 요청입니다."),

    /**
     * 500
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류가 발생했습니다. "),
    INTERNAL_SERVER_ERROR_BREED_ID(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버에 유효하지 않은 breedId가 존재합니다. "),
    INTERNAL_SERVER_ERROR_PET_ID_FOR_MEMBER(HttpStatus.INTERNAL_SERVER_ERROR, 50002, "서버에 유효하지 않은 petId가 존재합니다. "),
    INTERNAL_SERVER_ERROR_PET_AGE(HttpStatus.INTERNAL_SERVER_ERROR, 50003, "서버에 유효하지 않은 petAge가 존재합니다. "),
    /**
     * 503
     */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 50300, "현재 서비스를 사용할 수 없습니다. ");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
