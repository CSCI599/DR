package com.csci599.project;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.bcel.generic.InstructionHandle;

public class Nodes {
	public int nodeNumber;
	public InstructionHandle nodeName;
	public ArrayList<Nodes> parents;
	public ArrayList<Nodes> children;
	public ArrayList<Nodes> dominators;
	public ArrayList<Nodes> post_dominators;

	public String name;

	// used to compute reaching def. info
	public TreeSet<Definition> in;
	public TreeSet<Definition> out;
	public TreeSet<Definition> gen;
	public TreeSet<Definition> kill;

	public boolean visited;

	Nodes(String name) {
		this.name = name;
		this.visited = false;
	}

	Nodes(InstructionHandle nodeName) {
		this.visited = false;
		this.name = "";
		this.nodeName = nodeName;
		this.parents = new ArrayList<Nodes>();
		this.children = new ArrayList<Nodes>();
		this.dominators = new ArrayList<Nodes>();
		this.post_dominators = new ArrayList<Nodes>();
		this.in = new TreeSet<Definition>();
		this.out = new TreeSet<Definition>();
		this.gen = new TreeSet<Definition>();
		this.kill = new TreeSet<Definition>();

	}
}
