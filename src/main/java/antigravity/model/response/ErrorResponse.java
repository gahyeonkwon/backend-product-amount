package antigravity.model.response;


import antigravity.exception.code.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.PrimitiveIterator;

@Data
@Builder
public class ErrorResponse {
    private HttpStatus status;
    private String code;
    private String message;
}
