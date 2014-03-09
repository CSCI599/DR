package com.csci599.testcase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;


public class TestCasesGenereator {

	public static void main(String[] args) {
		Random r = new Random();
		final int NUMOFARGS = 8;
		final int NUMOFTESTCASES = 1000;
		final String OUTPUTFILENAME = "JavaCmds";
		final String JAVACMD = "java -cp ./target:./lib/cobertura.jar com.csci599.project.Condition";
		
		StringBuilder testsuitecmds = new StringBuilder();
		
		ArrayList<TestCase> tests = new ArrayList<TestCase>(NUMOFTESTCASES);
		
		while (tests.size() < 1000) {
			
			int[] prm = new int[NUMOFARGS];
			for (int j = 0; j < NUMOFARGS; j++) {
				int number = r.nextInt(10);
				boolean isNegative = r.nextBoolean();
				if(isNegative)
					number = number * -1;
				prm[j] = number;
			}
			TestCase tc = new TestCase(prm);
			
			boolean alreadyAdded = false;
			for (TestCase testCase : tests) {
				if(testCase.equals(tc)){
					alreadyAdded = true;
					break;
				}
			}
			if(!alreadyAdded)
				tests.add(tc);
		}
		System.out.println("Done from generating test cases...");
		
		
		for (TestCase testCase : tests) {
			testsuitecmds.append(JAVACMD);
			for (int i = 0; i < testCase.parameters.length; i++) {
				testsuitecmds.append(" "+testCase.parameters[i]);
			}
			testsuitecmds.append("\n");
		}
		
		System.out.println("Saving to file...");
		
		OutputStream output;
		try {
			output = new FileOutputStream(OUTPUTFILENAME);
			output.write(testsuitecmds.toString().getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
	}

}
