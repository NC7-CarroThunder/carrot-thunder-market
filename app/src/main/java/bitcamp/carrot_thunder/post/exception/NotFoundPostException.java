package bitcamp.carrot_thunder.post.exception;

import static bitcamp.carrot_thunder.exception.GlobalException.NOT_FOUND_POST_ERROR;

import bitcamp.carrot_thunder.exception.BaseException;

public class NotFoundPostException extends BaseException {
    public static final BaseException EXCEPTION = new NotFoundPostException();

    public NotFoundPostException() {
        super(NOT_FOUND_POST_ERROR);
    }
}
