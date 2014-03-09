package com.csci599.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.generic.InstructionHandle;

public class MainClass {

	public static void main(String[] args) throws IOException {

		if (args.length < 4) {
			System.out.println("Expected 4 arguments");
			System.out.println("1. Line number to run");
			System.out.println("2. Path to class folder");
			System.out.println("3. Name of class file (without .class extension");
			System.out.println("4. Name of the method");
			System.exit(0);
		}
		CFG cfg = new CFG();
		int lineNumber = Integer.parseInt(args[0]);

		String classPath = args[1];
		String className = args[2];
		String methodName = args[3];

		ArrayList<CFG_Graph> graphs = cfg.cfgMaker(classPath, className);
		//System.out.println("CFG Size: " + graphs.size());
		int i = 1;
		CFG_Graph mainGraph = new CFG_Graph();
		for (CFG_Graph graph : graphs) {
			if (graph.method.getName().equalsIgnoreCase(methodName)) {
				mainGraph = graph;
			}
			
			System.out.println("Graph " + i + " byte code mapping length: "
					+ graph.byteCode_to_sourceCode_mapping.size());
			i++;
			for (ArrayList<InstructionHandle> edge : graph.edges) {
				if (edge.get(0) != null) {
					System.out.print(edge.get(0).getPosition()
							+ " ("
							+ graph.byteCode_to_sourceCode_mapping
									.get((int) edge.get(0).getPosition())
							+ ") ----> ");
				}
				if (edge.get(1) != null) {
					System.out.print(edge.get(1).getPosition()
							+ " ("
							+ graph.byteCode_to_sourceCode_mapping
									.get((int) edge.get(1).getPosition())
							+ ")\n");
				} else {
					System.out.println("EXIT\n");
				}
			}

			System.out.println("Method: " + graph.method.getName());
			System.out
					.println("Code: " + graph.method.getCode().toString(true));
			
		}

		int node = -1;
		for (Integer key : mainGraph.byteCode_to_sourceCode_mapping.keySet()) {
			if (mainGraph.byteCode_to_sourceCode_mapping.get(key) == lineNumber) {
				node = key;
			}
		}
		if (node == -1) {
			System.out.println("Cannot find the line specified");
			System.exit(0);
		}

		System.out.println();
		System.out.println("Total Nodes: " + mainGraph.nodes.size());
		ArrayList<InstructionHandle> dependencyList = cfg
				.getDependencyInformation(mainGraph, node);
		System.out.println(node + "(line number " + lineNumber + ")"
				+ " can be reached by "
				+ mainGraph.reachabilityList.get(node).size() + " other nodes");
		System.out.println("Node at position " + node
				+ " depends on the following conditions ");
		System.out.println("Total dependency conditions: "
				+ dependencyList.size());
		for (InstructionHandle dependency : dependencyList) {
			System.out.print("\n" + dependency);
		}

		System.out.println();
		System.out.println();
		System.out.println("Conditions: ");
		ArrayList<DependencyInformation> depList = cfg.dependencyAdapter(
				dependencyList, node, mainGraph.localVariableTable,
				mainGraph.nodes, mainGraph.constantPool);

		for (DependencyInformation dep : depList) {
			System.out.println();
			System.out.println("Instruction: " + dep.dependencyNode);
			System.out.println("Must evaluate to : " + dep.true_false);
			System.out.println("Depends on variable: "
					+ dep.varVal.variableName);
			System.out.println("For instruction to be TRUE, "
					+ dep.varVal.variableName + " must be : "
					+ dep.varVal.value);
			System.out.println("Source code line number: "
					+ mainGraph.byteCode_to_sourceCode_mapping
							.get(dep.dependencyNode.getPosition()));
			int loc = dep.dependencyNode.getPosition();

			cfg.generateReachingDef(mainGraph.localVariableTable,
					mainGraph.nodes, mainGraph.edges, mainGraph.constantPool);

			Nodes node1 = mainGraph.nodesMap.get(loc);
			boolean isOutisdeDefn = true;
			for (Definition def : node1.out) {
				if (def.getVarName().equalsIgnoreCase(dep.varVal.variableName)
						&& (!def.isOutsideDef())) {
					isOutisdeDefn = false;
					System.out
							.println("The variable "
									+ dep.varVal.variableName
									+ " does not come from outside. It is redefined at position: "
									+ def.getLine());
				}
			}
			System.out.println();
		}

	}

}
