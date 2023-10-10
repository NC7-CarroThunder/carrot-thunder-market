package bitcamp.carrot_thunder.exception;

public class NotHaveAuthorityException extends BaseException{
    public static final BaseException EXCEPTION = new NotHaveAuthorityException();
    private NotHaveAuthorityException() { super(GlobalException.NOT_HAVE_AUTHORITY);}
}
