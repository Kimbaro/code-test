package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 문제: 에러 유형별 다양한 분기를 고려하지 않았습니다 (에러 처리/설계)
 * 원인:
 * - RuntimeException이 모든 예외를 감당하고 있습니다.
 * - @ResponseStatus와 ResponseEntity.status() 중복 사용합니다.
 * - 에러 응답 본문이 비어있어 클라이언트에게 전달가능한 정보가 한정적입니다.
 * - 구체적인 예외 타입별 처리 부재로 적절한 HTTP 상태 코드 반환이 불가합니다.
 * 개선안:
 * - 대안: HTTP Status를 나열하고 발생 가능한 Exception과 매칭하여 정리합니다.
 *   - 명확하지 않는 경우 Exception을 RuntimeException으로 처리하여 관리자가 쉽게 볼 수 있도록 합니다.
 *   - 응답 케이스별 DTO를 구성하고 HTTP Status별 결과를 클라이언트에게 제공합니다(RESTful 원칙).
 * - 선택 근거: HTTP 상태 코드를 의미에 맞게 반환하여 클라이언트가 적절히 처리 가능합니다.
 *   - 다양한 예외별 다른 분기를 정의하여 운영 환경에서 발생 가능한 에러에 대응해야 합니다.
 * - 트레이드오프: 많은 예외별 분기 로직을 정의함에 따라 코드가 길어지고 복잡해질 수 있지만
 *   운영 시 빠른 대응과 개선을 위한 리스크를 감안하면 장기적 이점이 큽니다.
 * 검증:
 * - 각 예외 타입별로 적절한 HTTP 상태 코드 반환을 확인합니다 (4xx: 클라이언트 오류, 5xx: 서버 오류).
 * - 에러 응답 본문에 메시지 포함 여부를 확인합니다.
 * - 로그에 에러 정보 기록 여부를 확인합니다.
 */
@Slf4j
@ControllerAdvice(value = {"com.wjc.codetest.product.controller"})
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> runTimeException(Exception e) {
        log.error("status :: {}, errorType :: {}, errorCause :: {}",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "runtimeException",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

//    아래의 샘플코드를 확장합니다.
//    @ResponseBody
//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<Map<String, Object>> nullPointerException(NullPointerException e) {
//        log.error("status :: {}, errorType :: {}, errorCause :: {}",
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                "nullPointerException",
//                e.getMessage()
//        );
//
//        Object errorResponse;
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }
}
