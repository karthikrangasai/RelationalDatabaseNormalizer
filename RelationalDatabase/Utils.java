package RelationalDatabase;

import java.util.*;

public class Utils{
	public static String getNormalForm(int normalForm){
		String NF = new String();
		switch(normalForm){
			case 1 :{
				NF = "1";
				break;
			}
			case 2 :{
				NF = "2";
				break;
			}
			case 3 :{
				NF = "3";
				break;
			}
			case 4 :{
				NF = "BC";
				break;
			}
			case 5 :{
				NF = "4";
				break;
			}
			case 6 :{
				NF = "5";
				break;
			}
		}
		return NF;
	}

	public static ArrayList<Attribute> copyAttributes(ArrayList<Attribute> copyFrom, ArrayList<Attribute> copyTo){
		for(Attribute a : copyFrom){
			copyTo.add(a);
		}
		return copyTo;
	}

	public static ArrayList<FunctionalDependency> copyFunctionalDependencies(ArrayList<FunctionalDependency> copyFrom, ArrayList<FunctionalDependency> copyTo){
		for(FunctionalDependency a : copyFrom){
			copyTo.add(a);
		}
		return copyTo;
	}

	///////////////////////// Sorting Methods /////////////////////////
	public static void sortAttributes(ArrayList<Attribute> attributes){
		Collections.sort(attributes, new SortAttr());
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

class SortClosure implements Comparator<Closure>{
	public int compare(Closure a, Closure b){
		return Utils.stringifyAttributeList(a.getLeftSide()).compareTo(Utils.stringifyAttributeList(b.getLeftSide()));
	}
}