package bitcamp.carrot_thunder.exception;

public class ExampleException extends BaseException {

    public static final BaseException EXCEPTION = new ExampleException();

    private ExampleException() {
        super(GlobalException.EXAMPLE_ERROR);
    }

}