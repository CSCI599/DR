package com.csci599.testcase;

public class TestCase{
	int[] parameters;
	public TestCase(int[] pars){
		parameters = pars;
	}
	public boolean equlas(TestCase tc){
		if(parameters.length != tc.parameters.length)
			return false;
		if(parameters == null && tc.parameters == null)
			return true;
		if(parameters == null || tc.parameters == null)
			return false;
		
		for (int i = 0; i < parameters.length; i++) {
			if(parameters[i] != tc.parameters[i])
				return false;
		}
		return true;
	}
}