package javacalculus.struct;

import java.math.BigInteger;

import javacalculus.core.CALC;
import javacalculus.exception.CalcArithmeticException;
import javacalculus.exception.CalcUnsupportedException;
import java.io.Serializable;

/**
 * Hierarchially encapsulates the java integer. BigInteger is used as the medium
 * because of the lack of size restrictions.
 *
 * @author Duyun Chen <A
 * HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>, Seth Shannin
 * <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *
 *
 */
public class CalcError implements CalcObject, Serializable{

    public CalcError() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CalcError) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isNumber() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CalcSymbol getHeader() {
        return CALC.ERROR;
    }

    @Override
    public CalcObject evaluate() {
        return this;
    }

    @Override
    public String toString() {
        return "CALC ERROR";
    }

    @Override
    public int compareTo(CalcObject obj) {
        return -1;
    }

    @Override
    public int getHierarchy() {
        return CalcObject.SYMBOL;
    }

    @Override
    public int getPrecedence() {
        return -1;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
