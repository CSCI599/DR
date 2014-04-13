package com.csci599.project;

import java.util.ArrayList;

public class BasicBlock {
	public int blockNumber;
	public int start;
	public int end;
	public boolean first;
	public boolean last;
	public ArrayList<Integer> parents;
	public ArrayList<Integer> children;

	public BasicBlock() {
		// TODO Auto-generated constructor stub
		blockNumber = -1;
		start = -1;
		end = -1;
		first = false;
		last = false;
		parents = new ArrayList<Integer>();
		children = new ArrayList<Integer>();
	}
}
