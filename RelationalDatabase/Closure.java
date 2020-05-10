package RelationalDatabase;

import java.util.*;

/**
* Holds the closure of a set of Attributes with respect to the Funcational Dependencies of a Relation
* @author Karthik Rangasai
*/
public class Closure{
	///////////////////////// Class Members /////////////////////////
	// (A,B)+ = {A,B,C,D}
	/**
	* The list of attributes whose closure the object holds.<br>
	* Eg: leftAttributes = {A} when (A)+ = {A, B, C}
	*/
	private ArrayList<Attribute> leftAttributes;
	/**
	* The list of attributes which is the closure.<br>
	* Eg: leftAttributes = {A, B, C} when (A)+ = {A, B, C}
	*/
	private ArrayList<Attribute> rightAttributes;

	///////////////////////// Class Constructors /////////////////////////
	/**
	* Private constructor that creates the Closure object when one exists after being computed.
	* @param leftAttributes The list of attributes whose closure the object holds.
	* @param rightAttributes The list of attributes which is the closure.
	*/
	private Closure(ArrayList<Attribute> leftAttributes, ArrayList<Attribute> rightAttributes){
		this.leftAttributes = leftAttributes;
		this.rightAttributes = rightAttributes;
	}

	///////////////////////// Class Methods /////////////////////////
	/** 
	* Computes the closure of the given list of attributes with
	* respect to the provided list of Functional Dependencies
	* @param closureAttributes The list of attributes to find the closure of
	* @param funcDeps The list of functional dependecies to refer to for computing the closure
	* @return A new Closure object after computing the closure.
	*/
	public static Closure computeClosure(ArrayList<Attribute> closureAttributes, Collection<FunctionalDependency> funcDeps){
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
		} while(!right.equals(oldLeftAttributes));
		Utils.sortAttributes(left);
		Utils.sortAttributes(right);
		return new Closure(left, right);
	}

	/** 
	* Computes the closure of left side list of attributes of every
	* Functional Dependency with respect to list of Functional Dependencies
	* @param F The list of functional dependecies to refer to for computing the closure
	* @return An ArrayList of Closure objects after computing the closures.
	*/
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

	/** 
	* Checks if two lists of Functional Dependencies are equivalent.
	* @param E An ArrayList of Functional Dependency.
	* @param F An ArrayList of Functional Dependency.
	* @return A boolean value true or false.
	*/
	public static boolean equivalentClosures(ArrayList<FunctionalDependency> E, ArrayList<FunctionalDependency> F){
		return (Closure.E_Covers_F(E, F) && Closure.E_Covers_F(F, E));
	}

	/** 
	* Checks if one list of Functional Dependencies cover another list of Functional Dependencies.
	* @param E An ArrayList of Functional Dependency.
	* @param F An ArrayList of Functional Dependency.
	* @return A boolean value true or false.
	*/
	private static boolean E_Covers_F(ArrayList<FunctionalDependency> E, ArrayList<FunctionalDependency> F){
		for(FunctionalDependency f : F){
			Closure c = Closure.computeClosure(f.getLeftSideAttributes(), E);
			if(!(c.getRightSide().containsAll(f.getRightSideAttributes()))){
				return false;
			}
		}
		return true;
	}

	/** 
	* Tests the specified object with the left and right side lists 
	* of attributes of the given functional dependency.
	*/
	public boolean equals(Object O){
		Closure c = (Closure)O;
		if((this.getLeftSide().equals(c.getLeftSide())) && (this.getRightSide().equals(c.getRightSide()))){
			return true;
		}else {
			return false;
		}
	}

	///////////////////////// Printing Methods /////////////////////////
	/** 
	* @return An ArrayList of Attribute objects
	*/
	public String toString(){
		String plus = ")+ = {";
		String s = this.stringifyAttributes();
		// if(s.equals("")){
		// 	return "";
		// }
		StringTokenizer st = new StringTokenizer(s, ";");
		return ("   > (" + st.nextToken() + plus + st.nextToken() + "}");
	}

	/** 
	* Generates a string from two member variables separated by a semicolon
	* @return A string
	*/
	private String stringifyAttributes(){
		StringBuilder s = new StringBuilder();
		// if(this.leftAttributes.isEmpty() && this.rightAttributes.isEmpty()){
		// 	return "";
		// }
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
	/** 
	* Returns the member variable leftAttrbutes
	* @return An ArrayList of Attribute objects
	*/
	public ArrayList<Attribute> getLeftSide(){
		return this.leftAttributes;
	}

	/** 
	* Returns the member variable right Attributes
	* @return An ArrayList of Attribute objects
	*/
	public ArrayList<Attribute> getRightSide(){
		return this.rightAttributes;
	}
}