package com.csci599.project;

public class NodeToPostDominatorNodeAdapter {

	public PostDominatorNode convertNodeToPostDominatorNode(Nodes node) {
		PostDominatorNode pdNode = new PostDominatorNode();
		pdNode = new PostDominatorNode(node);
		Nodes immediateDom = null;
		int count = 0;
		for (Nodes immDom : pdNode.post_dominators) {
			if (immDom.post_dominators.size() > count) {
				immediateDom = immDom;
				count = immediateDom.post_dominators.size();
			}
		}
		PostDominatorNode parent = new PostDominatorNode(immediateDom);
		if (pdNode.nodeName != null) {
			pdNode.nodeNumber = pdNode.nodeName.getPosition();
		} else if (pdNode.name.equalsIgnoreCase("Entry")) {
			pdNode.nodeNumber = -1;
		} else if (pdNode.name.equalsIgnoreCase("Exit")) {
			pdNode.nodeNumber = -2;
		}
		pdNode.parent = parent;
		return pdNode;
	}
}
