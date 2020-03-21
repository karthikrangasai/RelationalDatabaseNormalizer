package RelationalDatabase;

import java.util.*;

public class Utils{

	public static int getMinimalSize(ArrayList<ArrayList<Attribute>> superKeys){
		int size = superKeys.size();
		int min = superKeys.get(0).size();
		for(int i=1; i<size; i++){
			if(superKeys.get(i).size() <= min){
				min = superKeys.get(i).size();
			}
		}
		return min;
	}

	public static void sortAttributes(ArrayList<Attribute> attributes){
		Collections.sort(attributes, new SortAttr());
	}

	public static void sortClosure(ArrayList<Closure> closures){
		Collections.sort(closures, new SortClosure());
	}

	public static ArrayList<Attribute> copyAttributes(ArrayList<Attribute> copyFrom, ArrayList<Attribute> copyTo){
		for(Attribute a : copyFrom){
			copyTo.add(a);
		}
		return copyTo;
	}

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
		return a.getLeftSide().size() - b.getLeftSide().size();
	}
}