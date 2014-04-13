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
		CFG_Graph mainGraph = new CFG_Graph();
		for (CFG_Graph graph : graphs) {
			if (graph.method.getName().equalsIgnoreCase("Recommended_Show")) {
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

			System.out.println("Method: " + graph.method.getName());
			if (graph.method.getName().equalsIgnoreCase("Recommended_Show")) {
				System.out.println("Code: "
						+ graph.method.getCode().toString(true));
			}

		}

		System.out.println("Dotty File generated at: " + outputDottyPath);

		
		BlockMaker bMaker = new BlockMaker();
		System.out.println("Total Nodes "+mainGraph.nodes.size());

		ArrayList<BasicBlock> basicBlocks = bMaker.makeBlockFromNodes(mainGraph.nodes);
		System.out.println("Total Blocks: "+basicBlocks.size());
		SortedMap<Integer, BasicBlock> basicBlockMap = new TreeMap<Integer, BasicBlock>();

		for(BasicBlock block : basicBlocks){
			basicBlockMap.put(block.start, block);
		}
		
		for(BasicBlock block : basicBlocks){
			int parent = block.start;
			if(mainGraph.nodesMap.get(block.end).nodeName.getInstruction() instanceof GotoInstruction){
				int child = ((GotoInstruction)mainGraph.nodesMap.get(block.end).nodeName.getInstruction()).getTarget().getPosition();
				block.children.add(child);
				basicBlockMap.get(child).parents.add(parent);

			} else if(mainGraph.nodesMap.get(block.end).nodeName.getInstruction() instanceof IfInstruction){

				int child1 = ((IfInstruction)mainGraph.nodesMap.get(block.end).nodeName.getInstruction()).getTarget().getPosition();
				int child2 = mainGraph.nodesMap.get(block.end).nodeName.getNext().getPosition();
				block.children.add(child1);
				block.children.add(child2);
				
				basicBlockMap.get(child1).parents.add(parent);
				basicBlockMap.get(child2).parents.add(parent);
				
				
			}
		}
		
		System.out.println("Basic Block Details");
		for(BasicBlock block : basicBlocks){
			System.out.println("Block Number: "+block.blockNumber);
			System.out.print("\nStart: "+block.start+" End: "+block.end+"\n");
			System.out.println("Parents : "+block.parents.size());
			System.out.println("Children : "+block.children.size());
			System.out.println();
		}
		
		mainGraph.basicBlocks = basicBlocks;
		mainGraph.basicBlockMap = basicBlockMap;
		System.exit(0);
		
		for (Nodes node : mainGraph.nodes) {
			// System.out.println("Parents of "+node.nodeName+" = "+node.parents.size());
			if (node.nodeName != null) {
				node.nodeNumber = node.nodeName.getPosition();
			} else {
				node.nodeNumber = -2;
			}
		}

		Dominator dom = new Dominator();

		mainGraph = dom.getDominatorTreeForCFG(mainGraph);

		// for (int key : mainGraph.nodesMap.keySet()) {
		// System.out.println("Key: " + key + " Value: "
		// + mainGraph.nodesMap.get(key).nodeName);
		// }

		// System.out.println("Before reversal");
		// System.out.println("Node " + mainGraph.nodes.get(1).nodeName
		// + " has child "
		// + mainGraph.nodes.get(1).children.get(0).nodeName);
		// System.out.println("Node " + mainGraph.nodes.get(1).nodeName
		// + " has parent "
		// + mainGraph.nodes.get(1).parents.get(0).nodeName);

		ReverseCFG rev = new ReverseCFG();
		CFG_Graph mainGraphRev = rev.makeReverseCFG(mainGraph);
		// System.out.println("After reversal");
		// System.out.println("Node " + mainGraphRev.nodes.get(1).nodeName
		// + " has child "
		// + mainGraphRev.nodes.get(1).children.get(0).nodeName);
		// System.out.println("Node " + mainGraphRev.nodes.get(1).nodeName
		// + " has parent "
		// + mainGraphRev.nodes.get(1).parents.get(0).nodeName);

		java.util.Collections.reverse(mainGraphRev.nodes);
		mainGraphRev = dom.getPostDominatorTreeForCFG(mainGraphRev);

		// System.exit(0);
		for (Nodes node : mainGraphRev.nodes) {
			// System.out.println("Node: " + node.nodeName
			// + " is post-dominated by " + node.post_dominators.size()
			// + " nodes");
			// for (Nodes dom1 : node.post_dominators) {
			// System.out.println(dom1.nodeName + " , ");
			// }
		}

		for (Nodes node : mainGraph.nodes) {
			// System.out.println("Node: " + node.nodeName + " is dominated by "
			// + node.dominators.size() + " nodes");
			// for (Nodes dom : node.dominators) {
			// System.out.println(dom.nodeName + " , ");
			// }

		}
		// System.exit(0);

		for (Nodes node : mainGraphRev.nodes) {
			if (node.post_dominators.contains(node)) {
				node.post_dominators.remove(node);
			}
		}

		NodeToPostDominatorNodeAdapter adapter = new NodeToPostDominatorNodeAdapter();
		SortedMap<Integer, PostDominatorNode> pdNodesMap = new TreeMap<Integer, PostDominatorNode>();
		ArrayList<PostDominatorNode> postDomNodes = new ArrayList<PostDominatorNode>();
		for (Nodes node : mainGraphRev.nodes) {
			if (node.nodeName != null) {
				PostDominatorNode pdNode = adapter
						.convertNodeToPostDominatorNode(node);
				postDomNodes.add(pdNode);

				if (pdNode.nodeName != null) {
					pdNodesMap.put(pdNode.nodeName.getPosition(), pdNode);
				} else {
					if (pdNode.name.equalsIgnoreCase("Entry")) {
						pdNodesMap.put(-1, pdNode);
					} else if (pdNode.name.equalsIgnoreCase("Exit")) {
						pdNodesMap.put(-2, pdNode);
					}
				}
			}
		}

		// System.exit(0);
		// System.out.println("Total Nodes: " + mainGraphRev.nodes.size());
		// System.out.println("Total PD Nodes: " + postDomNodes.size());

		SortedMap<Integer, ArrayList<PostDominatorNode>> postDominatedNodes = new TreeMap<Integer, ArrayList<PostDominatorNode>>();

		for (PostDominatorNode pdNode : postDomNodes) {
			// System.out.println("NODE : " + pdNode.nodeName);

			for (Nodes node : pdNode.post_dominators) {
				PostDominatorNode pdNode1 = null;
				if (node.nodeName != null) {
					pdNode1 = pdNodesMap.get(node.nodeName.getPosition());
					// System.out.println("PD Node : " + pdNode1.nodeName);
				} else {
					if (node.name.equalsIgnoreCase("Entry")) {
						pdNode1 = pdNodesMap.get(-1);
					} else if (node.name.equalsIgnoreCase("Exit")) {
						pdNode1 = pdNodesMap.get(-2);
					} else {
						// System.out.println("Node: " + node.name);
					}
				}

				if (pdNode1 != null) {
					int key = 0;
					if (pdNode1.name.equalsIgnoreCase("Entry")) {
						key = -1;
					} else if (node.name.equalsIgnoreCase("Exit")) {
						key = -2;
					} else {
						key = pdNode1.nodeName.getPosition();
					}
					if (postDominatedNodes.get(key) == null) {
						ArrayList<PostDominatorNode> pdList = new ArrayList<PostDominatorNode>();
						pdList.add(pdNode);
						postDominatedNodes.put(key, pdList);
					} else {
						if (!postDominatedNodes.get(key).contains(pdNode)) {
							postDominatedNodes.get(key).add(pdNode);
						}
					}
					// postDominatedNodes.put(pdNode1, pdNode);
				} else {
					// System.out.println("ERROR!!!\nNODE NULL");
				}

			}
		}

		for (PostDominatorNode pdNode : postDomNodes) {
			pdNode.children = postDominatedNodes.get(pdNode.nodeNumber);
		}

		PostDominatorNode start = null;
		PostDominatorNode target = null;
		for (PostDominatorNode pdNode : postDomNodes) {

			if (pdNode.nodeNumber == 526) {
				start = pdNode;
			} else if (pdNode.nodeNumber == 119) {
				target = pdNode;
			}

			if (pdNode.children == null) {
				pdNode.children = new ArrayList<PostDominatorNode>();
			}

			if (pdNode.children != null) {
				// System.out.println("Node " + pdNode.nodeNumber + "Parent: "
				// + pdNode.parent.nodeName + " - " + pdNode.parent.name
				// + " post dominates: " + pdNode.children.size()
				// + " other nodes");
			} else {
				// System.out.println("Node " + pdNode.nodeNumber
				// + " post dominates NO other nodes");
			}
		}

		for (Nodes n : mainGraph.nodes) {
			if (n.name.equalsIgnoreCase("Exit")) {
				n.nodeNumber = -2;
			}
		}
		ControlDependency cDep = new ControlDependency();
		start = null;
		// System.out.println("Start children: " + start.children.size());
		// if (start != null && target != null) {
		// cDep.DFS(postDomNodes, start, target);
		// } else {
		// System.out.println("Null START/TARGET");
		// }
		ArrayList<PostDominatorNode> pdNodes = cDep.getControlDependency(
				mainGraph, postDomNodes);

		/*
		 * for (PostDominatorNode pdNode : pdNodes) { if
		 * (pdNode.controlDependencyList.size() != 0) {
		 * //System.out.print("\nNode " // +
		 * mainGraph.byteCode_to_sourceCode_mapping // .get(pdNode.nodeNumber) +
		 * "(" // + pdNode.nodeNumber + ")" // + " is control dependent on : ");
		 * for (PostDominatorNode pDN : pdNode.controlDependencyList) { //
		 * System.out.print(" " // + mainGraph.byteCode_to_sourceCode_mapping //
		 * .get(pDN.nodeNumber) + "(" + pDN.nodeNumber // + ")" + " , "); }
		 * //System.out.println(); } }
		 */

		// for (Integer key : postDominatedNodes.keySet()) {
		// if (key >= 496 && key <= 520) {

		// System.out.println("Node " + key + " post dominates: "
		// + postDominatedNodes.get(key).size() + " other nodes");
		// for (PostDominatorNode node : postDominatedNodes.get(key)) {
		// System.out.println(node.nodeName + " NAME: " + node.name);
		// }
		// }
		// }
		// ControlDependency cDep = new ControlDependency();
		// cDep.getLCA(mainGraphRev.nodes.get(0), mainGraphRev.nodes.get(1));

		// Run line number 43 in source code, 240 in byte code

		// System.out.println("195 post dominates: ");
		/*
		 * for (PostDominatorNode postDomNode : pdNodes) { if
		 * (postDomNode.nodeNumber == 572) { for (PostDominatorNode child :
		 * postDomNode.children) { // System.out.println(child.nodeNumber); } }
		 * }
		 */
		System.out.println();
		System.out.println();

		int pos = -1;
		for (int key : mainGraph.byteCode_to_sourceCode_mapping.keySet()) {
			if (mainGraph.byteCode_to_sourceCode_mapping.get(key) == 590) {
				pos = key;
			}
		}
		System.out.println("Node: " + pos + " Source line number: "
				+ mainGraph.byteCode_to_sourceCode_mapping.get(pos));
		ArrayList<PostDominatorNode> fullCDList = new ArrayList<PostDominatorNode>();

		System.out.println("PD Nodes Size: " + pdNodes.size());
		for (PostDominatorNode pN : pdNodes) {
			if (pN.nodeNumber == pos) {
				// System.out.println("Post dominated children: ");
				for (PostDominatorNode children : pN.children) {
					// System.out.println(mainGraph.byteCode_to_sourceCode_mapping
					// .get(children.nodeNumber));
				}
				// System.out.println("Control Dependent nodes: ");
				fullCDList = cDep.getFullControlDependenceList(pdNodes, pN);
				for (PostDominatorNode children : fullCDList) {
					// System.out.println(mainGraph.byteCode_to_sourceCode_mapping
					// .get(children.nodeNumber));
				}
			}
		}
		ArrayList<InstructionHandle> dependencyList = new ArrayList<InstructionHandle>();
		for (PostDominatorNode pdNd : pdNodes) {
			if (pdNd.nodeNumber == pos) {
				System.out.println("Found");
				System.out.println("Number of CD nodes: "+pdNd.controlDependencyList.size());
				for (PostDominatorNode contDep : pdNd.controlDependencyList) {
					System.out.println("Added");
					dependencyList.add(contDep.nodeName);
				}
			}
		}
		System.out.println("Dependencies: " + dependencyList.size());
		ArrayList<DependencyInformation> depList = cfg.dependencyAdapter(
				dependencyList, pos, mainGraph.localVariableTable,
				mainGraph.nodes, mainGraph.constantPool);

		System.out.println("Dep List size: " + depList.size());

		for (DependencyInformation dep : depList) {
			System.out.println();
			System.out.println("Instruction: " + dep.dependencyNode);
			System.out.println("Must evaluate to : " + dep.true_false);
			System.out.println("Depends on variable: "
					+ dep.varVal.variableName);
			System.out.println("Variable Type: " + dep.varVal.type);
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
