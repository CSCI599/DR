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
			System.out.println("1. Path to class file");
			System.out.println("2. Path to generate dotty file");
			System.out.println("3. Line number to run");
			System.exit(0);
		}
		CFG cfg = new CFG();

		String classPath = args[0];
		String outputDottyPath = args[1];
		int line_num_to_run = Integer.parseInt(args[2]);
		ArrayList<CFG_Graph> graphs = cfg.cfgMaker(classPath, outputDottyPath);

		int i = 1;

		String methodName = "RegAction";
		CFG_Graph mainGraph = new CFG_Graph();
		for (CFG_Graph graph : graphs) {
			if (graph.method.getName().equalsIgnoreCase(methodName)) {
				mainGraph = graph;
			}

			i++;

		}

		System.out.println("Dotty File generated at: " + outputDottyPath);

		BlockMaker bMaker = new BlockMaker();

		//Convert statements to basic blocks
		ArrayList<BasicBlock> basicBlocks = bMaker
				.makeBlockFromNodes(mainGraph.nodes);
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
					BasicBlock child = basicBlockMap.get(mainGraph.nodesMap
							.get(block.end).nodeName.getNext().getPosition());
					block.children.add(child);
					basicBlockMap.get(child.start).parents.add(parent);
				}
			}
		}

		//Edges between basic blocks
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

		//Get dominators and post dominators
		mainGraph = dom.getDominatorTreeForCFGBlocks(mainGraph);
		ArrayList<BasicBlock> reverseBasicBlocks = (ArrayList<BasicBlock>) mainGraph.basicBlocks
				.clone();
		mainGraph.reverseBasicBlocks = reverseBasicBlocks;
		CFG_Graph mainGraphRev = rev.makeReverseCFGBlocks(mainGraph);

		java.util.Collections.reverse(mainGraphRev.reverseBasicBlocks);

		mainGraphRev = dom.getPostDominatorTreeForCFGBlocks((mainGraphRev));

		for (BasicBlock node : mainGraphRev.basicBlocks) {
			if (node.post_dominators.contains(node)) {
				node.post_dominators.remove(node);
			}
		}

		// Get control dependencies
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

		int pos = -1;
		for (int key : mainGraph.byteCode_to_sourceCode_mapping.keySet()) {
			if (mainGraph.byteCode_to_sourceCode_mapping.get(key) == line_num_to_run) {
				pos = key;
			}
		}

		
		//Prepare the dependency list for each node
		ArrayList<InstructionHandle> dependencyList = new ArrayList<InstructionHandle>();
		for (BasicBlock pdNd : mainGraph.basicBlocks) {

			if (pdNd.start <= pos && pdNd.end >= pos) {
				for (BasicBlock contDep : pdNd.controlDependencyList) {
					if (mainGraph.nodesMap.get(contDep.end).nodeName
							.getInstruction() instanceof IfInstruction) {
						dependencyList
								.add(mainGraph.nodesMap.get(contDep.end).nodeName);
					}
				}
			}
		}
		
		//Convert dependencies to a better form.
		ArrayList<DependencyInformation> depList = cfg.dependencyAdapter(
				dependencyList, pos, mainGraph.localVariableTable,
				mainGraph.nodes, mainGraph.constantPool);

		SortedMap<String, String> int_ext_var = new TreeMap<String, String>();
		ArrayList<Integer> ext_var_index = new ArrayList<Integer>();
		org.apache.bcel.classfile.LocalVariable[] localVariables = mainGraph.localVariableTable
				.getLocalVariableTable();
		
		/*
		 * Prepare mapping between internal-external variables
		 */
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

					if (n1.nodeName != null && n2.nodeName != null) {
						if (n1.nodeName.getInstruction() instanceof INVOKEVIRTUAL) {
							String mtName = ((INVOKEVIRTUAL) n1.nodeName
									.getInstruction())
									.getMethodName(mainGraph.constantPool);
							if (mtName.equalsIgnoreCase("getparam")) {

								if (n2.nodeName.getInstruction() instanceof LDC_W) {

									int index = ((StoreInstruction) node.nodeName
											.getInstruction()).getIndex();
									String external_var = ((LDC_W) n2.nodeName
											.getInstruction()).getValue(
											mainGraph.constantPool).toString();
									ext_var_index.add(index);
									for (LocalVariable var : localVariables) {
										if (index == var.getIndex()) {
											int_ext_var.put(var.getName(),
													external_var);
										}
									}
								}
							}
						}
					}
				}
			}

		}

		// Load followed by Store OR Load ->Invoke->Store
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

		
		//Display output
		System.out.println("Line " + line_num_to_run + " depends on : "
				+ depList.size() + " conditions");
		System.out.println();
		SortedMap<String, String> ext_var_values = new TreeMap<String, String>();

		for (DependencyInformation dep : depList) {
			System.out.println("Instruction: "
					+ dep.dependencyNode
					+ "(at source line number: "
					+ mainGraph.byteCode_to_sourceCode_mapping
							.get(dep.dependencyNode.getPosition()) + ")");

			System.out.println("Must evaluate to : " + dep.true_false);
			System.out.println("For instruction to be TRUE: ");

			System.out.println("Variable: ");

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

		// This functionality may not work correctly.
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
			//Hard-coded name and path to Selenium test case.
			tcParser.parseTestCase("Tests/Sample", "name=" + var_name, var_val);

		}

	}
}
