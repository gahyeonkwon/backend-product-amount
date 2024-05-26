package antigravity.model.response;


import antigravity.exception.code.ErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private ErrorCode code;
}
