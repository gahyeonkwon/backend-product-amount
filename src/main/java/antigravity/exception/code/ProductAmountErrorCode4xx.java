package antigravity.exception.code;

import org.springframework.http.HttpStatus;

public enum ProductAmountErrorCode4xx implements ErrorCode{

    INVALID_REQUEST_PRODUCT(HttpStatus.BAD_REQUEST, "프로덕트 아이디가 누락 되었습니다"),
    INVALID_REQUEST_PROMOTION(HttpStatus.BAD_REQUEST, "프로모션 코드가 누락 되었습니다");

    private ProductAmountErrorCode4xx(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
