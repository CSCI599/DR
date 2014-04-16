package com.csci599.project;

import java.util.ArrayList;

public class BasicBlock {
	public int blockNumber;
	public int start;
	public int end;
	public boolean first;
	public boolean last;
	public ArrayList<BasicBlock> parents;
	public ArrayList<BasicBlock> children;
	public ArrayList<BasicBlock> dominators;
	public ArrayList<BasicBlock> post_dominators;
	ArrayList<BasicBlock> controlDependencyList;
	boolean visited;

	public BasicBlock() {
		// TODO Auto-generated constructor stub
		visited = false;
		blockNumber = -1;
		start = -1;
		end = -1;
		first = false;
		last = false;
		parents = new ArrayList<BasicBlock>();
		children = new ArrayList<BasicBlock>();
		dominators = new ArrayList<BasicBlock>();
		post_dominators = new ArrayList<BasicBlock>();
		controlDependencyList = new ArrayList<BasicBlock>();

	}
}
