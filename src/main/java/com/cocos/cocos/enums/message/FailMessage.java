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
    BAD_REQUEST_INVALID_SORT_CRITERIA(HttpStatus.BAD_REQUEST, 40005, "유효하지 않은 정렬 기준입니다."),
    BAD_REQUEST_INVALID_TOKEN(HttpStatus.BAD_REQUEST, 40006, "유효하지 않은 인증정보입니다."),
    BAD_REQUEST_INVALID_LOCATION_TYPE(HttpStatus.BAD_REQUEST, 40007, "유효하지 않은 위치 타입입니다."),
    BAD_REQUEST_INVALID_HOSPITAL_ID(HttpStatus.BAD_REQUEST, 40008, "유효하지 않은 병원 아이디입니다."),
    BAD_REQUEST_INVALID_BREED_ID(HttpStatus.BAD_REQUEST, 40009, "유효하지 않은 품종 아이디입니다."),
    BAD_REQUEST_INVALID_DISEASE_ID(HttpStatus.BAD_REQUEST, 40010, "유효하지 않은 질병 아이디입니다."),
    BAD_REQUEST_INVALID_MEMBER_QUERY(HttpStatus.BAD_REQUEST, 40011, "회원 식별자 (id 또는 nickname)를 제공해야 합니다."),
    BAD_REQUEST_MISSING_BODY(HttpStatus.BAD_REQUEST, 40012, "필수 body가 누락되었습니다."),
    BAD_REQUEST_INVALID_REVIEW_ID(HttpStatus.BAD_REQUEST, 40013, "유효하지 않은 리뷰 아이디입니다."),
    /**
     * 401
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증이 필요합니다. "),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, 40101, "인증되지 않은 사용자 입니다."),
    UNAUTHORIZED_EXPIRATION_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, 40102, "기간이 만료된 토큰입니다."),
    UNAUTHORIZED_NOT_WRITER(HttpStatus.UNAUTHORIZED, 40103, "리뷰를 작성한 사용자가 아닙니다."),


    /**
     * 403
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "권한이 없습니다. "),
    FORBIDDEN_COMMENT_DELETE(HttpStatus.FORBIDDEN, 40301, "댓글을 삭제할 권한이 없습니다. "),
    FORBIDDEN_PET_UPDATE(HttpStatus.FORBIDDEN, 40302, "애완동물 정보를 수정할 권리가 없습니다. "),
    /**
     * 404
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "리소스를 찾을 수 없습니다. "),
    NOT_FOUND_ENTITY(HttpStatus.NOT_FOUND, 40401, "대상을 찾을 수 없습니다. "),
    NOT_FOUND_API(HttpStatus.NOT_FOUND, 40402, "잘못된 API입니다. "),
    NOT_FOUND_POSTLIKE(HttpStatus.NOT_FOUND, 40403, "게시물 공감을 찾을 수 없습니다."),
    NOT_FOUND_BODY(HttpStatus.NOT_FOUND, 40404, "신체 부위를 찾을 수 없습니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, 40405, "게시글을 찾을 수 없습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, 40406, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_BREED(HttpStatus.NOT_FOUND, 40407, "품종을 찾을 수 없습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, 40408, "카테고리를 찾을 수 없습니다."),
    NOT_FOUND_ANIMAL(HttpStatus.NOT_FOUND, 40409, "동물을 찾을 수 없습니다."),
    NOT_FOUND_DISEASE(HttpStatus.NOT_FOUND, 40410, "질병을 찾을 수 없습니다."),
    NOT_FOUND_SYMPTOM(HttpStatus.NOT_FOUND, 40411, "증상을 찾을 수 없습니다."),
    NOT_FOUND_POSTTAG(HttpStatus.NOT_FOUND, 40412, "태그를 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, 40413, "댓글을 찾을 수 없습니다."),
    NOT_FOUND_SUB_COMMENT(HttpStatus.NOT_FOUND, 40414, "대댓글을 찾을 수 없습니다."),
    NOT_FOUND_PET(HttpStatus.NOT_FOUND, 40415, "애완동물을 찾을 수 없습니다. "),
    NOT_FOUND_MENTIONED_MEMBER(HttpStatus.NOT_FOUND, 40416, "언급된 사용자를 찾을 수 없습니다. "),
    NOT_FOUND_TOWN(HttpStatus.NOT_FOUND, 40417, "동을 찾을 수 없습니다. "),
    NOT_FOUND_MEMBER_ADDRESS(HttpStatus.NOT_FOUND, 40418, "사용자 위치를 찾을 수 없습니다."),
    NOT_FOUND_CITY(HttpStatus.NOT_FOUND, 40419, "시/도를 찾을 수 없습니다."),
    NOT_FOUND_DISTRICT(HttpStatus.NOT_FOUND, 40420, "시/군/구를 찾을 수 없습니다."),
    NOT_FOUND_HOSPITAL(HttpStatus.NOT_FOUND, 40421, "병원을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, 40422, "리뷰를 찾을 수 없습니다."),
    NOT_FOUND_SUMMARY_OPTION(HttpStatus.NOT_FOUND, 40423, "리뷰 요약 옵션을 찾을 수 없습니다."),

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
    CONFLICT_PET(HttpStatus.CONFLICT, 40903, "이미 사용자의 애완동물이 존재합니다."),
    CONFLICT_MEMBER_HOSPITAL(HttpStatus.CONFLICT, 40904, "이미 사용자의 즐겨찾는 병원이 존재합니다."),

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
    INTERNAL_SERVER_ERROR_UNSUPPORTED_OPERATION(HttpStatus.INTERNAL_SERVER_ERROR, 50004, "유틸 클래스입니다. "),
    INTERNAL_SERVER_ERROR_KAKAO_UNLINK(HttpStatus.INTERNAL_SERVER_ERROR, 50005, "카카오 회원탈퇴에 실패했습니다."),

    /**
     * 503
     */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 50300, "현재 서비스를 사용할 수 없습니다. ");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
