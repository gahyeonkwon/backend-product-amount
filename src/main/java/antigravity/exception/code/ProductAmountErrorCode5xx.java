package antigravity.exception.code;

import org.springframework.http.HttpStatus;

public enum ProductAmountErrorCode5xx implements ErrorCode{

    FINAL_PRICE_IS_MINUS(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 최종 가격이 도출되었습니다."),

    INVALID_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "요청 데이터를 찾을 수 없습니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.INTERNAL_SERVER_ERROR, "제품 가격이 유효하지 않습니다."),
    CANNOT_FOUND_PRODUCT(HttpStatus.INTERNAL_SERVER_ERROR, "해당하는 상품이 없습니다."),
    CANNOT_FOUND_PROMOTION(HttpStatus.INTERNAL_SERVER_ERROR, "해당하는 프로모션이 없습니다.");

    private ProductAmountErrorCode5xx(final HttpStatus httpStatus, final String message) {
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
