package rs.ac.bg.etf.pp1.exceptions;

public class MJCodeGeneratorException extends MJCompilerException {

    public MJCodeGeneratorException() {
        super();
    }

    public MJCodeGeneratorException(String message) {
        super(message);
    }

    public MJCodeGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MJCodeGeneratorException(Throwable cause) {
        super(cause);
    }

    protected MJCodeGeneratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}