package com.csci599.project;

public class NodeToPostDominatorNodeAdapter {

	public PostDominatorNode convertNodeToPostDominatorNode(Nodes node) {
		PostDominatorNode pdNode = new PostDominatorNode();
		pdNode = new PostDominatorNode(node);
		Nodes immediateDom = null;
		int diff = 10000;
		if (node.nodeName != null) {
		}
		for (Nodes immDom : pdNode.post_dominators) {
			if (immDom.nodeName != null) {
				int dif = Math.abs(node.nodeName.getPosition()
						- immDom.nodeName.getPosition());
				if (dif < diff) {
					immediateDom = immDom;
					diff = dif;
				}
			}
		}
		PostDominatorNode parent = null;
		if(immediateDom != null){
			 parent = new PostDominatorNode(immediateDom);
		}
		
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
