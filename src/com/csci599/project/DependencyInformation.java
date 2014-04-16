package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class DependencyInformation {
	public InstructionHandle dependencyNode;
	public boolean true_false; // true means instruction at dependency node has
								// to be true
	public ArrayList<VariableValues> variables;
	public String symbol;

	public DependencyInformation() {
		// TODO Auto-generated constructor stub
		symbol = "";
	}
}
