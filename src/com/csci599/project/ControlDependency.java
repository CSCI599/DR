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
				// System.out.println("NodeNumber for B = " + posB);

				if (!checkIfNodeIsInPostDomList(posB, a.post_dominators)) {
					lca = getLCA(a, b);
					// }

					if (lca == null) {
						// System.out.println("2) NO LCA FOUND for "
						// + a.nodeNumber + " and " + b.nodeNumber);
					}
					// System.out.println("3) a = " + a.nodeNumber + " b = "
					// + b.nodeNumber + " lca = " + lca.nodeNumber);

					for (PostDominatorNode pdNode : pdNodes) {
						if (a.nodeName.getPosition() == pdNode.nodeNumber) {
							A = pdNode;
							// System.out.println("Found A");
						} else if (b.nodeNumber == pdNode.nodeNumber) {
							B = pdNode;
							// System.out.println("Found B");
						} else if (lca.nodeNumber == pdNode.nodeNumber) {
							LCA = pdNode;
							// System.out.println("Found LCA");
						} else {
							// System.out.println("No match for node: "+pdNode.nodeNumber);
						}
					}

					// Immediate post_dominator: Out of all its post_doms, the
					// one
					// which has max. post_doms of its own.
					ArrayList<PostDominatorNode> path = null;
					if (LCA != null) {
						// System.out.println("Finding path");
						path = DFS(pdNodes, LCA, B);
						if (LCA == A) {
							System.out.println("REMOVED A");
							path.remove(LCA);
						} else if (A.parent == LCA) {
							System.out.println("LCA IS PARENT");
						} else {
							// System.out.println("No case matched A = "
							// + A.nodeNumber + " A.parent = "
							// + A.parent.nodeName + " LCA = "
							// + LCA.nodeNumber);
						}
					} else {
						// System.out.println("NO LCA FOUND");
					}

					if (A.nodeName.getPosition() > B.nodeName.getPosition()) {
						System.out.println("A > B");
						path = DFS(pdNodes, A, B);
					} else {

					}

					System.out.println("LCA: " + LCA.name + " " + LCA.nodeName);
					System.out.println("Edge: " + edge.get(0) + "-->"
							+ edge.get(1));

					System.out.println("Number of nodes on path: "
							+ path.size());
					if (path != null) {
						for (PostDominatorNode pdNode : path) {
							// System.out.println(pdNode.nodeNumber + "--->");
							System.out.println("Node on path: "
									+ pdNode.nodeName);

							for (PostDominatorNode pDN : pdNodes) {

								// if(pDN.nodeName.getPosition() == 486){
								// System.out.println("Number of PDs for "+pDN.nodeName+" "+pDN.post_dominators.size());
								// System.out.println("Parent of "+pDN.nodeName
								// +" "+pDN.parent.nodeName);
								// }
								if (pDN.nodeNumber == pdNode.nodeNumber) {

									if (!pDN.controlDependencyList.contains(A)
											&& !pDN.children.contains(A)) {

										if (pDN.nodeName.getPosition() == 486) {
											System.out
													.println("=================================================Adding to 486");
										}
										pDN.controlDependencyList.add(A);
										if (pDN.nodeName.getPosition() == 486)
											System.out.println("Added "
													+ A.nodeName + " " + A.name
													+ " to CD list of "
													+ pDN.nodeName);
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
			// System.out.println("Edge: " + edge.get(0).start + "-->"
			// + edge.get(1).start);

			BasicBlock lca = null;
			BasicBlock LCA = null;
			BasicBlock A = null;
			BasicBlock B = null;
			if (edge.get(1) != null) {
				BasicBlock a = cfg.basicBlockMap.get(edge.get(0).start);
				BasicBlock b = cfg.basicBlockMap.get(edge.get(1).start);
				int posB = b.start;
				// System.out.println("NodeNumber for B = " + posB);

				if (!a.post_dominators.contains(b)) {
					lca = getLCA_Block(a, b);
					// }

					if (lca == null) {
						// System.out.println("2) NO LCA FOUND for " + a.start
						// + " and " + b.start);
					}
					// System.out.println("3) a = " + a.nodeNumber + " b = "
					// + b.nodeNumber + " lca = " + lca.nodeNumber);

					for (BasicBlock pdNode : pdNodes) {
						if (a.start == pdNode.start) {
							A = pdNode;
							// System.out.println("Found A");
						} else if (b.start == pdNode.start) {
							B = pdNode;
							// System.out.println("Found B");
						} else if (lca.start == pdNode.start) {
							LCA = pdNode;
							// System.out.println("Found LCA");
						} else {
							// System.out.println("No match for node: "+pdNode.nodeNumber);
						}
					}

					// Immediate post_dominator: Out of all its post_doms, the
					// one
					// which has max. post_doms of its own.
					ArrayList<BasicBlock> path = null;
					if (LCA != null) {
						// System.out.println("Finding path");
						System.out.println("Edge: " + edge.get(0).start + "-->"
								+ edge.get(1).start);
						System.out.println("LCA: " + LCA.start);
						path = DFS_Block(pdNodes, LCA, B);
						if (LCA == A) {
							System.out.println("LCA is A, REMOVED A");
							path.remove(LCA);
						} else if (A.post_dominators.contains(LCA)) {
							System.out.println("LCA IS PARENT");
						} else {
							System.out.println("No case matched A = " + A.start
									+ "B = " + B.start + " LCA = " + LCA.start);
						}

					} else {
						System.out.println("Edge: " + edge.get(0).start + "-->"
								+ edge.get(1).start);
						System.out.println("NO LCA FOUND");
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

	public ArrayList<PostDominatorNode> DFS_backup(
			ArrayList<PostDominatorNode> postDomNodes, PostDominatorNode start,
			PostDominatorNode target) {
		ArrayList<PostDominatorNode> searchQueue = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> popped = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> path = new ArrayList<PostDominatorNode>();
		System.out.println("Start: " + start.nodeName + " Target: "
				+ target.nodeName);
		searchQueue.add(start);

		while (searchQueue.get(0) != target) {
			// System.out.println("Search queue head "+searchQueue.get(0).nodeNumber+" children: "+searchQueue.get(0).children);

			if (!popped.contains(searchQueue.get(0))) {
				popped.add(searchQueue.get(0));
				if (searchQueue.get(0).children.size() > 0) {
					path.add(searchQueue.get(0));
				}
				searchQueue.addAll(0, searchQueue.get(0).children);
				if (start.nodeName.getPosition() == 852
						&& target.nodeName.getPosition() == 476) {
					System.out.println("Added following children for node: "
							+ searchQueue.get(0).nodeName);
					for (PostDominatorNode pn : searchQueue.get(0).children) {
						System.out.print(pn.nodeName.getPosition() + " , ");
					}
					// System.out.println;
				}
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

		// System.out.println("Path : ");
		// for (PostDominatorNode pdNode : path) {
		// System.out.println(pdNode.nodeNumber + "--->");
		// }

		return path;
	}

	public ArrayList<PostDominatorNode> DFS(
			ArrayList<PostDominatorNode> postDomNodes, PostDominatorNode start,
			PostDominatorNode target) {
		ArrayList<PostDominatorNode> searchQueue = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> popped = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> path = new ArrayList<PostDominatorNode>();
		System.out.println("Start: " + start.nodeName + " Target: "
				+ target.nodeName);
		searchQueue.add(start);

		while (searchQueue.get(0) != target) {

			if (!popped.contains(searchQueue.get(0))) {
				popped.add(searchQueue.get(0));
				if (searchQueue.get(0).children.size() > 0) {
					path.add(searchQueue.get(0));
				}

				if (start.nodeName.getPosition() == 852
						&& target.nodeName.getPosition() == 476) {
					System.out.println("Search queue head "
							+ searchQueue.get(0).nodeNumber + " children: "
							+ searchQueue.get(0).children.size());

					System.out.println("Adding following children for node: "
							+ searchQueue.get(0).nodeName);
					for (PostDominatorNode pn : searchQueue.get(0).children) {
						System.out.print(pn.nodeName.getPosition() + " , ");
					}
					// System.out.println;
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

		// System.out.println("Path : ");
		// for (PostDominatorNode pdNode : path) {
		// System.out.println(pdNode.nodeNumber + "--->");
		// }

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

				if (start.start == 852 && target.start == 476) {
					// System.out.println("Search queue head "
					// + searchQueue.get(0).start + " children: "
					// + searchQueue.get(0).children.size());

					// System.out.println("Adding following children for node: "
					// + searchQueue.get(0).start);
					for (BasicBlock pn : searchQueue.get(0).children) {
						// System.out.print(pn.start + " , ");
					}
					// System.out.println;
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

		// System.out.println("Path : ");
		// for (PostDominatorNode pdNode : path) {
		// System.out.println(pdNode.nodeNumber + "--->");
		// }

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
		// System.out.println("First Node " + commonParents.get(1).nodeName
		// + " Last Node "
		// + commonParents.get(commonParents.size() - 1).nodeName);
		// System.out.println("LCA: "
		// + commonParents.get(commonParents.size() - 1).nodeName
		// + " Name " + commonParents.get(commonParents.size() - 1).name);
		return commonParents.get(commonParents.size() - 1);
	}

	public BasicBlock getLCA_Block(BasicBlock node1, BasicBlock node2) {
		// System.out.println("A = " + node1.start + " B = " + node2.start);
		// System.out.println("Parent = ");

		for (BasicBlock par : node2.parents) {
			// System.out.print(par.start + " , ");
			if (par.start == node1.start) {
				// System.out.println(node2.start + " contains " + node1.start);
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
