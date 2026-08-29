package ruiseki.integrateddynamics.api.evaluate;

/**
 * Exception to indicate a failed evaluation.
 *
 * @author rubensworks
 */
public class EvaluationException extends Exception {

    private boolean retryEvaluation;

    public EvaluationException(String msg) {
        super(msg);
        this.retryEvaluation = false;
    }

    public void setRetryEvaluation(boolean retryEvaluation) {
        this.retryEvaluation = retryEvaluation;
    }

    public boolean isRetryEvaluation() {
        return retryEvaluation;
    }
}
