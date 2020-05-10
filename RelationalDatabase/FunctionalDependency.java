package RelationalDatabase;

import java.util.*;

/** 
* The FunctionalDependency class represents a functional dependency
* of a relation along with it's Normal Form property.
* @author Karthik Rangasai
*/
public class FunctionalDependency{
	///////////////////////// Class Members /////////////////////////
	/** 
	* The input string provided for the functional dependency.
	*/
	private String funcdep;
	/** 
	* The relation to which the funcional dependency belongs to.
	*/
	private Relation relation;
	/** 
	* The attributes which can infer other attributes.
	*/
	private ArrayList<Attribute> x;
	/** 
	* The attributes which are inferred by other attributes.
	*/
	private ArrayList<Attribute> y;
	/** 
	* The normal form of the functional dependency
	* with respect to the relation.
	*/
	private int normalForm;

	///////////////////////// Class Constructors /////////////////////////
	// funcDeps are of the form ** A,B->C A,B->D C->B B->D **
	/** 
	* Class constructor using functional dependency input string
	* and the Relation object it belongs to.
	*/
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

	/** 
	* Class constructor using the LHS and RHS of a functional dependency.
	*/
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

	/** 
	* Class constructor using LHS, RHS of a functional dependency input string
	* , the Relation object it belongs to and it's Normal Form.
	*/
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
	/** 
	* Determines the Normal Form of the Functional Dependency.
	* @param keyAttributes The ArrayList of Key Attributes of the relation
	* @param nonKeyAttributes The ArrayList of Non Key Attributes of the relation
	* @param candidate_key The ArrayList of Candidate Keys of the relation
	*/
	public void computeNormalForm(ArrayList<Attribute> keyAttributes, ArrayList<Attribute> nonKeyAttributes, ArrayList<ArrayList<Attribute>> candidate_key){				
		// 2NF Checking
		boolean is2NF;
		if(isPartialKey(this.x, nonKeyAttributes, candidate_key) && !areAllKeyAttributes(this.y, nonKeyAttributes)){
			is2NF = false;
		} else {
			is2NF = true;
		}
		// if(this.isNonKeyAttribute(this.y, nonKeyAttributes)){
		// 	if(isPartialKey(this.x, nonKeyAttributes, candidate_key)){
		// 		is2NF = false;
		// 	} else {
		// 		is2NF = true;
		// 	}
		// } else {
		// 	is2NF = true;
		// }
		if(is2NF){
			// System.out.println("Setting 2NF");
			this.normalForm = 2;
		}

		// 3NF Checking
		if(this.normalForm == 2){
			if((isFullKey(this.x, candidate_key)) || (isFullKey(this.y, candidate_key) || (isPartialKey(this.y, nonKeyAttributes, candidate_key) && !isNonKeyAttribute(this.y, nonKeyAttributes)))){
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

	/** 
	* Checks whether the list of Attributes form a key of the relation.
	* @param attributes The ArrayList of Attributes to check if it is a key
	* @param candidate_key The ArrayList of Candidate Keys of the relation
	* @return A boolean value
	*/
	private boolean isFullKey(ArrayList<Attribute> attributes, ArrayList<ArrayList<Attribute>> candidate_key){
		return candidate_key.contains(attributes);
	}

	/** 
	* Checks whether the list of Attributes are a partial key of the relation.
	* @param attributes The ArrayList of Attributes to check if it is a partial key
	* @param nonKeyAttributes The ArrayList of Non Key Attributes of the relation
	* @param candidate_key The ArrayList of Candidate Keys of the relation
	* @return A boolean value
	*/
	private boolean isPartialKey(ArrayList<Attribute> attributes, ArrayList<Attribute> nonKeyAttributes, ArrayList<ArrayList<Attribute>> candidate_key){
		for(ArrayList<Attribute> k : candidate_key){
			// System.out.println("Key is: " + k + " and Attr: " + attributes + " and Attr E Key: " + k.containsAll(attributes) + " and are equal: " + k.equals(attributes));
			if(k.containsAll(attributes) && !k.equals(attributes)){
				return true;
			}
		}
		return false;
	}
	// private boolean isPartialKey(ArrayList<Attribute> attributes, ArrayList<Attribute> nonKeyAttributes, ArrayList<ArrayList<Attribute>> candidate_key){
	// 	return (!isFullKey(attributes, candidate_key) && !isNonKeyAttribute(attributes, nonKeyAttributes));
	// 	// return !candidate_key.contains(attributes);
	// }

	// private boolean isFullKey(ArrayList<Attribute> attributes, ArrayList<Attribute> keyAttributes){
	// 	return keyAttributes.containsAll(attributes);
	// }
	// private boolean isPartialKey(ArrayList<Attribute> attributes, ArrayList<Attribute> keyAttributes){
	// 	return !keyAttributes.containsAll(attributes);
	// }

	/** 
	* Checks whether the list of Attributes is not a key of the relation.
	* @param attributes The ArrayList of Attributes to check if it is a partial key
	* @param nonKeyAttributes The ArrayList of Non Key Attributes of the relation
	* @return A boolean value
	*/
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

	/** 
	* Checks whether all the Attributes in the list of Attributes is a Key Attribute of the relation.
	* @param attributes The ArrayList of Attributes to check if it is a partial key
	* @param nonKeyAttributes The ArrayList of Non Key Attributes of the relation
	* @return A boolean value
	*/
	private boolean areAllKeyAttributes(ArrayList<Attribute> attributes, ArrayList<Attribute> nonKeyAttributes){
		boolean isNonKey = true;
		for(Attribute a : attributes){
			if(nonKeyAttributes.contains(a)){
				return false;
			}
		}
		return true;
	}

	/** 
	* Checks if the provided Collection of Attributes are present
	* in the Functional Dependency.
	* @param attributes List of Attributes to be tested for presence in the Functional Dependency
	* @return A boolean value
	*/
	public boolean hasAttributes(Collection<Attribute> attributes){
		return (this.x.containsAll(attributes) || this.y.containsAll(attributes));
	}

	/** 
	* Checks if the Functional Dependency infers multiple attributes.
	* @return A boolean value
	*/
	public boolean isMultivaluedDependency(){
		return (this.y.size() > 1) ? true : false;
	}

	/** 
	* Checks if the Functional Dependency is in BCNF.
	* @return A boolean value
	*/
	public boolean inBCNF(){
		return (this.normalForm >= 4);
	}

	/** 
	* Checks if the LHS and RHS of Functional Dependency are same as the current one.
	*/
	public boolean equals(Object O){
		FunctionalDependency f = (FunctionalDependency)O;
		return this.getLeftSideAttributes().equals(f.getLeftSideAttributes()) && this.getRightSideAttributes().equals(f.getRightSideAttributes());
	}

	///////////////////////// Printing Methods /////////////////////////
	/** 
	* A string representation of Functional Dependency.
	*/
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
	/** 
	* Returns the FD as a string
	* @return A String
	*/
	public String getName(){
		return this.funcdep;
	}

	/** 
	* Returns the relation the FD belongs to.
	* @return A Relation Object
	*/
	public Relation getRelation(){
		return this.relation;
	}

	/** 
	* Returns the leftAttributes member variable
	* @return An ArrayList of Atrributes
	*/
	public ArrayList<Attribute> getLeftSideAttributes(){
		return this.x;
	}

	/** 
	* Returns the rightAttributes member variable
	* @return An ArrayList of Atrributes
	*/
	public ArrayList<Attribute> getRightSideAttributes(){
		return this.y;
	}

	/** 
	* Returns the Normal form of the FD
	* @return An Integer
	*/
	public int getNormalForm(){
		return this.normalForm;
	}
}