/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javacalculus.evaluator;

import java.util.ArrayList;
import javacalculus.core.CALC;
import javacalculus.struct.CalcError;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

/**
 *
 * @author Seva
 */
public class IntegrationThread implements Runnable {

    private CalcObject[] udvPair;
    private CalcSymbol var;
    private CalcINTBYPARTS overlord;

    public IntegrationThread(CalcObject[] pair, CalcSymbol var, CalcINTBYPARTS overlord) {
        udvPair = pair;
        this.var = var;
        this.overlord = overlord;
        //if (overlord.isMaster()) {
        //    System.out.println("I have the honor of being a master thread " + toString());
        //}
    }

    @Override
    public String toString() {
        return "I am " + Thread.currentThread().getName() + " and my overlord is the overlord master: " + (overlord.isMaster()) + "";
    }

    @Override
    public void run() {
        //toString();
        if (overlord.keepGoing()) {
            try {
                //Thread.sleep(10);
                CalcObject u = udvPair[0];
                CalcObject dv = udvPair[1];
                //System.out.println("I am " + Thread.currentThread().getName() + " and my overlord is the overlord master: " + overlord.overlord == null);
                //Thread.sleep(10);
                //System.out.println("hi");
                CalcINT vIntegrator = new CalcINT();
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
                CalcINT vduIntegrator = new CalcINT();
                vduIntegrator.intByPartsCarryOver = overlord;
                //System.out.println(toString());
                CalcObject answer = CALC.SYM_EVAL(CALC.ADD.createFunction(uTimesV, CALC.MULTIPLY.createFunction(vduIntegrator.integrate(vTimesDu, var), CALC.NEG_ONE)));
                if (!containsError(answer)) {
                    overlord.answer = answer;
                }
                //System.out.println("I am " + Thread.currentThread().getName() + " and i found an answer");
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        } else {
            //System.out.println("I gave up; " + toString());
        }
    }

    public boolean containsError(CalcObject test) {
        return test.toString().contains("Error");
    }
}
