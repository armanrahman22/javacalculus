/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

/**
 *
 * @author Seva
 */
public final class IntegrationThread implements Runnable {

    private CalcObject[] udvPair;
    private CalcSymbol var;
    private CalcINTBYPARTS overlord;
    private CalcINTBYPARTS father;
    private int depth;

    public IntegrationThread(CalcObject[] pair, CalcSymbol var, CalcINTBYPARTS overlord, CalcINTBYPARTS father, int depth) {
        udvPair = pair;
        this.var = var;
        this.overlord = overlord;
        this.father = father;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "I am " + Thread.currentThread().getName() + " and my overlord is the overlord master: " + (overlord.isMaster()) + "";
    }

    @Override
    public void run() {
        if (depth < CALC.max_recursion_depth) {
            if (overlord.keepGoing()) {
                try {
                    CalcObject u = udvPair[0];
                    CalcObject dv = udvPair[1];
                    CalcINT vIntegrator = new CalcINT(depth + 1);
                    vIntegrator.intByPartsCarryOver = overlord;
                    CalcObject v = CALC.SYM_EVAL(vIntegrator.integrate(dv, var));
                    u = CALC.SYM_EVAL(u);
                    v = CALC.SYM_EVAL(v);
                    //System.out.println("This is our u: " + u);
                    //System.out.println("This is our v: " + v);
                    //we should have a non null u and dv here
                    CalcObject du = CALC.SYM_EVAL(CALC.DIFF.createFunction(u, var));
                    //System.out.println("This is our du: " + du);
                    CalcObject uTimesV = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(u, v));
                    CalcObject vTimesDu = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(v, du));
                    CalcINT vduIntegrator = new CalcINT(depth + 1);
                    vduIntegrator.intByPartsCarryOver = overlord;
                    //System.out.println(toString());
                    CalcObject answer = CALC.SYM_EVAL(CALC.ADD.createFunction(uTimesV, CALC.MULTIPLY.createFunction(vduIntegrator.integrate(vTimesDu, var), CALC.NEG_ONE)));
                    if (!containsError(answer)) {
                        if (overlord.answer == null) {
                            overlord.answer = answer;
                        }
                        father.waiter.countDown();
                    }
                    //System.out.println("I am " + Thread.currentThread().getName() + " and i found an answer");
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    //System.exit(1);
                }
            }
            if (overlord.answer == null) {
                overlord.answer = CALC.ERROR;
            }
            father.waiter.countDown();
            //overlord.waiter.countDown();
        } else {
            if (overlord.answer == null) {
                overlord.answer = CALC.ERROR;
            }
            father.waiter.countDown();
        }
    }

    public boolean containsError(CalcObject test) {
        return test == null || test.toString().contains(CALC.ERROR.toString());
    }
}
