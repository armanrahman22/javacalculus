package javacalculus.core;

import javacalculus.core.CalcParser;
import javacalculus.core.CALC;
import javacalculus.exception.CalcSyntaxException;
import javacalculus.struct.*;

/**
 * The main programmable interface of the library. This is the class that the
 * user should import and call methods from.
 *
 * @author Duyun Chen <A
 * HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>, Seth Shannin
 * <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *
 *
 */
public final class CalculusEngine {

    private String result = "No commands executed.";
    private long currentTime, deltaTime;

    /**
     * Constructor
     */
    public CalculusEngine() {
    }

    /**
     * This is the most important function in CalculusEngine. The user specifies
     * an input that is sent through the algorithm, producing a mathematical
     * output that satisfies the grammar used in the command.
     *
     * @param command
     * @return The result obtained by parsing and evaluating <b>command</b>.
     */
    public String execute(String command) {
        currentTime = System.nanoTime();
        CALC.operator_notation = false;
        CALC.max_recursion_depth = 3;
        CALC.full_integrate_mode = true;
        CalcObject parsed = null;
        CalcObject processed = null;
        CalcParser parser = new CalcParser();

        try {
            currentTime = System.nanoTime();
            parsed = parser.parse(command);
        } catch (CalcSyntaxException e) {
            e.printStackTrace();
        }

        result = "Input: " + command + "\n";
        result += "Evaluation Queue: " + parsed + "\n";
        result += "Processed Queue: " + (processed = CALC.SYM_EVAL(parsed)) + "\n";
        CALC.toggleOperatorNotation();
        result += "Output: " + processed + "\n";
        deltaTime = System.nanoTime() - currentTime;
        result += "Time used: " + deltaTime + " nanoseconds\n";

        return result;
    }

    /**
     *
     * @return the previous result obtained by <b>execute</b>
     */
    public String getResult() {
        return result;
    }

    /**
     * Set the floating point precision to <b>precision</b> digits
     *
     * @param precision
     */
    public void setPrecision(int precision) {
        CALC.setMathContext(precision);
    }
}