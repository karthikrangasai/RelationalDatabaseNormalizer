package RelationalDatabase;

import java.util.*;

public class Closure{
	///////////////////////// Class Members /////////////////////////
	// (A,B)+ = {A,B,C,D}
	private ArrayList<Attribute> leftAttributes;
	private ArrayList<Attribute> rightAttributes;

	///////////////////////// Class Constructors /////////////////////////
	private Closure(ArrayList<Attribute> leftAttributes, ArrayList<Attribute> rightAttributes){
		this.leftAttributes = leftAttributes;
		this.rightAttributes = rightAttributes;
	}

	///////////////////////// Class Methods /////////////////////////
	public static Closure computeClosure(ArrayList<Attribute> closureAttributes, ArrayList<FunctionalDependency> funcDeps){
		ArrayList<Attribute> left = new ArrayList<Attribute>();
		ArrayList<Attribute> right = new ArrayList<Attribute>();
		for(Attribute a : closureAttributes){
			left.add(a);
			right.add(a);
		}
		ArrayList<Attribute> oldLeftAttributes = new ArrayList<Attribute>();
		do{
			Utils.copyAttributes(right, oldLeftAttributes);
			for(FunctionalDependency f : funcDeps){
				boolean hasAllAttributes = true;
				ArrayList<Attribute> fd_leftSide = f.getLeftSideAttributes();
				ArrayList<Attribute> fd_rightSide = f.getRightSideAttributes();
				if(fd_leftSide.size() <= right.size()){
					for(Attribute a : fd_leftSide){
						if(!right.contains(a)){
							hasAllAttributes = false;
							break;
						}
					}
					if(hasAllAttributes){
						for(Attribute a : fd_rightSide){
							if(!right.contains(a)){
								right.add(a);
							}
							
						}
					}
				}
			}
		}
		while(!left.equals(oldLeftAttributes));
		Utils.sortAttributes(right);
		return new Closure(left, right);
	}

	///////////////////////// Printing Methods /////////////////////////
	public String toString(){
		String plus = ")+ = {";
		String s = this.stringifyAttributes();
		StringTokenizer st = new StringTokenizer(s, ";");
		return ("(" + st.nextToken() + plus + st.nextToken() + "}");
	}
	private String stringifyAttributes(){
		StringBuilder s = new StringBuilder();
		for(Attribute a : this.leftAttributes){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		s.append(";");
		for(Attribute a : this.rightAttributes){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		return s.toString();
	}

	///////////////////////// Getter and Setter Methods /////////////////////////
	public ArrayList<Attribute> getLeftSide(){
		return this.leftAttributes;
	}
	public ArrayList<Attribute> getRightSide(){
		return this.rightAttributes;
	}
}