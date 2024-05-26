package antigravity.exception;

import antigravity.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends  RuntimeException {
        private final ErrorCode errorCode;
}
