package RelationalDatabase;

import java.util.*;

public class FunctionalDependency{
	// Class Members
	private String funcdep;
	private Relation relation;
	private ArrayList<Attribute> x;
	private ArrayList<Attribute> y;

	// Class Constructors
	public FunctionalDependency(Relation relation, String funcdep){
		// funcDeps are of the form ** A,B->D or B->C,D or A,B->C,D**

		this.funcdep = funcdep;
		this.relation = relation;
		this.x = new ArrayList<Attribute>();
		this.y = new ArrayList<Attribute>();
		StringTokenizer dep = new StringTokenizer(funcdep, "->");
		String leftPart = dep.nextToken();
		String rightPart = dep.nextToken();

		StringTokenizer leftPartAttributes = new StringTokenizer(leftPart, ",");
		while(leftPartAttributes.hasMoreTokens()){
			this.x.add(relation.getAttribute(leftPartAttributes.nextToken()));
		}

		StringTokenizer rightPartAttributes = new StringTokenizer(rightPart, ",");
		while(rightPartAttributes.hasMoreTokens()){
			this.y.add(relation.getAttribute(rightPartAttributes.nextToken()));
		}
	}

	public ArrayList<Attribute> getLeftSideAttributes(){
		return this.x;
	}

	public ArrayList<Attribute> getRightSideAttributes(){
		return this.y;
	}

	public boolean isMultivaluedDependency(){
		return (y.size() > 1) ? false : true;
	}

	public String toString(){
		String s = this.stringifyAttributes();
		StringTokenizer st = new StringTokenizer(s, ";");
		return ("{" + st.nextToken() + "} -> {" + st.nextToken() + "}");
	}

	private String stringifyAttributes(){
		StringBuilder s = new StringBuilder();
		for(Attribute a : this.x){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		s.append(";");
		for(Attribute a : this.y){
			s.append(a.getName());
			s.append(",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		return s.toString();
	}
}