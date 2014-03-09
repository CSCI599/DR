package com.csci599.project;

import java.util.ArrayList;
import org.apache.bcel.generic.InstructionHandle;

public class Dominator_Tree {
	ArrayList<Nodes> nodes;
	ArrayList<ArrayList<InstructionHandle>> edges;

	public Dominator_Tree() {
		nodes = new ArrayList<Nodes>();
		edges = new ArrayList<ArrayList<InstructionHandle>>();
	}
}
