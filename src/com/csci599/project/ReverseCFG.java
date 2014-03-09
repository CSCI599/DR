package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class ReverseCFG {

	public CFG_Graph makeReverseCFG(CFG_Graph cfg) {

		for(Nodes node: cfg.nodes){
			
			ArrayList<Nodes> parents = new ArrayList<Nodes>();
			parents.addAll(node.parents);
			ArrayList<Nodes> children  = new ArrayList<Nodes>();
			children.addAll(node.children);
			node.parents.clear();
			node.children.clear();
			node.parents.addAll(children);
			node.children.addAll(parents);
		}
		return cfg;
	}
}
