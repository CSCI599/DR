package com.csci599.project;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LDC_W;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.StoreInstruction;

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
		String methodName = "RegAction";
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

		System.out.println("Basic Block Details");
		for (BasicBlock blocks : basicBlocks) {
			System.out.println("Block Number: " + blocks.blockNumber);
			System.out.print("\nStart: " + blocks.start + " End: " + blocks.end
					+ "\n");
			System.out.println("Parents : ");
			for (BasicBlock par : blocks.parents) {
				System.out.print(par.start + ", ");

			}
			System.out.println();
			System.out.println("Children : ");
			for (BasicBlock child : blocks.children) {
				System.out.print(child.start + ", ");
			}
			// System.out.println; }

		}
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

		for (BasicBlock node : mainGraph.basicBlocks) {
			ArrayList<BasicBlock> controllers = new ArrayList<BasicBlock>();
			if (node.controlDependencyList.size() != 0) {
				for (BasicBlock controllerNode : node.controlDependencyList) {
					controllers.addAll(controllerNode.controlDependencyList);
				}
			}

			node.controlDependencyList.addAll(controllers);
			HashSet hs = new HashSet();
			hs.addAll(node.controlDependencyList);
			node.controlDependencyList.clear();
			node.controlDependencyList.addAll(hs);

		}

		for (BasicBlock node : mainGraph.basicBlocks) {
			System.out.println("\nBlock: " + node.start
					+ " is control dependent on "
					+ node.controlDependencyList.size() + " blocks: ");
			for (BasicBlock domi : node.controlDependencyList) {
				System.out.print(domi.start + " , ");
			} // System.out.println;

		}

		// System.exit(0);

		int pos = -1;
		int sourceLineNumber = 493;
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

		SortedMap<String, String> int_ext_var = new TreeMap<String, String>();
		ArrayList<Integer> ext_var_index = new ArrayList<Integer>();
		org.apache.bcel.classfile.LocalVariable[] localVariables = mainGraph.localVariableTable
				.getLocalVariableTable();
		for (int j = 0; j < mainGraph.nodes.size(); j++) {
			Nodes node = mainGraph.nodes.get(j);
			Nodes n1 = null;
			Nodes n2 = null;

			if (node.nodeName != null
					&& node.nodeName.getInstruction() instanceof StoreInstruction) {
				if (j > 1) {
					n1 = mainGraph.nodes.get(j - 1);
					n2 = mainGraph.nodes.get(j - 2);
				}
				if (n1 != null && n2 != null) {
					// System.out.println("N1 "+n1.nodeName);
					// System.out.println("N2 "+n2.nodeName);
					// System.out.println("Node "+node.nodeName);
					// System.out.println();
					if (n1.nodeName != null && n2.nodeName != null) {
						if (n1.nodeName.getInstruction() instanceof INVOKEVIRTUAL) {
							String mtName = ((INVOKEVIRTUAL) n1.nodeName
									.getInstruction())
									.getMethodName(mainGraph.constantPool);
							if (mtName.equalsIgnoreCase("getparam")) {

								if (n2.nodeName.getInstruction() instanceof LDC_W) {
									/*
									 * System.out.println("InvokeStatic: " +
									 * ((INVOKEVIRTUAL) n1.nodeName
									 * .getInstruction
									 * ()).getMethodName(mainGraph
									 * .constantPool)); System.out
									 * .println("LDC " + ((LDC_W) n2.nodeName
									 * .getInstruction())
									 * .getValue(mainGraph.constantPool));
									 */

									int index = ((StoreInstruction) node.nodeName
											.getInstruction()).getIndex();
									String external_var = ((LDC_W) n2.nodeName
											.getInstruction()).getValue(
											mainGraph.constantPool).toString();
									// int ext_var_pos = ((LDC_W)
									// n2.nodeName.getInstruction()).getIndex();
									ext_var_index.add(index);
									for (LocalVariable var : localVariables) {
										if (index == var.getIndex()) {
											// System.out.println("Variable: "+var.getName());
											int_ext_var.put(var.getName(),
													external_var);
										}
									}

									// System.out.println("Node: "+node.nodeName.getInstruction().getName());

									// System.out.println();
								}
							}
						}
					}
				}
				// System.out.println(node.nodeName);
			}

		}

		/*
		 * Load followed by Store OR Load ->Invoke->Store
		 */
		for (int j = 0; j < mainGraph.nodes.size(); j++) {
			Nodes node = mainGraph.nodes.get(j);
			Nodes n1 = null;
			Nodes n2 = null;

			if (node.nodeName != null
					&& node.nodeName.getInstruction() instanceof LoadInstruction) {
				int index = ((LoadInstruction) node.nodeName.getInstruction())
						.getIndex();
				if (ext_var_index.contains(index)) {
					n1 = mainGraph.nodes.get(j + 1);
					n2 = mainGraph.nodes.get(j + 2);
					String ext_var = null;
					String int_var = null;

					for (LocalVariable var : localVariables) {
						if (index == var.getIndex()) {
							ext_var = var.getName();
						}
					}

					if (n1.nodeName != null) {
						if (n1.nodeName.getInstruction() instanceof InvokeInstruction) {
							if (n2.nodeName != null
									&& n2.nodeName.getInstruction() instanceof StoreInstruction) {
								int index2 = ((StoreInstruction) n2.nodeName
										.getInstruction()).getIndex();
								for (LocalVariable var : localVariables) {
									if (index2 == var.getIndex()) {
										int_var = var.getName();
									}
								}
							}
						} else if (n1.nodeName.getInstruction() instanceof StoreInstruction) {
							int index2 = ((StoreInstruction) n1.nodeName
									.getInstruction()).getIndex();
							for (LocalVariable var : localVariables) {
								if (index2 == var.getIndex()) {
									int_var = var.getName();
								}
							}
						}
					}

					if (ext_var != null && int_var != null) {
						int_ext_var.put(int_var, ext_var);
					}
				}
			}
		}

		mainGraph.internal_external_variables = int_ext_var;
		/*
		 * for (int key : ext_var_index) { System.out.println("Var Index: " +
		 * key); }
		 */

		// System.exit(0);
		System.out.println("Line " + sourceLineNumber + " depends on : "
				+ depList.size() + " conditions");
		System.out.println();
		SortedMap<String, String> ext_var_values = new TreeMap<String, String>();

		for (DependencyInformation dep : depList) {
			// System.out.println;
			System.out.println("Instruction: "
					+ dep.dependencyNode
					+ "(at source line number: "
					+ mainGraph.byteCode_to_sourceCode_mapping
							.get(dep.dependencyNode.getPosition()) + ")");

			System.out.println("Must evaluate to : " + dep.true_false);
			System.out.println("For instruction to be TRUE: ");

			System.out.println("Variable: ");
			// System.out.print("Variable : " +
			// dep.variables.get(0).variableName
			// + " (" + dep.variables.get(0).type + ") ");

			String value = "";
			String variable_name = "";

			if (dep.variables.get(0).variableName
					.equalsIgnoreCase("AutoCreated")) {
				System.out.println("(VALUE = " + dep.variables.get(0).value
						+ " )");
			} else {
				System.out.println(dep.variables.get(0).variableName + " ("
						+ dep.variables.get(0).type + ") ");
			}

			System.out.println(dep.symbol + " ");

			if (!dep.variables.get(1).variableName.equalsIgnoreCase("this")) {
				if (!dep.variables.get(1).variableName
						.equalsIgnoreCase(dep.variables.get(0).variableName)) {
					if (dep.variables.get(1).variableName
							.equalsIgnoreCase("AutoCreated")) {
						System.out.println(dep.variables.get(1).value);
					} else {
						System.out.println(dep.variables.get(1).variableName
								+ " (" + dep.variables.get(1).type + ") ");
						value = dep.variables.get(1).value.toString();

					}
				}
			} else {
				System.out.println(dep.variables.get(0).value);
				value = dep.variables.get(0).value.toString();
			}

			System.out.println("-----------------");

			if (int_ext_var.keySet()
					.contains(dep.variables.get(0).variableName)) {
				System.out
						.println(int_ext_var.get(dep.variables.get(0).variableName)
								+ " must have the value: " + value);
				variable_name = int_ext_var
						.get(dep.variables.get(0).variableName);
			} else if (int_ext_var.keySet().contains(
					dep.variables.get(1).variableName)) {
				System.out
						.println(int_ext_var.get(dep.variables.get(1).variableName)
								+ " must have the value: " + value);
				variable_name = int_ext_var
						.get(dep.variables.get(1).variableName);

			}
			int loc = dep.dependencyNode.getPosition();

			cfg.generateReachingDef(mainGraph.localVariableTable,
					mainGraph.nodes, mainGraph.edges, mainGraph.constantPool);

			Nodes node1 = mainGraph.nodesMap.get(loc);
			boolean isOutisdeDefn = true;
			if (!variable_name.equalsIgnoreCase("")) {
				ext_var_values.put(variable_name, value);
			}
			for (Definition def : node1.out) {
				for (VariableValues var : dep.variables) {
					if (def.getVarName().equalsIgnoreCase(var.variableName)
							&& (!def.isOutsideDef())) {
						isOutisdeDefn = false;
						if (int_ext_var.keySet().contains(var.variableName)) {
							System.out.println(var.variableName
									+ " depends on external variable "
									+ int_ext_var.get(var.variableName));
						}
						System.out
								.println(var.variableName
										+ " is not an external variable. It is initialized/modified at line: "
										+ mainGraph.byteCode_to_sourceCode_mapping
												.get(def.getLine()));

					}
				}
			}

			System.out.println();
		}
		System.out.println("From ----> To");
		for (String key : int_ext_var.keySet()) {

			System.out.println(int_ext_var.get(key) + "---->" + key);
		}

		TestCaseParser tcParser = new TestCaseParser();

		for (String key : ext_var_values.keySet()) {
			String var_name = key;
			String var_val = "";
			System.out.print("\n" + key + " must have the value ");
			if (ext_var_values.get(key).equalsIgnoreCase("empty")) {
				System.out.print("");
				var_val = "";
			} else {
				System.out.print(ext_var_values.get(key));
				var_val = ext_var_values.get(key);
			}
			tcParser.parseTestCase("Tests/Sample", "name=" + var_name, var_val);
			

		}

		// System.exit(0);
	}
}
