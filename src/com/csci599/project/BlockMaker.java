package com.csci599.project;

import java.util.ArrayList;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IfInstruction;

public class BlockMaker {

	// TODO : Get all starters before implementing
	public ArrayList<BasicBlock> makeBlockFromNodes(ArrayList<Nodes> nodes) {
		ArrayList<BasicBlock> basicBlocks = new ArrayList<BasicBlock>();
		ArrayList<Integer> starters = new ArrayList<Integer>();
		int start = -1, end = -1, first = -1;
		int blockNumber = 1;
		// System.out.println("Total Nodes " + nodes.size());

		for (Nodes node : nodes) {
			if (node.nodeName != null) {
				first = node.nodeName.getPosition();
				// System.out.println("First = " + first);
				starters.add(first);

				break;
			}
		}

		for (Nodes node : nodes) {

			if (node.nodeName != null) {

				if (node.nodeName.getInstruction() instanceof BranchInstruction) {
					BranchInstruction brInst = (BranchInstruction) node.nodeName
							.getInstruction();
					if (brInst instanceof GotoInstruction) {
						if (!starters
								.contains(brInst.getTarget().getPosition())) {
							starters.add(brInst.getTarget().getPosition());
						}

					} else if (brInst instanceof IfInstruction) {
						if (!starters
								.contains(brInst.getTarget().getPosition())) {
							starters.add(brInst.getTarget().getPosition());
						}
						if (!starters.contains(node.nodeName.getNext()
								.getPosition())) {
							starters.add(node.nodeName.getNext().getPosition());
						}

					}

				}
			}
		}
		start = first;

		boolean blockCreated = false;

		for (Nodes node : nodes) {
			if (node.nodeName != null) {

				if (starters.contains(node.nodeName.getPosition())) {
					if (blockCreated) {
						start = node.nodeName.getPosition();
						blockCreated = false;
					} else {
						if (start != first) {
							end = node.nodeName.getPrev().getPosition();
							BasicBlock bBlock = new BasicBlock();
							bBlock.start = start;
							bBlock.end = end;
							// System.out.println("Start = " + start);
							// System.out.println("End = " + end);
							// System.out.println("=====ENd of REGULAR block====");
							if (start == first) {
								bBlock.first = true;
							}
							bBlock.blockNumber = blockNumber;
							blockNumber++;
							// blockCreated = true;
							basicBlocks.add(bBlock);
							start = node.nodeName.getPosition();

						}
					}

				}

				if (node.nodeName.getInstruction() instanceof BranchInstruction) {
					BranchInstruction brInst = (BranchInstruction) node.nodeName
							.getInstruction();
					if (brInst instanceof GotoInstruction) {
						end = node.nodeName.getPosition();
						BasicBlock bBlock = new BasicBlock();
						bBlock.start = start;
						bBlock.end = end;
						// System.out.println("Start = " + start);

						// System.out.println("End = " + end);
						// System.out.println("=====ENd of GOTO block====");
						if (start == first) {
							bBlock.first = true;
						}
						bBlock.blockNumber = blockNumber;
						blockNumber++;
						blockCreated = true;
						basicBlocks.add(bBlock);
						// start =
						// ((GotoInstruction)brInst).getTarget().getPosition();
						/*
						 * if (!starters.contains(((GotoInstruction) brInst)
						 * .getTarget().getPosition())) {
						 * starters.add(((GotoInstruction) brInst).getTarget()
						 * .getPosition()); }
						 */

					} else if (brInst instanceof IfInstruction) {
						end = node.nodeName.getPosition();
						BasicBlock bBlock = new BasicBlock();
						bBlock.start = start;
						bBlock.end = end;
						// System.out.println("Start = " + start);

						// System.out.println("End = " + end);
						// System.out.println("=====ENd of IF block====");
						// start = node.nodeName.getNext().getPosition();
						if (start == first) {
							bBlock.first = true;
						}
						bBlock.blockNumber = blockNumber;
						blockNumber++;
						blockCreated = true;
						basicBlocks.add(bBlock);

						/*
						 * if (!starters.contains(((IfInstruction) brInst)
						 * .getTarget().getPosition())) {
						 * starters.add(((IfInstruction) brInst).getTarget()
						 * .getPosition()); }
						 */
					}
				} else if (node.nodeName.getPosition() == nodes.get(nodes
						.size() - 1).nodeName.getPosition()) {
					// System.out.println("Last block. End = "
					// + node.nodeName.getPosition());
					end = node.nodeName.getPosition();
					BasicBlock bBlock = new BasicBlock();
					bBlock.start = start;
					bBlock.end = end;
					bBlock.blockNumber = blockNumber;
					blockNumber++;
					bBlock.last = true;
					blockCreated = true;
					basicBlocks.add(bBlock);
				}
			}
		}

		return basicBlocks;
	}
}
