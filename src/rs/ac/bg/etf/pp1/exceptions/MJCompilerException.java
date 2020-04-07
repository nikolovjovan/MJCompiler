package rs.ac.bg.etf.pp1.exceptions;

public class MJCompilerException extends Exception {

    public MJCompilerException() {
        super();
    }

    public MJCompilerException(String message) {
        super(message);
    }

    public MJCompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MJCompilerException(Throwable cause) {
        super(cause);
    }

    protected MJCompilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}