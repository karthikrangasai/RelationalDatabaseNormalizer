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
		// System.out.println("\n*)Computing Closures: ");
		ArrayList<Attribute> left = new ArrayList<Attribute>();
		ArrayList<Attribute> right = new ArrayList<Attribute>();
		for(Attribute a : closureAttributes){
			left.add(a);
			right.add(a);
		}
		ArrayList<Attribute> oldLeftAttributes = new ArrayList<Attribute>();
		// System.out.println("   For Attributes " + closureAttributes + ": ");
		do{
			Utils.copyAttributes(right, oldLeftAttributes);
			// System.out.println(right);
			// System.out.println(oldLeftAttributes);
			for(FunctionalDependency f : funcDeps){
				// System.out.println("       For " + f + ": ");
				if(right.containsAll(f.getLeftSideAttributes())){
					// System.out.println("        " + right + " conatains " + f.getLeftSideAttributes() + ": ");
					for(Attribute a : f.getRightSideAttributes()){
						// System.out.println("         " + left + " -> " + right + ": ");
						if(!right.contains(a)){
							right.add(a);
						}
					}
				}
				// boolean hasAllAttributes = true;
				// ArrayList<Attribute> fd_leftSide = f.getLeftSideAttributes();
				// ArrayList<Attribute> fd_rightSide = f.getRightSideAttributes();
				// if(right.containsAll(fd_leftSide)){
				// 	for(Attribute a : fd_rightSide){
				// 		if(!right.contains(a)){
				// 			right.add(a);
				// 		}
						
				// 	}
				// }
				// if(fd_leftSide.size() <= right.size()){
				// 	for(Attribute a : fd_leftSide){
				// 		if(!right.contains(a)){
				// 			hasAllAttributes = false;
				// 			break;
				// 		}
				// 	}
				// 	if(hasAllAttributes){
				// 		for(Attribute a : fd_rightSide){
				// 			if(!right.contains(a)){
				// 				right.add(a);
				// 			}
							
				// 		}
				// 	}
				// }
			}
		}
		while(!right.equals(oldLeftAttributes));
		Utils.sortAttributes(left);
		Utils.sortAttributes(right);
		return new Closure(left, right);
	}

	public static ArrayList<Closure> computeClosure(ArrayList<FunctionalDependency> F){
		Utils.sortFunctionalDependency(F);
		ArrayList<Closure> F_Closure = new ArrayList<Closure>();
		for(FunctionalDependency f : F){
			Closure c = computeClosure(f.getLeftSideAttributes(), F);
			if(!F_Closure.contains(c)){
				F_Closure.add(c);
			}
		}
		return new ArrayList<>(F_Closure);
	}

	public static boolean equivalentClosures(ArrayList<FunctionalDependency> E, ArrayList<FunctionalDependency> F){
		return (Closure.E_Covers_F(E, F) && Closure.E_Covers_F(F, E));
	}

	private static boolean E_Covers_F(ArrayList<FunctionalDependency> E, ArrayList<FunctionalDependency> F){
		for(FunctionalDependency f : F){
			Closure c = Closure.computeClosure(f.getLeftSideAttributes(), E);
			if(!(c.getRightSide().containsAll(f.getRightSideAttributes()))){
				return false;
			}
		}
		return true;
	}

	public boolean equals(Object O){
		Closure c = (Closure)O;
		if((this.getLeftSide().equals(c.getLeftSide())) && (this.getRightSide().equals(c.getRightSide()))){
			return true;
		}else {
			return false;
		}
	}

	///////////////////////// Printing Methods /////////////////////////
	public String toString(){
		String plus = ")+ = {";
		String s = this.stringifyAttributes();
		StringTokenizer st = new StringTokenizer(s, ";");
		return ("   > (" + st.nextToken() + plus + st.nextToken() + "}");
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