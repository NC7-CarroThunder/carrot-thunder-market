package bitcamp.carrot_thunder.post.exception;

import bitcamp.carrot_thunder.exception.BaseException;

import static bitcamp.carrot_thunder.exception.GlobalException.NOT_FOUND_POST_ERROR;

public class NotFoundPostException extends BaseException {
    public static final BaseException EXCEPTION = new NotFoundPostException();

    public NotFoundPostException() {
        super(NOT_FOUND_POST_ERROR);
    }
}
