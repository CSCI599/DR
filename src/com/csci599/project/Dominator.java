package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.InstructionHandle;

public class Dominator {

	public CFG_Graph getDominatorTreeForCFG(CFG_Graph cfg) {

		Nodes entry = new Nodes("Entry");
		entry.children = new ArrayList<Nodes>();
		entry.children.add(cfg.nodes.get(0));
		entry.dominators = new ArrayList<Nodes>();
		entry.dominators.add(entry);
		Nodes exit = new Nodes("Exit");
		exit.parents = new ArrayList<Nodes>();
		exit.dominators = new ArrayList<Nodes>();
		exit.children = new ArrayList<Nodes>();

		// Prepare N
		ArrayList<Nodes> N = new ArrayList<Nodes>();
		N.addAll(cfg.nodes);
		N.add(exit);

		cfg.nodes.get(cfg.nodes.size() - 1).children = new ArrayList<Nodes>();

		cfg.nodes.get(cfg.nodes.size() - 1).children.add(exit);
		cfg.nodesMap.remove(cfg.nodes.get(cfg.nodes.size() - 1).nodeName
				.getPosition());
		cfg.nodesMap.put(
				cfg.nodes.get(cfg.nodes.size() - 1).nodeName.getPosition(),
				cfg.nodes.get(cfg.nodes.size() - 1));

		N.get(0).parents.add(entry);
		ArrayList<Nodes> searchQueue = new ArrayList<Nodes>();

		ArrayList<Nodes> T = new ArrayList<Nodes>();

		// Prepare T
		T.addAll(N);
		boolean change = true;

		// Set initial dominators to N
		for (Nodes n : N) {
			if (!n.name.equalsIgnoreCase("Entry")) {
				n.dominators.addAll(N);
			}

		}

		while (change) {

			// Mark all nodes as not-visited
			for (Nodes n : N) {
				n.visited = false;
			}

			change = false;

			searchQueue.clear();
			searchQueue.add(N.get(0));

			while (!searchQueue.isEmpty()) {

				Nodes n = searchQueue.get(0);

				if (n.visited || n == null) {
					searchQueue.remove(0);

					continue;
				}

				if (n.parents == null || n.parents.size() == 0) {

					n.visited = true;
					searchQueue.remove(0);
				} else if (n.parents.size() == 1) {
					T.clear();
					T.addAll(n.parents.get(0).dominators);
				} else {

					ArrayList<Nodes> parents = new ArrayList<Nodes>();
					ArrayList<ArrayList<Nodes>> dominators_of_parents = new ArrayList<ArrayList<Nodes>>();

					ArrayList<Nodes> dominators_for_current_node = new ArrayList<Nodes>();
					parents.addAll(n.parents);
					for (Nodes parent : n.parents) {
						dominators_of_parents.add(parent.dominators);
					}
					while (dominators_of_parents.size() > 1) {
						dominators_for_current_node = intersection(
								dominators_of_parents.get(0),
								dominators_of_parents.get(1));

						dominators_of_parents.remove(0);
						dominators_of_parents.remove(0);
						dominators_of_parents.add(0,
								dominators_for_current_node);
					}

					T.clear();
					T.addAll(dominators_for_current_node);
				}

				T.add(searchQueue.get(0));
				ArrayList<Nodes> children = new ArrayList<Nodes>();

				for (Nodes child : n.children) {

					if (child.name.equalsIgnoreCase("exit")) {
						children.add(exit);
					} else if (child != null) {
						children.add(cfg.nodesMap.get(child.nodeName
								.getPosition()));
					}
				}

				searchQueue.addAll(children);

				// Mark Node as visited
				n.visited = true;

				searchQueue.remove(0);

				// Compare T and dominatorNodes
				if (!T.equals(n.dominators)) {

					change = true; // Dominator set has changed
					n.dominators.clear();
					n.dominators.addAll(T);
					T.clear();
					T.addAll(N);
				}

			}

		}

		cfg.nodesMap.clear();
		for (Nodes node : N) {
			if (node.nodeName != null) {
				cfg.nodesMap.put(node.nodeName.getPosition(), node);
			}
		}

		return cfg;
	}

	public CFG_Graph getPostDominatorTreeForCFG(CFG_Graph cfg) {
		System.out
				.println("--------------------------------------------------------------------------------------------");
		System.out.println("TOTAL NODES: " + cfg.nodes.size());
		System.out
				.println("--------------------------------------------------------------------------------------------");
		cfg.nodes.get(0).parents = new ArrayList<Nodes>();

		// System.out.println("First Node's parent: "+cfg.nodes.get(0).parents.get(0).nodeName);
		Nodes entry = new Nodes("Entry");
		entry.parents = new ArrayList<Nodes>();
		entry.children = new ArrayList<Nodes>();
		entry.post_dominators = new ArrayList<Nodes>();
		// entry.dominators.add(entry);

		Nodes exit = new Nodes("Exit");
		exit.children = new ArrayList<Nodes>();

		System.out.println("CFG head: " + cfg.nodes.get(0).nodeName);
		// System.exit(0);
		exit.post_dominators = new ArrayList<Nodes>();
		exit.post_dominators.add(exit);
		exit.children = new ArrayList<Nodes>();
		exit.children.add(cfg.nodes.get(0));

		// Prepare N
		ArrayList<Nodes> N = new ArrayList<Nodes>();
		N.addAll(cfg.nodes);
		N.add(entry);

		cfg.nodes.get(cfg.nodes.size() - 1).children = new ArrayList<Nodes>();

		cfg.nodes.get(cfg.nodes.size() - 1).children.add(entry);
		cfg.nodesMap.remove(cfg.nodes.get(cfg.nodes.size() - 1).nodeName
				.getPosition());
		cfg.nodesMap.put(
				cfg.nodes.get(cfg.nodes.size() - 1).nodeName.getPosition(),
				cfg.nodes.get(cfg.nodes.size() - 1));

		N.get(0).parents.add(exit);
		ArrayList<Nodes> searchQueue = new ArrayList<Nodes>();

		ArrayList<Nodes> T = new ArrayList<Nodes>();

		// Prepare T
		T.addAll(N);
		boolean change = true;

		// Set initial dominators to N
		for (Nodes n : N) {
			if (!n.name.equalsIgnoreCase("Exit")) {
				n.post_dominators.add(exit);
				n.post_dominators.addAll(N);
			}

		}

		// System.out.println("Search queue head: " + N.get(0).name);
		// System.out.println("Search queue head: " + N.get(0).nodeName);

		// System.exit(0);
		while (change) {

			// Mark all nodes as not-visited
			for (Nodes n : N) {
				n.visited = false;
			}

			change = false;

			searchQueue.clear();
			searchQueue.add(N.get(0));

			while (!searchQueue.isEmpty()) {
				 //System.out.println("Search queue head: "
				 //+ searchQueue.get(0).nodeName);

				// System.out.println("Search queue head: " +
				// searchQueue.get(0).name);
				// System.out.println("Search queue head: " +
				// searchQueue.get(0).nodeName);

				Nodes n = searchQueue.get(0);

				if (n.visited || n == null) {
					searchQueue.remove(0);

					continue;
				}

				if (n.parents == null || n.parents.size() == 0) {
					// System.out.println("Search queue head: " +
					// searchQueue.get(0).name);
					// System.out.println("Search queue head: " +
					// searchQueue.get(0).nodeName);
					// System.out.print(" has already been visited");
					n.visited = true;
					searchQueue.remove(0);
					if (searchQueue.isEmpty()) {
						break;
					}
				} else if (n.parents.size() == 1) {
					//System.out.println("One Parent");
					T.clear();

					for (Nodes node : cfg.nodes) {
						if (node.nodeName != null) {
							if (n.parents.get(0).nodeName != null) {
								if (node.nodeName.getPosition() == n.parents
										.get(0).nodeName.getPosition()) {
									T.addAll(node.post_dominators);
									//System.out.println("Parent "+node.parents.get(0).nodeName+" has "+node.parents.get(0).post_dominators.size()+" PDs");
									break;
								}
							}else{
								T.addAll(n.parents.get(0).post_dominators);
								//System.out.println("Parent_NULL "+node.parents.get(0).nodeName+" has "+node.parents.get(0).post_dominators.size()+" PDs");
								break;
							}
						}
					}
					// T.addAll(n.parents.get(0).post_dominators);

				} else {
					// System.out.println("Two Parents " + n.parents.size());

					ArrayList<Nodes> parents = new ArrayList<Nodes>();
					ArrayList<ArrayList<Nodes>> dominators_of_parents = new ArrayList<ArrayList<Nodes>>();

					ArrayList<Nodes> dominators_for_current_node = new ArrayList<Nodes>();

					// parents.addAll(n.parents);

					for (Nodes node : cfg.nodes) {
						if (node.nodeName != null) {
							for (Nodes par : n.parents) {
								if (node.nodeName.getPosition() == par.nodeName
										.getPosition()) {
									parents.add(node);
								}
							}
						}
					}
					for (Nodes parent : parents) {
						dominators_of_parents.add(parent.post_dominators);
						//System.out.println("Parent: " + parent.nodeName
						//		+ " post-dominators: "
						//		+ parent.post_dominators.size());
					}
					while (dominators_of_parents.size() > 1) {
						dominators_for_current_node = intersection(
								dominators_of_parents.get(0),
								dominators_of_parents.get(1));

						dominators_of_parents.remove(0);
						dominators_of_parents.remove(0);
						dominators_of_parents.add(0,
								dominators_for_current_node);
						// System.out.println("Number of dominators: "
						// + dominators_for_current_node.size());
					}

					T.clear();
					//System.out.println("Found "
					//		+ dominators_for_current_node.size()
					//		+ " post-dominators: "
					//		+ dominators_for_current_node.get(0).nodeName);
					T.addAll(dominators_for_current_node);
				}

				T.add(searchQueue.get(0));
				ArrayList<Nodes> children = new ArrayList<Nodes>();

				for (Nodes child : n.children) {

					if (child.name.equalsIgnoreCase("Entry")) {
						children.add(entry);
					} else if (child != null) {
						children.add(cfg.nodesMap.get(child.nodeName
								.getPosition()));
					}
				}

				searchQueue.addAll(children);
				// System.out.println("Added "+children.size()+" new nodes");

				// Mark Node as visited
				n.visited = true;

				// Compare T and dominatorNodes
				if (!T.equals(n.post_dominators)) {

					change = true; // Dominator set has changed
					n.post_dominators.clear();
					n.post_dominators.addAll(T);

					for (Nodes node : cfg.nodes) {
						if (node.nodeName != null) {

							if (node.nodeName.getPosition() == n.nodeName
									.getPosition()) {
								node = n;
							}

						}
					}

					//System.out.println("Added: " + n.post_dominators.size()
					//		+ " post-dominators");
					T.clear();
					T.add(exit);
					T.addAll(N);
				}
				// System.out.println("Node "+searchQueue.get(0).nodeName+" has "+searchQueue.get(0).post_dominators.size()+" post-dominators");
				searchQueue.remove(0);
				for (Nodes node : cfg.nodes) {
					if (node.nodeName != null) {

						if (node.nodeName.getPosition() == n.nodeName
								.getPosition()) {
							//System.out.println("Original Node " + node.nodeName
							//		+ " has " + node.post_dominators.size()
							//		+ " post-dominators "
							//		+ node.post_dominators.get(0).nodeName);
						}

					}
				}

			}

		}

		cfg.nodesMap.clear();
		for (Nodes node : N) {
			if (node.nodeName != null) {
				cfg.nodesMap.put(node.nodeName.getPosition(), node);
			}
		}

		return cfg;
	}

	public ArrayList<Nodes> intersection(ArrayList<Nodes> nodes,
			ArrayList<Nodes> nodes2) {
		ArrayList<Nodes> list = new ArrayList<Nodes>();
		// System.out.println("Intersection");
		// System.out.println("Node 0: " + nodes2.get(0).name);
		for (Nodes t : nodes) {
			 //System.out.println("Checking " + t.nodeName + " OR " + t.name);
			if (nodes2.contains(t)) {
				 //System.out.print(" FOUND ");
				list.add(t);
			}
		}

		return list;
	}

}
