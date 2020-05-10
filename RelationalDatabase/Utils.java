package RelationalDatabase;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;

/**
 * The Utils class providies the package all frequently required
 * methods and operations like Sorting, Generating new objects etc.
 * @author Karthik Rangasai
 */
final public class Utils{

	private Utils() {
		throw new RuntimeException("You should not instantiate this class");
	}

	/**
	* <p>This method converts the numeric value used in the code to it's respective name for a Normal Form.</p>
	* @param normalForm the integer value assigned to each NF from 1NF to 5NF
	* @return the string value required to print the actual Normal Form
	*/
	public static String getNormalForm(int normalForm){
		switch(normalForm){
			case 1 :{
				return "1";
			}
			case 2 :{
				return "2";
			}
			case 3 :{
				return "3";
			}
			case 4 :{
				return "BC";
			}
			case 5 :{
				return "4";
			}
			case 6 :{
				return "5";
			}
		}
		return null;
	}

	/**
	* <p>This method copies the List of Attribute Objects from one ArrayList to another ArrayList.</p>
	* @param copyFrom Source ArrayList
	* @param copyTo Destination ArrayList
	*/
	public static void copyAttributes(ArrayList<Attribute> copyFrom, ArrayList<Attribute> copyTo){
		for(Attribute a : copyFrom){
			if(!copyTo.contains(a)){
				copyTo.add(a);
			}
		}
		// return copyTo;
	}

	/**
	* <p>This method copies the List of FunctionalDependency Objects from one ArrayList to another ArrayList.</p>
	* @param copyFrom Source ArrayList
	* @param copyTo Destination ArrayList
	*/
	public static void copyFunctionalDependencies(ArrayList<FunctionalDependency> copyFrom, ArrayList<FunctionalDependency> copyTo){
		for(FunctionalDependency a : copyFrom){
			copyTo.add(a);
		}
		// return copyTo;
	}

	/**
	* <p>This method creates new FunctionalDependency Objects from one ArrayList in another ArrayList.</p>
	* @param copyFrom Source ArrayList
	* @param copyTo Destination ArrayList
	*/
	public static void generateFunctionalDependencies(ArrayList<FunctionalDependency> copyFrom, ArrayList<FunctionalDependency> copyTo){
		for(FunctionalDependency f : copyFrom){
			copyTo.add(new FunctionalDependency(f.getRelation(), f.getLeftSideAttributes(), f.getRightSideAttributes(), f.getNormalForm()));
		}
		// return copyTo;
	}

	/**
	* <p>This method creates new FunctionalDependency Objects from one ArrayList in another CopyOnWriteArrayList.</p>
	* @param copyFrom Source ArrayList
	* @param copyTo Destination ArrayList
	*/
	public static void generateFunctionalDependencies(ArrayList<FunctionalDependency> copyFrom, CopyOnWriteArrayList<FunctionalDependency> copyTo){
		for(FunctionalDependency f : copyFrom){
			copyTo.add(new FunctionalDependency(f.getRelation(), f.getLeftSideAttributes(), f.getRightSideAttributes(), f.getNormalForm()));
		}
		// return copyTo;
	}

	///////////////////////// Sorting Methods /////////////////////////
	/**
	* <p>Sorts the given list of Attribute objects</p>
	* @param attributes an ArrayList of Attribute object
	*/
	public static void sortAttributes(ArrayList<Attribute> attributes){
		Collections.sort(attributes, new SortAttr());
	}

	/**
	* <p>Sorts the given list of FunctionalDependency objects</p>
	* @param funcDeps an ArrayList of FunctionalDependency object
	*/
	public static void sortFunctionalDependency(ArrayList<FunctionalDependency> funcDeps){
		Collections.sort(funcDeps, new SortFDs());
	}

	/**
	* <p>Sorts the given list of Closure objects</p>
	* @param closures an ArrayList of Closure object
	*/
	public static void sortClosure(ArrayList<Closure> closures){
		Collections.sort(closures, new SortClosure());
	}

	///////////////////////// Printing Methods /////////////////////////
	/**
	* Print the ArrayList of Attribte objects in a neat manner.
	* Used for debugging purposes.
	* @param attributes ArrayList of Attribute objects to be printed
	*/
	public static void printAttributeList(ArrayList<Attribute> attributes){
		for(Attribute a : attributes){
			System.out.print(a.getName() + " ");
		}
		System.out.println("");
	}

	/** 
	* Generates a string from the ArrayList of Attribute objects.
	* @param attributes ArrayList of Attribute objects to be generate String from
	* @return A string
	*/
	public static String stringifyAttributeList(ArrayList<Attribute> attributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : attributes){
			s.append(a.getName());
		}
		return s.toString();
	}

	/** 
	* Generates a string from two ArrayList of Attribute objects separated by a semicolon
	* @param leftAttributes First ArrayList of Attribute objects to be generate String from
	* @param rightAttributes Second ArrayList of Attribute objects to be generate String from
	* @return A string
	*/
	public static String stringifyAttributes(ArrayList<Attribute> leftAttributes, ArrayList<Attribute> rightAttributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : leftAttributes){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		s.append(";");
		for(Attribute a : rightAttributes){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		return s.toString();
	}
}

/**
 * The helper class to provide the sortAttributes method with a comparator method
 * @author Karthik Rangasai
 * @see Utils#sortAttributes
 */
class SortAttr implements Comparator<Attribute>{
	public int compare(Attribute a, Attribute b){
		return ((a.getName()).compareTo((b.getName())));
	}
}

/**
 * The helper class to provide the sortFunctionalDependency method with a comparator method
 * @author Karthik Rangasai
 * @see Utils#sortFunctionalDependency
 */
class SortFDs implements Comparator<FunctionalDependency>{
	public int compare(FunctionalDependency a, FunctionalDependency b){
		return Utils.stringifyAttributeList(a.getLeftSideAttributes()).compareTo(Utils.stringifyAttributeList(b.getLeftSideAttributes()));
	}
}

/**
 * The helper class to provide the sortClosure method with a comparator method
 * @author Karthik Rangasai
 * @see Utils#sortClosure
 */
class SortClosure implements Comparator<Closure>{
	public int compare(Closure a, Closure b){
		return Utils.stringifyAttributeList(a.getLeftSide()).compareTo(Utils.stringifyAttributeList(b.getLeftSide()));
	}
}