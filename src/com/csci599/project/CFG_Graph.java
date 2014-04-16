package com.csci599.project;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;

class CFG_Graph {
	String servletName;
	Method method;
	ArrayList<Nodes> nodes;
	SortedMap<Integer, Nodes> nodesMap;//Map from node position to node

	ArrayList<ArrayList<InstructionHandle>> edges; //Inner list is of size 2. Check for nulls (EXIT) in TO part.
	SortedMap<Integer, ArrayList<Nodes> > edgesMap; //Map from node position to list of children.
	SortedMap<Integer, ArrayList<InstructionHandle>> reachabilityList; //Map from node to list of nodes that can reach it.
	ArrayList<Reachability> canReachList;
	ConstantPoolGen constantPool;

	SortedMap<Integer, Integer> byteCode_to_sourceCode_mapping;
	ArrayList<LineHitsForEachServlet> servletStats;
	ArrayList<EdgesHit> edgesTraversed;
	ArrayList<Integer> testCases;
	ArrayList<Integer> newTestCases;
	ArrayList<Nodes> dangerousEdges;
	ArrayList<TestCaseToEdges> testCaseToEdge;
	LineNumberTable lineNumberTable;
	LocalVariableTable localVariableTable;
	
	ArrayList<BasicBlock> basicBlocks;
	ArrayList<BasicBlock> reverseBasicBlocks;

	SortedMap<Integer, BasicBlock> basicBlockMap; //block start to block
	ArrayList<ArrayList<BasicBlock>> blockEdges;
	

	public CFG_Graph() {
		constantPool = null;
		servletName = "";
		reachabilityList = new TreeMap<Integer, ArrayList<InstructionHandle>>();
		canReachList = new ArrayList<Reachability>();

		method = new Method();
		nodes = new ArrayList<Nodes>();
		nodesMap = new TreeMap<Integer, Nodes>();
		basicBlockMap = new TreeMap<Integer, BasicBlock>();

		edges = new ArrayList<ArrayList<InstructionHandle>>();
		basicBlocks = new ArrayList<BasicBlock>();
		blockEdges = new ArrayList<ArrayList<BasicBlock>>();
		reverseBasicBlocks = new ArrayList<BasicBlock>();

		edgesMap = new TreeMap<Integer, ArrayList<Nodes>>();
		byteCode_to_sourceCode_mapping = new TreeMap<Integer, Integer>();
		servletStats = new ArrayList<LineHitsForEachServlet>();
		edgesTraversed = new ArrayList<EdgesHit>();
		testCases = new ArrayList<Integer>();
		newTestCases = new ArrayList<Integer>();
		dangerousEdges = new ArrayList<Nodes>();
		testCaseToEdge = new ArrayList<TestCaseToEdges>();
	}

}