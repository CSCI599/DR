package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class TestCaseToEdges {
	int testCaseNumber;
	ArrayList<InstructionHandle> edge;

	public TestCaseToEdges() {
		testCaseNumber = -1;
		edge = new ArrayList<InstructionHandle>();
	}
}
