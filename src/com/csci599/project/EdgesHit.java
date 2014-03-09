package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class EdgesHit {
	public ArrayList<Integer> testCaseNumber;
	public boolean traversed;
	public ArrayList<InstructionHandle> edge;

	public EdgesHit() {
		testCaseNumber = new ArrayList<Integer>();
	}
}
