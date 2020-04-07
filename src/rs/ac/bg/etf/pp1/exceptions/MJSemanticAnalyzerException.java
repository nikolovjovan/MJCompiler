package rs.ac.bg.etf.pp1.exceptions;

public class MJSemanticAnalyzerException extends MJCompilerException {

    public MJSemanticAnalyzerException() {
        super();
    }

    public MJSemanticAnalyzerException(String message) {
        super(message);
    }

    public MJSemanticAnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MJSemanticAnalyzerException(Throwable cause) {
        super(cause);
    }

    protected MJSemanticAnalyzerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}