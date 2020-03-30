package RelationalDatabase;

import java.util.*;

public class Utils{
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

	public static ArrayList<Attribute> copyAttributes(ArrayList<Attribute> copyFrom, ArrayList<Attribute> copyTo){
		for(Attribute a : copyFrom){
			if(!copyTo.contains(a)){
				copyTo.add(a);
			}
		}
		return copyTo;
	}

	public static ArrayList<FunctionalDependency> copyFunctionalDependencies(ArrayList<FunctionalDependency> copyFrom, ArrayList<FunctionalDependency> copyTo){
		for(FunctionalDependency a : copyFrom){
			copyTo.add(a);
		}
		return copyTo;
	}

	public static void generateFunctionalDependencies(ArrayList<FunctionalDependency> copyFrom, ArrayList<FunctionalDependency> copyTo){
		for(FunctionalDependency f : copyFrom){
			copyTo.add(new FunctionalDependency(f.getRelation(), f.getLeftSideAttributes(), f.getRightSideAttributes(), f.getNormalForm()));
		}
		// return copyTo;
	}

	///////////////////////// Sorting Methods /////////////////////////
	public static void sortAttributes(ArrayList<Attribute> attributes){
		Collections.sort(attributes, new SortAttr());
	}
	public static void sortFunctionalDependency(ArrayList<FunctionalDependency> funcDeps){
		Collections.sort(funcDeps, new SortFDs());
	}
	public static void sortClosure(ArrayList<Closure> closures){
		Collections.sort(closures, new SortClosure());
	}

	///////////////////////// Printing Methods /////////////////////////
	public static void printAttributeList(ArrayList<Attribute> attributes){
		for(Attribute a : attributes){
			System.out.print(a.getName() + " ");
		}
		System.out.println("");
	}

	public static String stringifyAttributeList(ArrayList<Attribute> attributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : attributes){
			s.append(a.getName());
		}
		return s.toString();
	}

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

class SortAttr implements Comparator<Attribute>{
	public int compare(Attribute a, Attribute b){
		return ((a.getName()).compareTo((b.getName())));
	}
}

class SortFDs implements Comparator<FunctionalDependency>{
	public int compare(FunctionalDependency a, FunctionalDependency b){
		return Utils.stringifyAttributeList(a.getLeftSideAttributes()).compareTo(Utils.stringifyAttributeList(b.getLeftSideAttributes()));
	}
}

class SortClosure implements Comparator<Closure>{
	public int compare(Closure a, Closure b){
		return Utils.stringifyAttributeList(a.getLeftSide()).compareTo(Utils.stringifyAttributeList(b.getLeftSide()));
	}
}