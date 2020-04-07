package rs.ac.bg.etf.pp1.exceptions;

public class MJParserException extends MJCompilerException {

    public MJParserException() { super(); }

    public MJParserException(String message) {
        super(message);
    }

    public MJParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public MJParserException(Throwable cause) {
        super(cause);
    }

    protected MJParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}