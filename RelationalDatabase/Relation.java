package RelationalDatabase;

import java.util.*;

public class Relation {
	// Class Members
	private String relation;
	private String functionaldependencies;
	private int noOfAttr;
	private ArrayList<Attribute> attributes;
	private ArrayList<ArrayList<Attribute>> superKeys;
	private ArrayList<ArrayList<Attribute>> candidate_key;
	private ArrayList<FunctionalDependency> funcDeps;
	private ArrayList<Closure> closures;
	// public Node[] attributes;


	// Class Constructors
	public Relation(String relation, int noOfAttr, String functionalDeps) {
		// relation is of the form ** R(A,B,C,D) **
		// funcDeps is of the form ** A,B->D;A,B->C;B->E **
		this.noOfAttr = noOfAttr;
		this.relation = relation;
		this.functionaldependencies = functionalDeps;
		this.attributes = new ArrayList<Attribute>();
		this.funcDeps = new ArrayList<FunctionalDependency>();

		StringTokenizer separate_attributes = new StringTokenizer(relation.substring(2, relation.length() - 1), ",");
		while(separate_attributes.hasMoreTokens()){
			attributes.add(new Attribute(separate_attributes.nextToken()));
		}

		superKeys = null;
		candidate_key = null;
		
		StringTokenizer separate_funcDeps = new StringTokenizer(functionalDeps, ";");
		while(separate_funcDeps.hasMoreTokens()){
			this.funcDeps.add(new FunctionalDependency(this, separate_funcDeps.nextToken()));
		}

		closures = null;
	}


	// Class Methods
	public void computeClosures(){
		this.closures = new ArrayList<Closure>();
		ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
		this.computeClosures(0, attributeList);
	}
	public void computeClosures(int index, ArrayList<Attribute> attributeList){
		for(int i=index; i < this.noOfAttr; i++){
			attributeList.add(attributes.get(i));
			Closure computedClosure = Closure.computeClosure(attributeList, this.funcDeps);
			if(computedClosure != null){
				this.closures.add(computedClosure);
			}
			if(i < this.noOfAttr){
				this.computeClosures(i+1, attributeList);
			}
			attributeList.remove(attributeList.size() - 1);
		}
		Utils.sortClosure(this.closures);
	}

	public void computeSuperKeys(){
		superKeys = new ArrayList<ArrayList<Attribute>>();
		for(Closure c : closures){
			if(c.getRightSide().equals(attributes)){
				superKeys.add(c.getLeftSide());
			}
		}
	}
	
	public void computeCandiadteKey(){
		int len = Utils.getMinimalSize(superKeys);
		this.candidate_key = new ArrayList<ArrayList<Attribute>>();
		for(ArrayList<Attribute> k : superKeys){
			if(k.size() == len){
				candidate_key.add(k);
			}
		}
	}

	// Printing Methods
	public void printFDs(){
		for(FunctionalDependency f : funcDeps){
			System.out.println(f + " ");
		}
	}

	private String printListAttributes(ArrayList<Attribute> attributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : attributes){
			s.append(a.getName() + ",");
		}
		return s.toString();
	}

	public void printAttributes(){
		for(Attribute a : this.attributes){
			System.out.print(a.getName() + " ");
		}
		System.out.println("");
	}

	public void printSuperKeys(){
		for(ArrayList<Attribute> k : this.superKeys){
			System.out.print(Utils.stringifyAttributeList(k) + " ");
		}
		System.out.println("");
	}
	public void printCandidateKeys(){
		for(ArrayList<Attribute> k : this.candidate_key){
			System.out.print(Utils.stringifyAttributeList(k) + " ");
		}
		System.out.println("");
	}

	public String toString(){
		return this.relation;
	}



	// Class getters and setters
	public int getNoOfAttr(){
		return this.noOfAttr;
	}
	public ArrayList<Attribute> getAttributes(){
		return this.attributes;
	}
	public ArrayList<ArrayList<Attribute>> getKeys(){
		return this.superKeys;
	}
	public ArrayList<Closure> getClosures(){
		return this.closures;
	}
	public Attribute getAttribute(String attrName){
		for(Attribute a : this.attributes){
			if(a.getName().equals(attrName)){
				return a;
			}
		}
		return null;
	}
}