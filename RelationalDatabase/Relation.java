package RelationalDatabase;

import java.util.*;

public class Relation {
	///////////////////////// Class Members /////////////////////////
	private String relation;
	private String functionaldependencies;
	private int noOfAttr;
	private ArrayList<Attribute> attributes;
	private ArrayList<Attribute> essentialAttributes;
	private ArrayList<Attribute> nonEssentialAttributes;
	private ArrayList<ArrayList<Attribute>> superKeys;
	private ArrayList<ArrayList<Attribute>> candidate_key;
	private ArrayList<Attribute> keyAttributes;
	private ArrayList<Attribute> nonKeyAttributes;
	private ArrayList<FunctionalDependency> funcDeps;
	private ArrayList<FunctionalDependency> minimalCover;
	private ArrayList<Closure> closures;
	private int normalForm;

	///////////////////////// Class Constructors /////////////////////////
	// relation is of the form ** R(A,B,C,D) **
	// funcDeps is of the form ** A,B->D;A,B->C;B->E **
	public Relation(String relation, int noOfAttr, String functionalDeps) {
		this.noOfAttr = noOfAttr;
		this.relation = relation;
		this.functionaldependencies = functionalDeps;
		this.attributes = new ArrayList<Attribute>();
		this.funcDeps = new ArrayList<FunctionalDependency>();

		StringTokenizer separate_attributes = new StringTokenizer(relation.substring(2, relation.length() - 1), ",");
		while(separate_attributes.hasMoreTokens()){
			attributes.add(new Attribute(separate_attributes.nextToken()));
		}

		this.superKeys = null;
		this.candidate_key = null;
		
		StringTokenizer separate_funcDeps = new StringTokenizer(functionalDeps, ";");
		while(separate_funcDeps.hasMoreTokens()){
			this.funcDeps.add(new FunctionalDependency(this, separate_funcDeps.nextToken()));
		}

		this.minimalCover = null;
		this.closures = null;
		this.normalForm = 1;
	}


	///////////////////////// Class Methods /////////////////////////

	/** Computes Essential Attributes for a relation */
	private void getEssentialAttributes(ArrayList<FunctionalDependency> funcDeps){
		HashSet<Attribute> eA = new HashSet<Attribute>();
		HashSet<Attribute> nEA = new HashSet<Attribute>(this.attributes);
		Set<Attribute> S = new HashSet<Attribute>();
		for(FunctionalDependency f : funcDeps){
			eA.addAll(f.getLeftSideAttributes());
			S.addAll(f.getRightSideAttributes());
			eA.addAll(f.getRightSideAttributes());
		}
		eA.removeAll(S);
		nEA.removeAll(eA);
		this.essentialAttributes = new ArrayList<Attribute>(eA);
		Utils.sortAttributes(this.essentialAttributes);
		this.nonEssentialAttributes = new ArrayList<Attribute>(nEA);
		Utils.sortAttributes(this.nonEssentialAttributes);
	}

	/** Computes Closures of all Attributes combinantions 
		that has the essential attributes for a relation */
	public void computeClosures(){	// Driver Method
		this.closures = new ArrayList<Closure>();
		this.getEssentialAttributes(funcDeps);
		this.closures.add(Closure.computeClosure(this.essentialAttributes, this.funcDeps));
		this.computeClosures(0, this.essentialAttributes, this.nonEssentialAttributes);
	}
	// Actual Method
	public void computeClosures(int index, ArrayList<Attribute> essentialAttributeList, ArrayList<Attribute> nonEssentialAttributeList){
		for(int i=index; i < nonEssentialAttributeList.size(); i++){
			essentialAttributeList.add(nonEssentialAttributeList.get(i));
			Closure computedClosure = Closure.computeClosure(essentialAttributeList, this.funcDeps);
			if(computedClosure != null){
				this.closures.add(computedClosure);
			}
			if(i < this.noOfAttr){
				this.computeClosures(i+1, essentialAttributeList, nonEssentialAttributeList);
			}
			essentialAttributeList.remove(essentialAttributeList.size() - 1);
		}
		Utils.sortClosure(this.closures);
	}

	/** Computes Super Keys for a relation */
	public void computeSuperKeys(){
		this.superKeys = new ArrayList<ArrayList<Attribute>>();
		for(Closure c : closures){
			if(c.getRightSide().equals(attributes)){
				superKeys.add(c.getLeftSide());
			}
		}
		for(ArrayList<Attribute> key : this.superKeys){
			Utils.sortAttributes(key);
		}
	}
	
	/** Computes Canidate Keys for a relation */
	public void computeCandiadteKey(){
		int len = this.getMinimalSize();
		this.candidate_key = new ArrayList<ArrayList<Attribute>>();
		HashSet<Attribute> kA = new HashSet<Attribute>();
		HashSet<Attribute> nKA = new HashSet<Attribute>(this.attributes);
		for(ArrayList<Attribute> k : superKeys){
			if(k.size() == len){
				candidate_key.add(k);
				kA.addAll(k);
			}
		}
		this.keyAttributes = new ArrayList<Attribute>(kA);
		Utils.sortAttributes(this.keyAttributes);
		nKA.removeAll(kA);
		this.nonKeyAttributes  = new ArrayList<Attribute>(nKA);
		Utils.sortAttributes(this.nonKeyAttributes);
	}
	private int getMinimalSize(){
		int size = this.superKeys.size();
		int min = this.superKeys.get(0).size();
		for(int i=1; i<size; i++){
			if(this.superKeys.get(i).size() <= min){
				min = this.superKeys.get(i).size();
			}
		}
		return min;
	}

	/** Gets Primary Key for a relation */
	public ArrayList<Attribute> getPrimaryKey(){
		return this.candidate_key.get(0);
	}

	/** Computes Normal of the relation for a relation */
	public void computeNormalForm(){
		for(FunctionalDependency f : this.funcDeps){
			f.computeNormalForm(this.keyAttributes, this.nonKeyAttributes, this.candidate_key);
		}
		boolean is2NF = true;
		for(FunctionalDependency f : this.funcDeps){
			if(f.getNormalForm()!=2){
				is2NF = false;
				break;
			}
		}
		if(is2NF){
			this.normalForm = 2;
		}
		if(this.normalForm == 2){
			boolean is3NF = true;
			for(FunctionalDependency f : this.funcDeps){
				if(f.getNormalForm()!=3){
					is3NF = false;
					break;
				}
			}
			if(is3NF){
				this.normalForm = 3;
			}
		}
		if(this.normalForm == 3){
			boolean isBCNF = true;
			for(FunctionalDependency f : this.funcDeps){
				if(f.getNormalForm()!=4){
					isBCNF = false;
					break;
				}
			}
			if(isBCNF){
				this.normalForm = 4;
			}
		}
	}

	/** Gets Primary Key for a relation */
	public void computeMinimalCover(){
		this.minimalCover = new ArrayList<FunctionalDependency>();
		for(FunctionalDependency f : this.funcDeps){
			// System.out.println("For " + f + " :");
			if(f.isMultivaluedDependency()){
				// System.out.println("	It is a multivalues FD");
				String funcDep = f.getName();
				StringTokenizer st = new StringTokenizer(funcDep, "->");
				String leftAttr = st.nextToken();
				StringBuilder sb = new StringBuilder();
				sb.append(leftAttr);sb.append("->");
				for(Attribute a : f.getRightSideAttributes()){
					sb.append(a.getName());
					// System.out.println("		" + sb.toString());
					this.minimalCover.add(new FunctionalDependency(this, sb.toString()));
					sb.deleteCharAt(sb.length()-1);
				}
			} else {
				this.minimalCover.add(f);
			}
		}
		ArrayList<Closure> minimalCoverClosure = Closure.computeClosure(this.minimalCover); // Computing Minimal Cover Closure
		System.out.println("  1) Closure of Minimal Cover: ");
		for(Closure c : minimalCoverClosure){
			System.out.println("      " + c);
		}
		ArrayList<FunctionalDependency> minimalCoverCopy = new ArrayList<FunctionalDependency>();
		Utils.copyFunctionalDependencies(this.minimalCover, minimalCoverCopy);
		System.out.println("  2) Minimal Cover After Step 2: (minimalCoverCopy)");
		System.out.println("      " + minimalCoverCopy);
		System.out.println("\n  3) Starting Step 3: ");
		for(int i=0; i<minimalCoverCopy.size(); i++){
			FunctionalDependency tempFD = minimalCoverCopy.get(i);
			ArrayList<Attribute> tempFDLeft = tempFD.getLeftSideAttributes();
			if(tempFDLeft.size() >= 2){
				for(int j=0; j<tempFDLeft.size(); j++){
					Attribute tempA = tempFDLeft.get(j);
					tempFDLeft.remove(tempA);
					// If Equivalent
					System.out.print("    -> Minimal Cover Copy: ");
					for(FunctionalDependency f : minimalCoverCopy){
						System.out.print(f + "  ");
					}System.out.println("");
					ArrayList<Closure> minimalCoverCopyClosure = Closure.computeClosure(minimalCoverCopy);
					System.out.println("    -> Closure of Minimal Cover Copy: ");
					for(Closure c : minimalCoverCopyClosure){
						System.out.println("       " + c);
					}
					boolean equivalent = minimalCoverClosure.equals(minimalCoverCopyClosure) || minimalCoverClosure.containsAll(minimalCoverCopyClosure) || minimalCoverCopyClosure.containsAll(minimalCoverClosure);
					System.out.println("    -> Are they Equivalent: " + equivalent + "\n");
					if(equivalent){
						break;
					} else {
						tempFDLeft.add(j, tempA);
					}
				}
			}
		}
		System.out.println("    Minimal Cover After Step 3: (minimalCoverCopy)");
		System.out.println("      " + minimalCoverCopy);
		System.out.println("\n  4) Starting Step 4: ");
		System.out.println("    -> Minimal Cover Copy: " + minimalCoverCopy);
		for(int i=0; i<minimalCoverCopy.size(); i++){
			FunctionalDependency tempFD = minimalCoverCopy.get(i);
			System.out.println("\n    -> Removing: " + tempFD);
			minimalCoverCopy.remove(tempFD);
			ArrayList<Closure> minimalCoverCopyClosure = Closure.computeClosure(minimalCoverCopy);
			System.out.println("    -> Closure of Minimal Cover Copy: ");
			for(Closure c : minimalCoverCopyClosure){
				System.out.println("       " + c);
			}
			boolean equivalent = minimalCoverClosure.equals(minimalCoverCopyClosure) || minimalCoverClosure.containsAll(minimalCoverCopyClosure) || minimalCoverCopyClosure.containsAll(minimalCoverClosure);
			if(!equivalent){
				minimalCoverCopy.add(i, tempFD);
			}
		}

		System.out.println("\n    Minimal Cover After Step 4: (minimalCoverCopy)");
		System.out.println("      " + minimalCoverCopy);
	}

	///////////////////////// Printing Methods /////////////////////////
	public void printAttributes(){
		for(Attribute a : this.attributes){
			System.out.print(a.getName() + " ");
		}
		System.out.println("");
	}

	private String printListAttributes(ArrayList<Attribute> attributes){
		StringBuilder s = new StringBuilder();
		for(Attribute a : attributes){
			s.append(a.getName() + ",");
		}
		return s.toString();
	}

	public void printFDs(){
		for(FunctionalDependency f : funcDeps){
			System.out.println("   >" + f);
		}
	}

	public void printEssentialAttributes(){
		for(Attribute a : this.essentialAttributes){
			System.out.print(a.getName());
		}
		System.out.println("");
	}

	public void printNonEssentialAttributes(){
		for(Attribute a : this.nonEssentialAttributes){
			System.out.print(a.getName());
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

	public void printKeyAttributes(){
		for(Attribute a : this.keyAttributes){
			System.out.print(a.getName());
		}
		System.out.println("");
	}

	public void printNonKeyAttributes(){
		for(Attribute a : this.nonKeyAttributes){
			System.out.print(a.getName());
		}
		System.out.println("");
	}

	public void printNormalForms(){
		StringBuilder s = new StringBuilder();
		int len = 52;
		for(int i=0; i<len; i++){
			s.append("-");
		}
		s.append("\n");
		s.append("|  Functional Dependency  | 1NF | 2NF | 3NF | BCNF |\n");
		for(int i=0; i<len; i++){
			s.append("-");
		}
		s.append("\n");
		for(FunctionalDependency f : this.funcDeps){
			String name = f.getName();
			int normalForm = f.getNormalForm();
			double spaces = (25 - name.length())/2;
			s.append("|");
			for(int i=0; i<Math.floor(spaces); i++){
				s.append(" ");
			}
			s.append(name);
			for(int i=0; i<Math.ceil(spaces)+1; i++){
				s.append(" ");
			}
			s.append("|");
			switch(normalForm){
				case 1 :{
					s.append("  *  |  X  |  X  |  X   |\n");
					break;
				}
				case 2 :{
					s.append("  *  |  *  |  X  |  X   |\n");
					break;
				}
				case 3 :{
					s.append("  *  |  *  |  *  |  X   |\n");
					break;
				}
				case 4 :{
					s.append("  *  |  *  |  *  |  *   |\n");
					break;
				}
			}
		}
		for(int i=0; i<len; i++){
			s.append("-");
		}
		s.append("\n");
		switch(this.normalForm){
			case 1 :{
				s.append("\nRelation is in 1NF\n");
				break;
			}
			case 2 :{
				s.append("\nRelation is in 2NF\n");
				break;
			}
			case 3 :{
				s.append("\nRelation is in 3NF\n");
				break;
			}
			case 4 :{
				s.append("\nRelation is in BCNF\n");
				break;
			}
		}
		System.out.print(s.toString());
	}

	public String toString(){
		return this.relation;
	}

	///////////////////////// Getter and Setter Methods /////////////////////////
	public int getNoOfAttr(){
		return this.noOfAttr;
	}
	public Attribute getAttribute(String attrName){
		for(Attribute a : this.attributes){
			if(a.getName().equals(attrName)){
				return a;
			}
		}
		return null;
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
}