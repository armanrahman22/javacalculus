package javacalculus.struct;

import java.math.BigDecimal;
import java.math.BigInteger;
import javacalculus.core.CALC;
import java.io.Serializable;

/**
 * 
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcFraction implements CalcObject, Serializable {

	private BigInteger numerator;
	private BigInteger denominator;
	
	public CalcFraction(BigInteger nume, BigInteger deno) {
		if (deno.compareTo(CALC.ZERO.bigIntegerValue()) == 0) { //division by zero => throw exception
			throw new ArithmeticException();
		}
		
		//if denominator is negative, make the numerator negative instead.
		//Makes life easier when simplifying.
		if (deno.compareTo(CALC.ZERO.bigIntegerValue()) == -1) { 
			nume = nume.negate();
			deno = deno.negate();
		}
		
		BigInteger commonfactor = deno.gcd(nume);
		
		if (commonfactor.compareTo(CALC.ONE.bigIntegerValue()) != 0) {
			nume = nume.divide(commonfactor);
			deno = deno.divide(commonfactor);
		}
		
		numerator = nume;
		denominator = deno;		
	}
	
	public CalcFraction(CalcInteger a, CalcInteger b) {
		this(a.bigIntegerValue(), b.bigIntegerValue());
	}
	
	public CalcObject add(CalcFraction input) {
		
		BigInteger  nume = (numerator.multiply(input.getDenominator())).add(denominator.multiply(input.getNumerator()));
		BigInteger  deno = denominator.multiply(input.getDenominator());

		if (deno.compareTo(CALC.ZERO.bigIntegerValue()) < 0) {
			deno = deno.negate();
			nume = nume.negate();
		}
		
		BigInteger  commonfactor = nume.gcd(deno);

		return new CalcFraction(nume.divide(commonfactor), deno.divide(commonfactor));
	}
	
	public CalcObject multiply(CalcInteger input) {
		BigInteger nume = numerator.multiply(input.bigIntegerValue());
		BigInteger commonfactor = nume.gcd(denominator);

		return new CalcFraction(nume.divide(commonfactor), denominator.divide(commonfactor));		
	}
	
	public CalcObject multiply(CalcFraction input) {
		BigInteger nume = numerator.multiply(input.getNumerator());
		BigInteger deno = denominator.multiply(input.getDenominator());
		
		BigInteger commonfactor = nume.gcd(deno);
		
		return new CalcFraction(nume.divide(commonfactor), deno.divide(commonfactor));
	}
	
	/**
	 * @param n
	 * @return this fraction to the n<sup>th</sup> power
	 */
	public CalcObject power(int n) {
		if (n < 0) {
			n *= -1;
			return new CalcFraction(denominator.pow(n), numerator.pow(n));
		}
		
		return new CalcFraction(numerator.pow(n), denominator.pow(n));
	}
	
	public BigInteger getNumerator() {
		return numerator;
	}
	
	public BigInteger getDenominator() {
		return denominator;
	}
	
	@Override
	public boolean isNumber() {
		return true;
	}
	
	public boolean isNegative() {
		return !((numerator.compareTo(BigInteger.ZERO) < 0 
					&& denominator.compareTo(BigInteger.ZERO) < 0)
				|| (numerator.compareTo(BigInteger.ZERO) > 0 
					&& denominator.compareTo(BigInteger.ZERO) > 0));
	}
	
	public void negate() {
		numerator.negate();
	}

	@Override
	public CalcSymbol getHeader() {
		return CALC.FRACTION;
	}
	
	@Override
	public CalcObject evaluate() throws Exception {
		return new CalcDouble((new BigDecimal(numerator)).divide(new BigDecimal(denominator), CALC.mathcontext));
	}

	public String toString() {
		StringBuffer returnVal = new StringBuffer();
		if (denominator.compareTo(CALC.ONE.bigIntegerValue()) == 0) {
			returnVal.append(numerator.toString()); //if denominator is 1, then just append numerator
		}
		else {
			returnVal.append(numerator.toString()); //else append the entire fraction expression
			returnVal.append('/');
			returnVal.append(denominator.toString());
		}
		return returnVal.toString();
	}

	@Override
	public int compareTo(CalcObject obj) {
		if (getHierarchy() > obj.getHierarchy()) {
			return 1;
		}
		else if (getHierarchy() < obj.getHierarchy()) {
			return -1;
		}
		return 0;
	}

	@Override
	public int getHierarchy() {
		return CalcObject.FRACTION;
	}

	@Override
	public int getPrecedence() {
		if (numerator.compareTo(BigInteger.ZERO) < 0) {
			return 100;
		}
		else return 9999999;
	}
}
