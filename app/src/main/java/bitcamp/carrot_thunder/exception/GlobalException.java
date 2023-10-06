package bitcamp.carrot_thunder.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum GlobalException implements BaseErrorCode{
    EXAMPLE_ERROR(BAD_REQUEST.value(), "에러 예시 입니다."),
    NOT_FOUND_POST_ERROR(NOT_FOUND.value(), "존재하지 않은 게시글입니다.");



    private final Integer statusCode;
    private final String reason;

    @Override
    public ErrorDetail getErrorDetail() {
        return ErrorDetail.of(statusCode, reason);
    }
}
