package com.csci599.project;

import org.apache.bcel.generic.InstructionHandle;

public class DependencyInformation {
	public InstructionHandle dependencyNode;
	public boolean true_false; // true means instruction at dependency node has
								// to be true
	public VariableValues varVal;

	public DependencyInformation() {
		// TODO Auto-generated constructor stub
	}
}
