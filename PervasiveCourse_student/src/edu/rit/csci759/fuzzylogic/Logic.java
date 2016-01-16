package edu.rit.csci759.fuzzylogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class Logic {
	
	//add function
	//edit rule function
	//remove rule function
	
	public static void main(String[] args) throws Exception {
		
		String filename = "FuzzyLogic/Blinds.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Set inputs
		fb.setVariable("temperature", 24);
		fb.setVariable("ambient", 25);

		// Evaluate
		fb.evaluate();

		// Show output variable's chart
		fb.getVariable("blind").defuzzify();

		// Print ruleSet
		System.out.println(fb);
		System.out.println("Blind Position: " + fb.getVariable("blind").getValue());

	}
}
