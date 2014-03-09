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
							path.remove(LCA);
						} else if (A.parent == LCA) {

						} else {
							// System.out.println("No case matched A = "
							// + A.nodeNumber + " A.parent = "
							// + A.parent.nodeName + " LCA = "
							// + LCA.nodeNumber);
						}
					} else {
						// System.out.println("NO LCA FOUND");
					}

					if (path != null) {
						for (PostDominatorNode pdNode : path) {
							// System.out.println(pdNode.nodeNumber + "--->");

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

	public ArrayList<PostDominatorNode> DFS(
			ArrayList<PostDominatorNode> postDomNodes, PostDominatorNode start,
			PostDominatorNode target) {
		ArrayList<PostDominatorNode> searchQueue = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> popped = new ArrayList<PostDominatorNode>();
		ArrayList<PostDominatorNode> path = new ArrayList<PostDominatorNode>();

		searchQueue.add(start);

		while (searchQueue.get(0) != target) {
			// System.out.println("Search queue head "+searchQueue.get(0).nodeNumber+" children: "+searchQueue.get(0).children);

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
		// System.out.println("First Node " + commonParents.get(0).nodeName
		// + " Last Node "
		// + commonParents.get(commonParents.size() - 1).nodeName);
		// System.out.println("LCA: "
		// + commonParents.get(commonParents.size() - 1).nodeName
		// + " Name " + commonParents.get(commonParents.size() - 1).name);
		return commonParents.get(commonParents.size() - 1);
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
	
	public ArrayList<PostDominatorNode> getFullControlDependenceList(ArrayList<PostDominatorNode> pdNodes, PostDominatorNode node){
		ArrayList<PostDominatorNode> fullCDList = new ArrayList<PostDominatorNode>();
		fullCDList.addAll(node.controlDependencyList);
		for(int i=0;i<fullCDList.size();i++){
			fullCDList.addAll(fullCDList.get(i).controlDependencyList);
			
		}
		
		return fullCDList;
	}
}
