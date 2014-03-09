package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class Reachability {
	public ArrayList<InstructionHandle> reachability;

	public Reachability() {
		reachability = new ArrayList<InstructionHandle>();
	}
}
