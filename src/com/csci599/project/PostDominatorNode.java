package com.csci599.project;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.bcel.generic.InstructionHandle;

public class PostDominatorNode {
	public int nodeNumber;
	public InstructionHandle nodeName;
	public String name;
	public PostDominatorNode parent;
	public ArrayList<PostDominatorNode> children;
	public ArrayList<Nodes> post_dominators;
	public ArrayList<PostDominatorNode> controlDependencyList;

	// used to compute reaching def. info
	public TreeSet<Definition> in;
	public TreeSet<Definition> out;
	public TreeSet<Definition> gen;
	public TreeSet<Definition> kill;

	public PostDominatorNode(Nodes node) {
		name = "";
		parent = new PostDominatorNode();
		children = new ArrayList<PostDominatorNode>();
		post_dominators = node.post_dominators;
		controlDependencyList = new ArrayList<PostDominatorNode>();
		
		in = node.in;
		out = node.out;
		gen = node.gen;
		kill = node.kill;
		
		nodeName = node.nodeName;
		if (this.nodeName != null) {
			name = nodeName.getInstruction().getName();
		} else {
			this.name = node.name;
		}
	}

	public PostDominatorNode() {

		this.in = new TreeSet<Definition>();
		this.out = new TreeSet<Definition>();
		this.gen = new TreeSet<Definition>();
		this.kill = new TreeSet<Definition>();
	}
}
