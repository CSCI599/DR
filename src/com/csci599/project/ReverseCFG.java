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

	public CFG_Graph makeReverseCFGBlocks(CFG_Graph cfg) {

		for(BasicBlock node: cfg.reverseBasicBlocks){
			
			/*BasicBlock bb = new BasicBlock();
			
			bb.blockNumber = node.blockNumber;
			bb.start = node.start;
			bb.end = node.end;
			bb.first = node.first;
			bb.last = node.last;
			bb.controlDependencyList = (ArrayList<BasicBlock>) node.controlDependencyList.clone();
			bb.visited = node.visited;
			bb.dominators = node.dominators;
			bb.post_dominators = node.post_dominators;
			*/
			
			ArrayList<BasicBlock> parents = new ArrayList<BasicBlock>();
			parents.addAll(node.parents);
			ArrayList<BasicBlock> children  = new ArrayList<BasicBlock>();
			children.addAll(node.children);
			//node.parents.clear();
			//node.children.clear();
			node.parents.clear();
			node.children.clear();
			node.visited = false;
			node.parents.addAll(children);
			node.children.addAll(parents);
			
			//cfg.reverseBasicBlocks.add(bb);
		}
		return cfg;
	}

}
