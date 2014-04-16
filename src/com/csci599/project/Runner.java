package com.csci599.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class Runner {

	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			System.out.println("Expected 2 arguments");
			System.out.println("1. Path to class folder");
			System.out.println("2. Path to generate dotty file");
			System.exit(0);
		}
		CFG cfg = new CFG();

		String classPath = args[0];
		String outputDottyPath = args[1];

		ArrayList<CFG_Graph> graphs = cfg.cfgMaker(classPath, outputDottyPath);

		// System.out.println("CFG Size: " + graphs.size());
		int i = 1;
		String methodName = "Recommended_Show";
		CFG_Graph mainGraph = new CFG_Graph();
		for (CFG_Graph graph : graphs) {
			if (graph.method.getName().equalsIgnoreCase(methodName)) {
				mainGraph = graph;
			}

			// System.out.println("Graph " + i + " byte code mapping length: "
			// + graph.byteCode_to_sourceCode_mapping.size());
			i++;
			for (ArrayList<InstructionHandle> edge : graph.edges) {
				if (edge.get(0) != null) {
					// System.out.print(edge.get(0).getPosition()
					// + " ("
					// + graph.byteCode_to_sourceCode_mapping
					// .get((int) edge.get(0).getPosition())
					// + ") ----> ");
				}
				if (edge.get(1) != null) {
					// System.out.print(edge.get(1).getPosition()
					// + " ("
					// + graph.byteCode_to_sourceCode_mapping
					// .get((int) edge.get(1).getPosition())
					// + ")\n");
				} else {
					// System.out.println("EXIT\n");
				}
			}

			// System.out.println("Method: " + graph.method.getName());
			if (graph.method.getName().equalsIgnoreCase(methodName)) {
				System.out.println("Code: "
						+ graph.method.getCode().toString(true));
			}

		}

		System.out.println("Dotty File generated at: " + outputDottyPath);

		BlockMaker bMaker = new BlockMaker();
		// System.out.println("Total Nodes " + mainGraph.nodes.size());

		ArrayList<BasicBlock> basicBlocks = bMaker
				.makeBlockFromNodes(mainGraph.nodes);
		// System.out.println("Total Blocks: " + basicBlocks.size());
		SortedMap<Integer, BasicBlock> basicBlockMap = new TreeMap<Integer, BasicBlock>();

		for (BasicBlock block : basicBlocks) {
			basicBlockMap.put(block.start, block);
		}

		for (BasicBlock block : basicBlocks) {
			BasicBlock parent = block;
			if (mainGraph.nodesMap.get(block.end).nodeName.getInstruction() instanceof GotoInstruction) {
				BasicBlock child = basicBlockMap
						.get(((GotoInstruction) mainGraph.nodesMap
								.get(block.end).nodeName.getInstruction())
								.getTarget().getPosition());
				block.children.add(child);
				basicBlockMap.get(child.start).parents.add(parent);

			} else if (mainGraph.nodesMap.get(block.end).nodeName
					.getInstruction() instanceof IfInstruction) {

				BasicBlock child1 = basicBlockMap
						.get(((IfInstruction) mainGraph.nodesMap.get(block.end).nodeName
								.getInstruction()).getTarget().getPosition());
				BasicBlock child2 = basicBlockMap.get(mainGraph.nodesMap
						.get(block.end).nodeName.getNext().getPosition());
				block.children.add(child1);
				block.children.add(child2);

				basicBlockMap.get(child1.start).parents.add(parent);
				basicBlockMap.get(child2.start).parents.add(parent);

			} else {
				if (block.start != basicBlocks.get(basicBlocks.size() - 1).start) {
					// System.out.println("Block: " + block.start);
					BasicBlock child = basicBlockMap.get(mainGraph.nodesMap
							.get(block.end).nodeName.getNext().getPosition());
					block.children.add(child);
					basicBlockMap.get(child.start).parents.add(parent);
				}
			}
		}

		/*
		 * System.out.println("Basic Block Details"); for (BasicBlock block :
		 * basicBlocks) { System.out.println("Block Number: " +
		 * block.blockNumber); System.out.print("\nStart: " + block.start +
		 * " End: " + block.end + "\n"); System.out.println("Parents : "); for
		 * (BasicBlock par : block.parents) { System.out.print(par.start +
		 * ", "); } //System.out.println; System.out.println("Children : "); for
		 * (BasicBlock child : block.children) { System.out.print(child.start +
		 * ", "); } //System.out.println; }
		 */
		ArrayList<ArrayList<BasicBlock>> blockEdges = new ArrayList<ArrayList<BasicBlock>>();
		for (BasicBlock block : basicBlocks) {
			if (block.children.size() != 0) {
				for (BasicBlock child : block.children) {
					ArrayList<BasicBlock> blockEdge = new ArrayList<BasicBlock>();
					blockEdge.add(block);
					blockEdge.add(child);
					blockEdges.add(blockEdge);

				}
			}
		}

		mainGraph.basicBlocks = basicBlocks;
		mainGraph.basicBlockMap = basicBlockMap;
		mainGraph.blockEdges = blockEdges;

		Dominator dom = new Dominator();
		ReverseCFG rev = new ReverseCFG();

		mainGraph = dom.getDominatorTreeForCFGBlocks(mainGraph);
		ArrayList<BasicBlock> reverseBasicBlocks = (ArrayList<BasicBlock>) mainGraph.basicBlocks
				.clone();
		mainGraph.reverseBasicBlocks = reverseBasicBlocks;
		CFG_Graph mainGraphRev = rev.makeReverseCFGBlocks(mainGraph);

		java.util.Collections.reverse(mainGraphRev.reverseBasicBlocks);
		/*
		 * for (BasicBlock block : mainGraph.reverseBasicBlocks) {
		 * System.out.println(block.start + " VISITED: " + block.visited +
		 * " Parents: " + block.parents.size() + " Children: " +
		 * block.children.size()); }
		 */
		// System.exit(0);

		mainGraphRev = dom.getPostDominatorTreeForCFGBlocks((mainGraphRev));

		for (BasicBlock node : mainGraphRev.basicBlocks) {
			if (node.post_dominators.contains(node)) {
				node.post_dominators.remove(node);
			}
		}
		/*
		 * for (BasicBlock node : mainGraphRev.basicBlocks) {
		 * System.out.println("Block: " + node.start + " is post-dominated by "
		 * + node.post_dominators.size() + " blocks\n"); for (BasicBlock dom1 :
		 * node.post_dominators) { System.out.print(dom1.start + " , "); }
		 * //System.out.println; }
		 * 
		 * for (BasicBlock node : mainGraph.basicBlocks) {
		 * System.out.println("Block: " + node.start + " is dominated by " +
		 * node.dominators.size() + " blocks\n"); for (BasicBlock domi :
		 * node.dominators) { System.out.print(domi.start + " , "); }
		 * //System.out.println;
		 * 
		 * }
		 */
		ControlDependency cDep = new ControlDependency();
		mainGraph.basicBlocks = cDep.getControlDependencyBlocks(mainGraph);

		/*
		 * for (BasicBlock node : mainGraph.basicBlocks) {
		 * System.out.println("\nBlock: " + node.start +
		 * " is control dependent on " + node.controlDependencyList.size() +
		 * " blocks: "); for (BasicBlock domi : node.controlDependencyList) {
		 * System.out.print(domi.start + " , "); } //System.out.println;
		 * 
		 * }
		 */
		// System.exit(0);

		int pos = -1;
		int sourceLineNumber = 590;
		for (int key : mainGraph.byteCode_to_sourceCode_mapping.keySet()) {
			if (mainGraph.byteCode_to_sourceCode_mapping.get(key) == sourceLineNumber) {
				pos = key;
			}
		}
		// System.out.println("Node: " + pos + " Source line number: "
		// + mainGraph.byteCode_to_sourceCode_mapping.get(pos));
		// ArrayList<PostDominatorNode> fullCDList = new
		// ArrayList<PostDominatorNode>();

		// System.out.println("PD Nodes Size: " + mainGraph.basicBlocks.size());

		ArrayList<InstructionHandle> dependencyList = new ArrayList<InstructionHandle>();
		for (BasicBlock pdNd : mainGraph.basicBlocks) {
			// System.out.println("Start: " + pdNd.start + " POS: " + pos
			// + " End : " + pdNd.end);
			if (pdNd.start <= pos && pdNd.end >= pos) {
				// System.out.println("Found");
				// System.out.println("Number of CD nodes: "
				// + pdNd.controlDependencyList.size());
				for (BasicBlock contDep : pdNd.controlDependencyList) {
					// System.out.println("Added");
					if (mainGraph.nodesMap.get(contDep.end).nodeName
							.getInstruction() instanceof IfInstruction) {
						dependencyList
								.add(mainGraph.nodesMap.get(contDep.end).nodeName);
					}
				}
			}
		}
		// System.out.println("Dependencies: " + dependencyList.size());
		ArrayList<DependencyInformation> depList = cfg.dependencyAdapter(
				dependencyList, pos, mainGraph.localVariableTable,
				mainGraph.nodes, mainGraph.constantPool);

		System.out.println("Line " + sourceLineNumber + " depends on : "
				+ depList.size() + " conditions");
		System.out.println();

		for (DependencyInformation dep : depList) {
			// System.out.println;
			System.out.println("Instruction: "
					+ dep.dependencyNode
					+ "(at source line number: "
					+ mainGraph.byteCode_to_sourceCode_mapping
							.get(dep.dependencyNode.getPosition()) + ")");

			System.out.println("Must evaluate to : " + dep.true_false);
			System.out.println("For instruction to be TRUE: ");

			System.out.print("Variable : " + dep.variables.get(0).variableName
					+ " (" + dep.variables.get(0).type + ") " + dep.symbol
					+ " ");

			if (dep.variables.get(1).variableName
					.equalsIgnoreCase("AutoCreated")) {
				System.out.println(dep.variables.get(1).value);
			} else {
				System.out.println(dep.variables.get(1).variableName + " ("
						+ dep.variables.get(1).type + ") ");
			}

			int loc = dep.dependencyNode.getPosition();

			cfg.generateReachingDef(mainGraph.localVariableTable,
					mainGraph.nodes, mainGraph.edges, mainGraph.constantPool);

			Nodes node1 = mainGraph.nodesMap.get(loc);
			boolean isOutisdeDefn = true;

			for (Definition def : node1.out) {
				for (VariableValues var : dep.variables) {
					if (def.getVarName().equalsIgnoreCase(var.variableName)
							&& (!def.isOutsideDef())) {
						isOutisdeDefn = false;
						System.out
								.println(var.variableName
										+ " is not an external variable. It is initialized/modified at position: "
										+ mainGraph.byteCode_to_sourceCode_mapping
												.get(def.getLine()));
					}
				}
			}

			System.out.println();
		}

	}
}
