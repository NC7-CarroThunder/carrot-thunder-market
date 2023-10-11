package bitcamp.carrot_thunder.exception;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorDetail {
    private final Integer statusCode;
    private final String reason;

    public static ErrorDetail of(Integer statusCode, String reason) {
        return ErrorDetail.builder()
                .statusCode(statusCode)
                .reason(reason)
                .build();
    }
}
