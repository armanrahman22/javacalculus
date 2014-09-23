package javacalculus.struct;

import javacalculus.core.CALC;
import java.io.Serializable;

public class CalcSub implements CalcObject, Serializable {

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
        return CALC.USUB;
    }

    @Override
    public CalcObject evaluate() {
        return this;
    }

    @Override
    public String toString() {
        return "USub";
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
