package RelationalDatabase;

import java.util.*;

public class Utils{

	public static void printAttributeList(ArrayList<Attribute> attributes){
		for(Attribute a : attributes){
			System.out.print(a.getName() + " ");
		}
		System.out.println("");
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