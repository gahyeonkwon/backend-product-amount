package antigravity.exception.code;

import org.springframework.http.HttpStatus;

public enum ProductAmountErrorCode2xx implements ErrorCode{

    SUCCESS(HttpStatus.OK, "정상 호출 되었습니다.");

    private ProductAmountErrorCode2xx(final HttpStatus httpStatus, final String message) {
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
