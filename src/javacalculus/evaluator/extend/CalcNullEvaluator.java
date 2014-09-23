package javacalculus.evaluator.extend;

import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcObject;
import java.io.Serializable;

/**
 * Dummy symbolic evaluator (null evaluator).
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcNullEvaluator implements CalcFunctionEvaluator, Serializable {

	@Override
	public CalcObject evaluate(CalcFunction input) {
		return null;
	}

}
