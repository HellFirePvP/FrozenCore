package hellfirepvp.frozencore.core;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: ASMTransformationException
 * Created by HellFirePvP
 * Date: 14.04.2017 / 20:16
 */
public class ASMTransformationException extends RuntimeException {

    public ASMTransformationException() {}

    public ASMTransformationException(String message) {
        super(message);
    }

    public ASMTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ASMTransformationException(Throwable cause) {
        super(cause);
    }

    public ASMTransformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
