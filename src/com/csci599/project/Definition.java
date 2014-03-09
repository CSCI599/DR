package com.csci599.project;

public class Definition implements Comparable<Definition>{
	
	
	private int line;
	private String varName;
	private boolean outsideDef;
	
	public Definition(int line, String varName, boolean outsideDef) {
		this.line = line;
		this.varName = varName;
		this.outsideDef = outsideDef;
	}
	
	public int getLine() {
		return line;
	}
	public String getVarName() {
		return varName;
	}
	public boolean isOutsideDef() {
		return outsideDef;
	}

	@Override
	public int compareTo(Definition def) {
		if(this.line == def.line)
			return 0;
		else if(this.line < def.line)
			return -1;
		else
			return 1;
	}
	
	public String toString(){
		return "Def: "+line+" - "+varName+" - outside? "+outsideDef;
	}
	
}
