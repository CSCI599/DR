package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class ControlDependency {

	public ArrayList<PostDominatorNode> getControlDependency(CFG_Graph cfg,
			ArrayList<PostDominatorNode> pdNodes) {
		for (ArrayList<InstructionHandle> edge : cfg.edges) {

			Nodes lca = null;
			PostDominatorNode LCA = null;
			PostDominatorNode A = null;
			PostDominatorNode B = null;
			if (edge.get(1) != null) {
				Nodes a = cfg.nodesMap.get(edge.get(0).getPosition());
				Nodes b = cfg.nodesMap.get(edge.get(1).getPosition());
				int posB = b.nodeNumber;

				if (!checkIfNodeIsInPostDomList(posB, a.post_dominators)) {
					lca = getLCA(a, b);

		
					for (PostDominatorNode pdNode : pdNodes) {
						if (a.nodeName.getPosition() == pdNode.nodeNumber) {
							A = pdNode;
						} else if (b.nodeNumber == pdNode.nodeNumber) {
							B = pdNode;
						} else if (lca.nodeNumber == pdNode.nodeNumber) {
							LCA = pdNode;
						} 
					}

					ArrayList<PostDominatorNode> path = null;
					if (LCA != null) {
						path = DFS(pdNodes, LCA, B);
						if (LCA == A) {
							path.remove(LCA);
						} else if (A.parent == LCA) {
						} 
					} 

					if (A.nodeName.getPosition() > B.nodeName.getPosition()) {
						System.out.println("A > B");
						path = DFS(pdNodes, A, B);
					} else {

					}

					

					if (path != null) {
						for (PostDominatorNode pdNode : path) {
							System.out.println("Node on path: "
									+ pdNode.nodeName);

							for (PostDominatorNode pDN : pdNodes) {
								if (pDN.nodeNumber == pdNode.nodeNumber) {

									if (!pDN.controlDependencyList.contains(A)
											&& !pDN.children.contains(A)) {
										pDN.controlDependencyList.add(A);
									}
								}
							}
						}
					}
				}
			}
		}

		return pdNodes;
	}

	public ArrayList<BasicBlock> getControlDependencyBlocks(CFG_Graph cfg) {

		ArrayList<BasicBlock> pdNodes = cfg.basicBlocks;
		for (ArrayList<BasicBlock> edge : cfg.blockEdges) {
			BasicBlock lca = null;
			BasicBlock LCA = null;
			BasicBlock A = null;
			BasicBlock B = null;
			if (edge.get(1) != null) {
				BasicBlock a = cfg.basicBlockMap.get(edge.get(0).start);
				BasicBlock b = cfg.basicBlockMap.get(edge.get(1).start);
				int posB = b.start;

				if (!a.post_dominators.contains(b)) {
					lca = getLCA_Block(a, b);

					for (BasicBlock pdNode : pdNodes) {
						if (a.start == pdNode.start) {
							A = pdNode;
						} else if (b.start == pdNode.start) {
							B = pdNode;
						} else if (lca.start == pdNode.start) {
							LCA = pdNode;
						} 
					}

					// Immediate post_dominator: Out of all its post_doms, the
					// one
					// which has max. post_doms of its own.
					ArrayList<BasicBlock> path = null;
					if (LCA != null) {
						// System.out.println("Finding path");
						
						path = DFS_Block(pdNodes, LCA, B);
						if (LCA == A) {
							
							path.remove(LCA);
						} else if (A.post_dominators.contains(LCA)) {
						} 

					} 

					if (A.start > B.start) {
						// System.out.println("A > B");
						path = DFS_Block(pdNodes, A, B);
					} else {

					}

					// System.out.println("Number of nodes on path: "
					// + path.size());
					if (path != null) {
						for (BasicBlock pdNode : path) {
							// System.out.println(pdNode.nodeNumber + "--->");
							// System.out.println("Node on path: " +
							// pdNode.start);

							for (BasicBlock pDN : pdNodes) {

								// if(pDN.nodeName.getPosition() == 486){
								// System.out.println("Number of PDs for "+pDN.nodeName+" "+pDN.post_dominators.size());
								// System.out.println("Parent of "+pDN.nodeName
								// +" "+pDN.parent.nodeName);
								// }
								if (pDN.start == pdNode.start
										&& pDN.start != LCA.start) {
									// System.out.println("Checking "+pDN.start);
									if (!pDN.controlDependencyList.contains(A)) {// &&
																					// !pDN.children.contains(A)
										pDN.controlDependencyList.add(A);
										// System.out.println("Added "+A.start+" to the CD list of "+pDN.start);

									}
								}
							}
						}
					}
				}
			}

			// System.out.println;
			// System.out.println;
		}

		return pdNodes;
	}



	public ArrayList<PostDominatorNode> DFS(
			ArrayList<PostDominatorNode> postDomNodes, PostDominatorNode start,
			PostDominatorNode target) {
		ArrayList<PostDominatorNode> searchQueue = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> popped = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> path = new ArrayList<PostDominatorNode>();

		searchQueue.add(start);

		while (searchQueue.get(0) != target) {

			if (!popped.contains(searchQueue.get(0))) {
				popped.add(searchQueue.get(0));
				if (searchQueue.get(0).children.size() > 0) {
					path.add(searchQueue.get(0));
				}

				if (start.nodeName.getPosition() == 852
						&& target.nodeName.getPosition() == 476) {
				

			
					
				}

				searchQueue.addAll(0, searchQueue.get(0).children);

			} else {
				if (path.contains(searchQueue.get(0))) {
					path.remove(searchQueue.get(0));
				}
				searchQueue.remove(0);

			}
			if (searchQueue.size() == 0) {
				break;
			}
		}
		if (searchQueue.get(0) == target) {
			path.add(searchQueue.get(0));
		}

		

		return path;
	}

	public ArrayList<BasicBlock> DFS_Block(ArrayList<BasicBlock> postDomNodes,
			BasicBlock start, BasicBlock target) {
		ArrayList<BasicBlock> searchQueue = new ArrayList<BasicBlock>();
		ArrayList<BasicBlock> popped = new ArrayList<BasicBlock>();
		ArrayList<BasicBlock> path = new ArrayList<BasicBlock>();
		// System.out
		// .println("Start: " + start.start + " Target: " + target.start);
		searchQueue.add(start);

		while (searchQueue.get(0) != target) {

			if (!popped.contains(searchQueue.get(0))) {
				popped.add(searchQueue.get(0));
				if (searchQueue.get(0).children.size() > 0) {
					path.add(searchQueue.get(0));
				}

				

				searchQueue.addAll(0, searchQueue.get(0).children);

			} else {
				if (path.contains(searchQueue.get(0))) {
					path.remove(searchQueue.get(0));
				}
				searchQueue.remove(0);

			}
			if (searchQueue.size() == 0) {
				break;
			}
		}
		if (searchQueue.get(0) == target) {
			path.add(searchQueue.get(0));
		}

		return path;
	}

	public boolean checkIfNodeIsInPostDomList(int instNum,
			ArrayList<Nodes> post_dom_list) {
		for (Nodes node : post_dom_list) {
			if (node.nodeName != null) {
				if (node.nodeName.getPosition() == instNum) {
					return true;
				}
			}
		}
		return false;
	}

	public Nodes getLCA(Nodes node1, Nodes node2) {
		ArrayList<Nodes> commonParents = intersection(node1.post_dominators,
				node2.post_dominators);

		return commonParents.get(commonParents.size() - 1);
	}

	public BasicBlock getLCA_Block(BasicBlock node1, BasicBlock node2) {
		
		for (BasicBlock par : node2.parents) {
			if (par.start == node1.start) {
				return node1;
			}
		}

		ArrayList<BasicBlock> commonParents = intersection_blocks(
				node1.post_dominators, node2.post_dominators);
		// System.out.println("First Node " + commonParents.get(0).start
		// + " Last Node "
		// + commonParents.get(commonParents.size() - 1).start);
		// System.out.println("Common Parents: ");
		BasicBlock lca = commonParents.get(0);
		for (BasicBlock block : commonParents) {
			// System.out.print(block.start + " , ");
			if (block.start < lca.start) {
				lca = block;
			}

		}
		// System.out.println;
		// System.out.println("LCA: "
		// + commonParents.get(commonParents.size() - 1).nodeName
		// + " Name " + commonParents.get(commonParents.size() - 1).name);
		return lca;
	}

	public ArrayList<Nodes> intersection(ArrayList<Nodes> nodes,
			ArrayList<Nodes> nodes2) {
		ArrayList<Nodes> list = new ArrayList<Nodes>();
		// System.out.println("Intersection");
		// System.out.println("Node 0: " + nodes2.get(0).name);
		for (Nodes t : nodes) {
			// System.out.println("Checking " + t.nodeName + " OR " + t.name);
			if (nodes2.contains(t)) {
				// System.out.print(" FOUND ");
				list.add(t);
			}
		}

		return list;
	}

	public ArrayList<BasicBlock> intersection_blocks(
			ArrayList<BasicBlock> blocks, ArrayList<BasicBlock> blocks2) {
		ArrayList<BasicBlock> list = new ArrayList<BasicBlock>();
		// System.out.println("Intersection");
		// System.out.println("Node 0: " + nodes2.get(0).name);
		for (BasicBlock t : blocks) {
			// System.out.println("Checking " + t.nodeName + " OR " + t.name);
			if (blocks2.contains(t)) {
				// System.out.print(" FOUND ");
				list.add(t);
			}
		}

		return list;
	}

	public ArrayList<PostDominatorNode> getFullControlDependenceList(
			ArrayList<PostDominatorNode> pdNodes, PostDominatorNode node) {
		ArrayList<PostDominatorNode> fullCDList = new ArrayList<PostDominatorNode>();
		fullCDList.addAll(node.controlDependencyList);
		for (int i = 0; i < fullCDList.size(); i++) {
			fullCDList.addAll(fullCDList.get(i).controlDependencyList);

		}

		return fullCDList;
	}
}
