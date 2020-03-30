package RelationalDatabase;

import java.util.*;

public class FunctionalDependency{
	///////////////////////// Class Members /////////////////////////
	private String funcdep;
	private Relation relation;
	private ArrayList<Attribute> x;
	private ArrayList<Attribute> y;
	private int normalForm;

	///////////////////////// Class Constructors /////////////////////////
	// funcDeps are of the form ** A,B->C A,B->D C->B B->D **
	public FunctionalDependency(Relation relation, String funcdep){
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
		this.normalForm = 1;
		Utils.sortAttributes(this.x);
		Utils.sortAttributes(this.y);
	}

	public FunctionalDependency(Relation relation, ArrayList<Attribute> x, ArrayList<Attribute> y){
		this.relation = relation;
		this.x = new ArrayList<Attribute>();
		StringBuilder sbLeft = new StringBuilder();
		StringBuilder sbRight = new StringBuilder();
		for(Attribute a : x){
			this.x.add(a);
			sbLeft.append(a.getName());
			sbLeft.append(",");
		}
		sbLeft.deleteCharAt(sbLeft.toString().length() - 1);
		this.y = new ArrayList<Attribute>();
		for(Attribute a : y){
			this.y.add(a);
			sbRight.append(a.getName());
			sbRight.append(",");
		}
		sbRight.deleteCharAt(sbRight.toString().length() - 1);
		this.normalForm = 1;
		this.funcdep = sbLeft.toString() + "->" + sbRight.toString();
	}

	public FunctionalDependency(Relation relation, ArrayList<Attribute> x, ArrayList<Attribute> y, int normalForm){
		this.relation = relation;
		this.x = new ArrayList<Attribute>();
		StringBuilder sbLeft = new StringBuilder();
		StringBuilder sbRight = new StringBuilder();
		for(Attribute a : x){
			this.x.add(a);
			sbLeft.append(a.getName());
			sbLeft.append(",");
		}
		sbLeft.deleteCharAt(sbLeft.toString().length() - 1);
		this.y = new ArrayList<Attribute>();
		for(Attribute a : y){
			this.y.add(a);
			sbRight.append(a.getName());
			sbRight.append(",");
		}
		sbRight.deleteCharAt(sbRight.toString().length() - 1);
		this.normalForm = normalForm;
		this.funcdep = sbLeft.toString() + "->" + sbRight.toString();
	}

	///////////////////////// Class Methods /////////////////////////
	public void computeNormalForm(ArrayList<Attribute> keyAttributes, ArrayList<Attribute> nonKeyAttributes, ArrayList<ArrayList<Attribute>> candidate_key){				
		// 2NF Checking
		boolean is2NF;
		if(this.isNonKeyAttribute(this.y, nonKeyAttributes)){
			if(isPartialKey(this.x, candidate_key)){
				is2NF = false;
			} else {
				is2NF = true;
			}
		} else {
			is2NF = true;
		}
		if(is2NF){
			// System.out.println("Setting 2NF");
			this.normalForm = 2;
		}

		// 3NF Checking
		if(this.normalForm == 2){
			if((isFullKey(this.x, candidate_key)) || (isFullKey(this.y, candidate_key) || (isPartialKey(this.y, candidate_key) && isNonKeyAttribute(this.y, nonKeyAttributes)))){
				// System.out.println("Setting 3NF");
				this.normalForm = 3;
			}
		}
		// BC NF Checking
		if(this.normalForm == 3){
			if(isFullKey(this.x, candidate_key)){
				// System.out.println("Setting BCNF");
				this.normalForm = 4;
			}
		}
	}
	private boolean isFullKey(ArrayList<Attribute> attributes, ArrayList<ArrayList<Attribute>> candidate_key){
		return candidate_key.contains(attributes);
	}
	private boolean isPartialKey(ArrayList<Attribute> attributes, ArrayList<ArrayList<Attribute>> candidate_key){
		return !candidate_key.contains(attributes);
	}
	// private boolean isFullKey(ArrayList<Attribute> attributes, ArrayList<Attribute> keyAttributes){
	// 	return keyAttributes.containsAll(attributes);
	// }
	// private boolean isPartialKey(ArrayList<Attribute> attributes, ArrayList<Attribute> keyAttributes){
	// 	return !keyAttributes.containsAll(attributes);
	// }
	private boolean isNonKeyAttribute(ArrayList<Attribute> attributes, ArrayList<Attribute> nonKeyAttributes){
		boolean isNonKey = true;
		for(Attribute a : attributes){
			if(nonKeyAttributes.contains(a)){
				isNonKey = true;
			} else {
				isNonKey = false;
			}
		}
		return isNonKey;
	}

	public boolean isMultivaluedDependency(){
		return (this.y.size() > 1) ? true : false;
	}

	public boolean equals(Object O){
		FunctionalDependency f = (FunctionalDependency)O;
		return this.getLeftSideAttributes().equals(f.getLeftSideAttributes()) && this.getRightSideAttributes().equals(f.getRightSideAttributes());
	}

	///////////////////////// Printing Methods /////////////////////////
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

	///////////////////////// Getter and Setter Methods /////////////////////////
	public String getName(){
		return this.funcdep;
	}
	public Relation getRelation(){
		return this.relation;
	}
	public ArrayList<Attribute> getLeftSideAttributes(){
		return this.x;
	}
	public ArrayList<Attribute> getRightSideAttributes(){
		return this.y;
	}
	public int getNormalForm(){
		return this.normalForm;
	}
}