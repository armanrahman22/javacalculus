/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

/**
 *
 * @author Seva
 */
public class IntegrationThread extends Thread {

    public CalcObject answer = null;
    private CalcObject[] udvPair;
    private CalcSymbol var;
    private CalcINT overlord;

    public IntegrationThread(CalcObject[] pair, CalcSymbol var, CalcINT overlord) {
        udvPair = pair;
        this.var = var;
        this.overlord = overlord;
    }

    @Override
    public void run() {
        while (overlord.intByPartsThreadsAreAlive) {
            try {
                Thread.sleep(1);
                CalcObject u = udvPair[0];
                CalcObject dv = udvPair[1];
                CalcObject v = CALC.SYM_EVAL(CALC.INT.createFunction(dv, var));
                u = CALC.SYM_EVAL(u);
                v = CALC.SYM_EVAL(v);
                //System.out.println("This is our u: " + u);
                //System.out.println("This is our v: " + v);
                //we should have a non null u and dv here
                CalcObject du = CALC.SYM_EVAL(CALC.DIFF.createFunction(u, var));
                //System.out.println("This is our du: " + du);
                CalcObject uTimesV = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(u, v));
                CalcObject vTimesDu = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(v, du));
                answer = CALC.SYM_EVAL(CALC.ADD.createFunction(uTimesV, CALC.MULTIPLY.createFunction(CALC.INT.createFunction(vTimesDu, var), CALC.NEG_ONE)));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}

