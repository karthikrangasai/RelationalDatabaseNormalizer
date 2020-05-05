package RelationalDatabase;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;

public class Relation {
	///////////////////////// Class Members /////////////////////////
	private String relation;
	private String functionaldependencies;
	private int noOfAttr;
	private ArrayList<Attribute> attributes;
	private ArrayList<FunctionalDependency> funcDeps;
	private ArrayList<FunctionalDependency> partialFuncDeps;
	private ArrayList<FunctionalDependency> fullFuncDeps;
	private ArrayList<Attribute> essentialAttributes;
	private ArrayList<Attribute> nonEssentialAttributes;
	private ArrayList<ArrayList<Attribute>> superKeys;
	private ArrayList<ArrayList<Attribute>> candidate_key;
	private ArrayList<Attribute> keyAttributes;
	private ArrayList<Attribute> nonKeyAttributes;
	private ArrayList<FunctionalDependency> minimalCover;
	private ArrayList<Closure> closures;
	private ArrayList<Relation> twoNFRelations;
	private ArrayList<Relation> threeNFRelations;
	private ArrayList<Relation> bcNFRelations;
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

		StringTokenizer separate_funcDeps = new StringTokenizer(functionalDeps, ";");
		while(separate_funcDeps.hasMoreTokens()){
			this.funcDeps.add(new FunctionalDependency(this, separate_funcDeps.nextToken()));
		}
		
		this.partialFuncDeps = null;
		this.fullFuncDeps = null;
		this.essentialAttributes = null;
		this.nonEssentialAttributes = null;
		this.superKeys = null;
		this.candidate_key = null;
		this.keyAttributes = null;
		this.nonKeyAttributes = null;
		this.minimalCover = null;
		this.closures = null;
		this.twoNFRelations = null;
		this.threeNFRelations = null;
		this.bcNFRelations = null;
		this.normalForm = 1;

        this.computeClosures();
		this.computeSuperKeys();
		this.computeCandiadteKey();
		this.computeNormalForm();
		this.computeMinimalCover();
		this.separateFDs();
                //this.normalizeRelationByOneLevel();
	}

	public Relation(Set<Attribute> attributes, ArrayList<Attribute> fdLeftSide, ArrayList<Attribute> fdRightSide){
		this.noOfAttr = attributes.size();
		this.attributes = new ArrayList<Attribute>(attributes);
		Utils.sortAttributes(this.attributes);
		this.relation = this.generateRelationString(this.attributes);
		
		this.funcDeps = new ArrayList<FunctionalDependency>();
		this.funcDeps.add(new FunctionalDependency(this, fdLeftSide, fdRightSide));
		this.functionaldependencies = this.funcDeps.get(0).toString();
		
		// this.essentialAttributes = new ArrayList<Attribute>(fdLeftSide);
		// this.nonEssentialAttributes = new ArrayList<Attribute>(fdRightSide);
		// this.superKeys = new ArrayList<ArrayList<Attribute>>();
		// this.superKeys.add(new ArrayList<Attribute>(fdLeftSide));
		// this.candidate_key = new ArrayList<ArrayList<Attribute>>();
		// this.candidate_key.add(new ArrayList<Attribute>(fdLeftSide));
		// this.keyAttributes = new ArrayList<Attribute>(fdLeftSide);
		// this.nonKeyAttributes = new ArrayList<Attribute>(fdRightSide);
		// this.minimalCover = new ArrayList<FunctionalDependency>(this.funcDeps);
		// this.closures = new ArrayList<Closure>();
		// this.closures.add(Closure.computeClosure(fdLeftSide, this.funcDeps));
		// this.normalForm = 1;
		// this.computeNormalForm();

		this.essentialAttributes = null;
		this.nonEssentialAttributes = null;	
		this.superKeys = null;
		this.candidate_key = null;
		this.keyAttributes = null;
		this.nonKeyAttributes = null;
		this.minimalCover = null;
		this.closures = null;
		this.normalForm = 1;

		this.computeClosures();
		this.computeSuperKeys();
		this.computeCandiadteKey();
		this.computeNormalForm();
		this.computeMinimalCover();
	}
	public Relation(Set<Attribute> attributes, ArrayList<FunctionalDependency> funcDeps){
		this.noOfAttr = attributes.size();
		this.attributes = new ArrayList<Attribute>(attributes);
		Utils.sortAttributes(this.attributes);
		this.relation = this.generateRelationString(this.attributes);
		
		// System.out.println(this.relation);
		if(funcDeps != null){
			this.funcDeps = funcDeps;
			this.essentialAttributes = null;
			this.nonEssentialAttributes = null;	
			this.superKeys = null;
			this.candidate_key = null;
			this.keyAttributes = null;
			this.nonKeyAttributes = null;
			this.minimalCover = null;
			this.closures = null;
			this.normalForm = 1;

			this.computeClosures();
			this.computeSuperKeys();
			this.computeCandiadteKey();
			this.computeNormalForm();
			this.computeMinimalCover();
		} else {
			this.funcDeps = null;

			this.essentialAttributes = new ArrayList<Attribute>(attributes);
			this.nonEssentialAttributes = null;	
			this.superKeys = new ArrayList<ArrayList<Attribute>>();
			this.superKeys.add(essentialAttributes);
			this.candidate_key = new ArrayList<ArrayList<Attribute>>();
			this.candidate_key.add(essentialAttributes);
			this.keyAttributes = new ArrayList<Attribute>(attributes);
			this.nonKeyAttributes = null;
			this.minimalCover = null;
			this.closures = null;
			this.normalForm = 4;
		}
		
	}

	public Relation(Set<Attribute> attributes){
		this.noOfAttr = attributes.size();
		this.attributes = new ArrayList<Attribute>(attributes);
		Utils.sortAttributes(this.attributes);
		this.relation = this.generateRelationString(this.attributes);

		this.funcDeps = null;

		this.essentialAttributes = new ArrayList<Attribute>(attributes);
		this.nonEssentialAttributes = null;	
		this.superKeys = new ArrayList<ArrayList<Attribute>>();
		this.superKeys.add(essentialAttributes);
		this.candidate_key = new ArrayList<ArrayList<Attribute>>();
		this.candidate_key.add(essentialAttributes);
		this.keyAttributes = new ArrayList<Attribute>(attributes);
		this.nonKeyAttributes = null;
		this.minimalCover = null;
		this.closures = null;
		this.normalForm = 4;
	}


	///////////////////////// Class Methods /////////////////////////

	/** Computes Essential Attributes for a relation */
	private void getEssentialAttributes(ArrayList<FunctionalDependency> funcDeps){
		HashSet<Attribute> eA = new HashSet<Attribute>(this.attributes);
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
		// if(this.essentialAttributes.isEmpty()){
		// 	this.computeClosures(0, new ArrayList<Attribute>(), this.nonEssentialAttributes);	
		// }else {
		// 	this.closures.add(Closure.computeClosure(this.essentialAttributes, this.funcDeps));
		// 	this.computeClosures(0, this.essentialAttributes, this.nonEssentialAttributes);
		// }
		this.computeClosures(0, new ArrayList<Attribute>(), this.attributes);	
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
			if(f.getNormalForm()<2){
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
				if(f.getNormalForm()<3){
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
				if(f.getNormalForm()<4){
					isBCNF = false;
					break;
				}
			}
			if(isBCNF){
				this.normalForm = 4;
			}
		}
	}

	/** Computes Minimal Cover for a relation */
	public void computeMinimalCover(){
		
		// Step 1 and Step 2 : F_min = F and Making single valued dependency
		this.minimalCover = new ArrayList<FunctionalDependency>();
		ArrayList<FunctionalDependency> minimalCoverCopy = new ArrayList<FunctionalDependency>();
		for(FunctionalDependency f : this.funcDeps){
			String funcDep = f.getName();
			if(f.isMultivaluedDependency()){
				// System.out.println("	It is a multivalues FD");
				StringTokenizer st = new StringTokenizer(funcDep, "->");
				String leftAttr = st.nextToken();
				StringBuilder sb = new StringBuilder();
				sb.append(leftAttr);sb.append("->");
				for(Attribute a : f.getRightSideAttributes()){
					sb.append(a.getName());
					// System.out.println("		" + sb.toString());
					this.minimalCover.add(new FunctionalDependency(this, sb.toString()));
					minimalCoverCopy.add(new FunctionalDependency(this, sb.toString()));
					sb.deleteCharAt(sb.length()-1);
				}
			} else {
				this.minimalCover.add(new FunctionalDependency(this, funcDep));
				minimalCoverCopy.add(new FunctionalDependency(this, funcDep));
			}
		}
		Utils.sortFunctionalDependency(this.minimalCover);
		Utils.sortFunctionalDependency(minimalCoverCopy);
		// System.out.println("\n   1) Minimal Cover: After Step 1 and Step 2");
		// System.out.println("\n        Minimal Cover: " + this.minimalCover);
		// System.out.println("\n        Minimal Cover: " + minimalCoverCopy);
		
		// Step 3 : Minimizing the LHS
		int noOfFDs = minimalCoverCopy.size();
		for(int i=0; i<minimalCoverCopy.size(); i++){
			if(minimalCoverCopy.get(i).getLeftSideAttributes().size()>=2){
				FunctionalDependency g = minimalCoverCopy.get(i);
				// ArrayList<Attribute> f_left = ();
				// int noOfLeftAttr = ;
				for(int j=0; j<g.getLeftSideAttributes().size(); j++){
					FunctionalDependency f = minimalCoverCopy.remove(i);
					ArrayList<Attribute> f_left = f.getLeftSideAttributes();
					Attribute b = f_left.remove(j);
					// System.out.println("          Adding: " + f + " at " + i);
					// minimalCoverCopy.add(i, f);
					minimalCoverCopy.add(i, new FunctionalDependency(this, f.getLeftSideAttributes(), f.getRightSideAttributes()));
					// System.out.println("          " + this.minimalCover);
					// System.out.println("          " + minimalCoverCopy);
					boolean equivalent = Closure.equivalentClosures(this.minimalCover, minimalCoverCopy);
					// System.out.println("          " + minimalCoverCopy);
					// System.out.println("          Are Equivalent: " + equivalent);
					if(!equivalent){
						f_left.add(j,b);
						// System.out.println("          " + minimalCoverCopy);
						// System.out.println("          Setting: " + f + " at " + i);
						// minimalCoverCopy.remove(i);
						// System.out.println("          " + minimalCoverCopy);
						minimalCoverCopy.set(i,f);
						// minimalCoverCopy.add(i,f);
						// System.out.println("          " + this.minimalCover);
						// System.out.println("          " + minimalCoverCopy + "\n");
					}
					// f_left.add(j,b);
					// minimalCoverCopy.add(i, f);
					// if(Closure.equivalentClosures(minimalCoverCopy, this.minimalCover)){
					// }
				}
			}
		}
		// System.out.println("\n   2) Minimal Cover: After Step 3");
		// System.out.println("\n        Minimal Cover: " + this.minimalCover);
		// System.out.println("\n        Minimal Cover: " + minimalCoverCopy);

		// Step 4 : Minimizing the RHS
		for(int i=0; i<minimalCoverCopy.size(); i++){
			FunctionalDependency f = minimalCoverCopy.remove(i);
			boolean equivalent = Closure.equivalentClosures(this.minimalCover, minimalCoverCopy);
			if(!equivalent){
				minimalCoverCopy.add(i, f);
			} else {
				i--;
			}
		}
		// System.out.println("\n   3) Minimal Cover: After Step 4");
		// System.out.println("\n        Minimal Cover: " + this.minimalCover);
		// System.out.println("\n        Minimal Cover: " + minimalCoverCopy + "\n");
		Utils.sortFunctionalDependency(minimalCoverCopy);
		for(int i=0; i<minimalCoverCopy.size(); i++){
			FunctionalDependency f = minimalCoverCopy.get(i);
			// System.out.println("        For: " + f);
			for(int j=0; j<minimalCoverCopy.size(); j++){
				FunctionalDependency g = minimalCoverCopy.get(j);
				// System.out.println("            Checking: " + g);
				if(!f.equals(g) && f.getLeftSideAttributes().equals(g.getLeftSideAttributes())){
					for(Attribute a : g.getRightSideAttributes()){
						if(!f.getRightSideAttributes().contains(a)){
							f.getRightSideAttributes().add(a);
						}
					}
					// System.out.println("            Removing " + g);
					minimalCoverCopy.remove(g);
				}
				
			}
		}

		this.minimalCover.clear();
		Utils.copyFunctionalDependencies(minimalCoverCopy, this.minimalCover);
		minimalCoverCopy.clear();
		// System.out.println("\n\n        Final Minimal Cover: " + this.minimalCover);
	}

	public void separateFDs(){
		// System.out.println("Separating FDs:");
		// this.partialFuncDeps = new ArrayList<FunctionalDependency>();
		// this.fullFuncDeps = new ArrayList<FunctionalDependency>();
		Set<FunctionalDependency> partialFuncDeps = new HashSet<FunctionalDependency>();
		Set<FunctionalDependency> fullFuncDepsSet = new HashSet<FunctionalDependency>();
		CopyOnWriteArrayList<FunctionalDependency> fullFuncDeps = new CopyOnWriteArrayList<FunctionalDependency>();
		Utils.generateFunctionalDependencies(this.minimalCover, fullFuncDeps);
		Set<FunctionalDependency> transferBin = new HashSet<FunctionalDependency>();

		Iterator<FunctionalDependency> itr1 = fullFuncDeps.iterator();
		while(itr1.hasNext()){
			// System.out.println("	Starting:");
			// System.out.println("		" + fullFuncDeps);
			// System.out.println("		" + fullFuncDepsSet);
			// System.out.println("		" + partialFuncDeps);
			FunctionalDependency fd = itr1.next();
			// System.out.println("		fd = " + fd);
			if(fd.getNormalForm() < 2){
				partialFuncDeps.add(fd);
				fullFuncDeps.remove(fd);
				// transferBin.add(fd);

				Utils.sortAttributes(fd.getRightSideAttributes());
				Closure c = Closure.computeClosure(fd.getRightSideAttributes(), this.funcDeps);
				Iterator<FunctionalDependency> itr2 = fullFuncDeps.iterator();
				while(itr2.hasNext()){
					FunctionalDependency f = itr2.next();
					if(!f.equals(fd) && ( c.getRightSide().containsAll(f.getLeftSideAttributes()) )){
						partialFuncDeps.add(fd);
						fullFuncDeps.remove(fd);
						// transferBin.add(f);
					}
				}
			} 
			// else {
			// 	if(!transferBin.contains(fd)){
			// 		this.fullFuncDeps.add(fd);
			// 	}
			// }
			
			// fullFuncDeps.removeAll(transferBin);
			
			// transferBin.clear();
			// System.out.println("		" + this.fullFuncDeps);
			// System.out.println("		" + this.partialFuncDeps);
		}
		this.partialFuncDeps = new ArrayList<FunctionalDependency>();
		this.fullFuncDeps = new ArrayList<FunctionalDependency>();
		this.fullFuncDeps.addAll(fullFuncDeps);
		this.partialFuncDeps.addAll(partialFuncDeps);
		Utils.sortFunctionalDependency(this.fullFuncDeps);
		Utils.sortFunctionalDependency(this.partialFuncDeps);
		// System.out.println("	Ending:");
		// System.out.println("		" + this.fullFuncDeps);
		// System.out.println("		" + this.partialFuncDeps);
	}


	public void normalizeRelationByOneLevel(){
		if(!(this.normalForm >= 2)){
			this.decomposeInto2NFRelations();
		} else if(!(this.normalForm >= 3)){
			this.decomposeInto3NFRelations();
		} else if(!(this.normalForm >= 4)){
			this.decomposeIntoBCNFRelations();
		}
	}

	public void normalizeRelation(){
		if(!(this.normalForm >= 2)){
			this.decomposeInto2NFRelations();
			if(this.twoNFRelations != null){
				for(Relation r : this.twoNFRelations){
					// ArrayList<FunctionalDependency> f = r.getFunctionalDependencies();
					System.out.println(r + " with FDs " + r.getFunctionalDependencies() + " in the " + r.getNormalForm() + "NF" + " with Candidate Keys " + r.getCandidateKeys());
				}
				System.out.println("");	
			}

			this.decompose2NFInto3NFRelations();
			if(this.threeNFRelations != null){
				for(Relation r : this.threeNFRelations){
					// ArrayList<FunctionalDependency> f = r.getFunctionalDependencies();
					System.out.println(r + " with FDs " + r.getFunctionalDependencies() + " in the " + r.getNormalForm() + "NF" + " with Candidate Keys " + r.getCandidateKeys());
				}
				System.out.println("");	
			}

			this.decompose3NFIntoBCNFRelations();
			if(this.bcNFRelations != null){
				for(Relation r : this.bcNFRelations){
					// ArrayList<FunctionalDependency> f = r.getFunctionalDependencies();
					System.out.println(r + " with FDs " + r.getFunctionalDependencies() + " in the " + r.getNormalForm() + "NF" + " with Candidate Keys " + r.getCandidateKeys());
				}
				System.out.println("");	
			}
		}else if(!(this.normalForm >= 3)){
			this.decomposeInto3NFRelations();
			if(this.threeNFRelations != null){
				for(Relation r : this.threeNFRelations){
					// ArrayList<FunctionalDependency> f = r.getFunctionalDependencies();
					System.out.println(r + " with FDs " + r.getFunctionalDependencies() + " in the " + r.getNormalForm() + "NF" + " with Candidate Keys " + r.getCandidateKeys());
				}
				System.out.println("");	
			}

			this.decompose3NFIntoBCNFRelations();
			if(this.bcNFRelations != null){
				for(Relation r : this.bcNFRelations){
					// ArrayList<FunctionalDependency> f = r.getFunctionalDependencies();
					System.out.println(r + " with FDs " + r.getFunctionalDependencies() + " in the " + r.getNormalForm() + "NF" + " with Candidate Keys " + r.getCandidateKeys());
				}
				System.out.println("");	
			}
		}else if(!(this.normalForm >= 4)){
			this.decomposeIntoBCNFRelations();
			if(this.bcNFRelations != null){
				for(Relation r : this.bcNFRelations){
					// ArrayList<FunctionalDependency> f = r.getFunctionalDependencies();
					System.out.println(r + " with FDs " + r.getFunctionalDependencies() + " in the " + r.getNormalForm() + "NF" + " with Candidate Keys " + r.getCandidateKeys());
				}
				System.out.println("");	
			}
		}
	}

	public void decomposeInto2NFRelations(){
		System.out.println("\n Decomposing to 2NF Relations: ");
		if(!(this.normalForm >= 2)){
			this.twoNFRelations = Decompositions.decomposeInto2NFScheme(this);
		}
	}

	public void decompose2NFInto3NFRelations(){
		System.out.println("\n Decomposing to 3NF Relations: ");
		if(this.twoNFRelations != null){
			this.threeNFRelations = new ArrayList<Relation>();
			for(Relation r : this.twoNFRelations){
				int relationNormalForm = r.getNormalForm();
				if(!(relationNormalForm >= 3)){
					// System.out.println(r);
					ArrayList<Relation> tempDecomposiiton = Decompositions.decomposeInto3NFScheme(r);
					this.threeNFRelations.addAll(tempDecomposiiton);
				} else {
					this.threeNFRelations.add(r);
				}
			}
		}
	}


	public void decomposeInto3NFRelations(){
		System.out.println("\n Decomposing to 3NF Relations: ");
		if(!(this.normalForm >= 3)){
			this.threeNFRelations = Decompositions.decomposeInto3NFScheme(this);
		}
	}

	public void decompose3NFIntoBCNFRelations(){
		System.out.println("\n Decomposing to BCNF Relations: ");
		if(this.threeNFRelations != null){
			this.bcNFRelations = new ArrayList<Relation>();
			for(Relation r : this.threeNFRelations){
				int relationNormalForm = r.getNormalForm();
				if(!(relationNormalForm >= 4)){
					// System.out.println(r);
					ArrayList<Relation> tempDecomposiiton = Decompositions.decomposeIntoBCNFScheme(r);
					this.bcNFRelations.addAll(tempDecomposiiton);
				} else {
					this.bcNFRelations.add(r);
				}
			}
		}
	}

	public void decomposeIntoBCNFRelations(){
		System.out.println("\n Decomposing to BCNF Relations: ");
		if(!(this.normalForm >= 4)){
			this.bcNFRelations = Decompositions.decomposeIntoBCNFScheme(this);
		}
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

	public void printNormalForm(){
		switch(this.normalForm){
			case 1 :{
				System.out.print("Relation is in 1NF\n");
				break;
			}
			case 2 :{
				System.out.print("Relation is in 2NF\n");
				break;
			}
			case 3 :{
				System.out.print("Relation is in 3NF\n");
				break;
			}
			case 4 :{
				System.out.print("Relation is in BCNF\n");
				break;
			}
		}
	}

	public void printNormalFormsTable(){
                System.out.println(this.normalFormsTableGen());
		
	}
        
        public Object[][] generateNFTable(){
            String[][] output = new String[this.funcDeps.size()][5];
            int i = 0;
            for(FunctionalDependency f : this.funcDeps){
                output[i][0] = f.getName();
                int nf = f.getNormalForm();
                if(nf == 1){
                    output[i][1] = "Y";
                    output[i][2] = "N";
                    output[i][3] = "N";
                    output[i][4] = "N";
                } else if(nf == 2){
                    output[i][1] = "Y";
                    output[i][2] = "Y";
                    output[i][3] = "N";
                    output[i][4] = "N";
                } else if(nf == 3){
                    output[i][1] = "Y";
                    output[i][2] = "Y";
                    output[i][3] = "Y";
                    output[i][4] = "N";
                } else if(nf == 4){
                    output[i][1] = "Y";
                    output[i][2] = "Y";
                    output[i][3] = "Y";
                    output[i][4] = "Y";
                } else {
                    output[i][1] = "*";
                    output[i][2] = "*";
                    output[i][3] = "*";
                    output[i][4] = "*";
                }
                ++i;
            }
            return output;
        }

        public String normalFormsTableGen(){
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
			return s.toString();
        }

	public void printMinimalCover(){
		if(this.minimalCover != null){
			StringBuilder s = new StringBuilder();
			s.append("{ ");
			for(FunctionalDependency f : this.minimalCover){
				s.append(f.toString() + ", ");
			}
			s.deleteCharAt(s.toString().length() - 1);
			s.deleteCharAt(s.toString().length() - 1);
			s.append(" }");
			System.out.println(s.toString());
			return ;
		}
		System.out.println("Not yet computed :(");
	}

	public void print2NFRelations(){
		for(Relation r : this.twoNFRelations){
			System.out.println("	" + r);
		}
		System.out.println("");
	}

	public void print3NFRelations(){
		for(Relation r : this.threeNFRelations){
			System.out.println("	" + r);
		}
		System.out.println("");
	}

	public String getDecomposedRelationString(){
		ArrayList<Relation> decomposedRelations = this.getDecomposition();
		if(decomposedRelations == null){
			return "Relations already in BCNF";
		} else {
			StringBuilder s = new StringBuilder();
			int i=0;
			for(Relation r : decomposedRelations){
				s.append(i + ") " + r.toString() + "<br />");
			}
			return "<html>" + s.toString() + "<html/>";
		}
	}
        
	public String toString(){
		return this.relation;
	}

	private String generateRelationString(ArrayList<Attribute> attributes){
		StringBuilder s = new StringBuilder();
		s.append("R(");
		ArrayList<Attribute> attr = new ArrayList<Attribute>(attributes);
		Utils.sortAttributes(attr);
		for(Attribute a : attr){
			s.append(a.getName() + ",");
		}
		s.deleteCharAt(s.toString().length() - 1);
		s.append(")");
		return s.toString();
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
	public ArrayList<FunctionalDependency> getFunctionalDependencies(){
		return this.funcDeps;
	}
	public ArrayList<FunctionalDependency> getFullFunctionalDependencies(){
		return this.fullFuncDeps;
	}
	public ArrayList<FunctionalDependency> getPartialFunctionalDependencies(){
		return this.partialFuncDeps;
	}
    public ArrayList<Attribute> getEssentialAttributes(){
		return this.essentialAttributes;
	}
    public ArrayList<Attribute> getNonEssentialAttributes(){
		return this.nonEssentialAttributes;
	}
    public ArrayList<Attribute> getKeyAttributes(){
		return this.keyAttributes;
        }
    public ArrayList<Attribute> getNonKeyAttributes(){
		return this.nonKeyAttributes;
        }
	public ArrayList<ArrayList<Attribute>> getKeys(){
		return this.superKeys;
	}
    public ArrayList<ArrayList<Attribute>> getCandidateKeys(){
		return this.candidate_key;
	}
	public int getNormalForm(){
		return this.normalForm;
	}
	public ArrayList<Closure> getClosures(){
		if(this.closures != null){
			return this.closures;
		}
		return null;
	}
	public ArrayList<FunctionalDependency> getMinimalCover(){
		return this.minimalCover;
	}
	public ArrayList<Relation> get2NFRelations(){
		return this.twoNFRelations;
	}
	public ArrayList<Relation> get3NFRelations(){
		return this.threeNFRelations;
	}
	public ArrayList<Relation> getBCNFRelations(){
		return this.bcNFRelations;
	}
	public ArrayList<Relation> getDecomposition(){
		if(!(this.normalForm >= 2)){
			return this.twoNFRelations;
		}
		if(!(this.normalForm >= 3)){
			return this.threeNFRelations;
		}
		if(!(this.normalForm >= 4)){
			return this.bcNFRelations;
		}
		return null;
	}
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////// OLD ONE ///////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


// package RelationalDatabase;

// import java.util.*;

// public class Relation {
// 	///////////////////////// Class Members /////////////////////////
// 	private String relation;
// 	private String functionaldependencies;
// 	private int noOfAttr;
// 	private ArrayList<Attribute> attributes;
// 	private ArrayList<FunctionalDependency> funcDeps;
// 	private ArrayList<Attribute> essentialAttributes;
// 	private ArrayList<Attribute> nonEssentialAttributes;
// 	private ArrayList<ArrayList<Attribute>> superKeys;
// 	private ArrayList<ArrayList<Attribute>> candidate_key;
// 	private ArrayList<Attribute> keyAttributes;
// 	private ArrayList<Attribute> nonKeyAttributes;
// 	private ArrayList<FunctionalDependency> minimalCover;
// 	private ArrayList<Closure> closures;
// 	private ArrayList<Relation> twoNFRelations;
// 	private ArrayList<Relation> threeNFRelations;
// 	private ArrayList<Relation> bcNFRelations;
// 	private int normalForm;

// 	///////////////////////// Class Constructors /////////////////////////
// 	// relation is of the form ** R(A,B,C,D) **
// 	// funcDeps is of the form ** A,B->D;A,B->C;B->E **
// 	public Relation(String relation, int noOfAttr, String functionalDeps) {
// 		this.noOfAttr = noOfAttr;
// 		this.relation = relation;
// 		this.functionaldependencies = functionalDeps;
// 		this.attributes = new ArrayList<Attribute>();
// 		this.funcDeps = new ArrayList<FunctionalDependency>();

// 		StringTokenizer separate_attributes = new StringTokenizer(relation.substring(2, relation.length() - 1), ",");
// 		while(separate_attributes.hasMoreTokens()){
// 			attributes.add(new Attribute(separate_attributes.nextToken()));
// 		}

// 		StringTokenizer separate_funcDeps = new StringTokenizer(functionalDeps, ";");
// 		while(separate_funcDeps.hasMoreTokens()){
// 			this.funcDeps.add(new FunctionalDependency(this, separate_funcDeps.nextToken()));
// 		}
		
// 		this.essentialAttributes = null;
// 		this.nonEssentialAttributes = null;
// 		this.superKeys = null;
// 		this.candidate_key = null;
// 		this.keyAttributes = null;
// 		this.nonKeyAttributes = null;
// 		this.minimalCover = null;
// 		this.closures = null;
// 		this.twoNFRelations = null;
// 		this.threeNFRelations = null;
// 		this.bcNFRelations = null;
// 		this.normalForm = 1;
// 	}
// 	public Relation(Set<Attribute> attributes, ArrayList<Attribute> fdLeftSide, ArrayList<Attribute> fdRightSide){
// 		this.noOfAttr = attributes.size();
// 		this.attributes = new ArrayList<Attribute>(attributes);
// 		Utils.sortAttributes(this.attributes);
// 		this.relation = this.generateRelationString(this.attributes);
		
// 		this.funcDeps = new ArrayList<FunctionalDependency>();
// 		this.funcDeps.add(new FunctionalDependency(this, fdLeftSide, fdRightSide));
// 		this.functionaldependencies = this.funcDeps.get(0).toString();
		
// 		// this.essentialAttributes = new ArrayList<Attribute>(fdLeftSide);
// 		// this.nonEssentialAttributes = new ArrayList<Attribute>(fdRightSide);
// 		// this.superKeys = new ArrayList<ArrayList<Attribute>>();
// 		// this.superKeys.add(new ArrayList<Attribute>(fdLeftSide));
// 		// this.candidate_key = new ArrayList<ArrayList<Attribute>>();
// 		// this.candidate_key.add(new ArrayList<Attribute>(fdLeftSide));
// 		// this.keyAttributes = new ArrayList<Attribute>(fdLeftSide);
// 		// this.nonKeyAttributes = new ArrayList<Attribute>(fdRightSide);
// 		// this.minimalCover = new ArrayList<FunctionalDependency>(this.funcDeps);
// 		// this.closures = new ArrayList<Closure>();
// 		// this.closures.add(Closure.computeClosure(fdLeftSide, this.funcDeps));
// 		// this.normalForm = 1;
// 		// this.computeNormalForm();

// 		this.essentialAttributes = null;
// 		this.nonEssentialAttributes = null;	
// 		this.superKeys = null;
// 		this.candidate_key = null;
// 		this.keyAttributes = null;
// 		this.nonKeyAttributes = null;
// 		this.minimalCover = null;
// 		this.closures = null;
// 		this.normalForm = 1;

// 		this.computeClosures();
// 		this.computeSuperKeys();
// 		this.computeCandiadteKey();
// 		this.computeNormalForm();
// 		this.computeMinimalCover();
// 	}
// 	public Relation(Set<Attribute> attributes, ArrayList<FunctionalDependency> funcDeps){
// 		this.noOfAttr = attributes.size();
// 		this.attributes = new ArrayList<Attribute>(attributes);
// 		Utils.sortAttributes(this.attributes);
// 		this.relation = this.generateRelationString(this.attributes);
		
// 		// System.out.println(this.relation);

// 		this.funcDeps = funcDeps;
		
// 		this.essentialAttributes = null;
// 		this.nonEssentialAttributes = null;	
// 		this.superKeys = null;
// 		this.candidate_key = null;
// 		this.keyAttributes = null;
// 		this.nonKeyAttributes = null;
// 		this.minimalCover = null;
// 		this.closures = null;
// 		this.normalForm = 1;

// 		this.computeClosures();
// 		this.computeSuperKeys();
// 		this.computeCandiadteKey();
// 		this.computeNormalForm();
// 		this.computeMinimalCover();
// 	}	


// 	///////////////////////// Class Methods /////////////////////////

// 	/** Computes Essential Attributes for a relation */
// 	private void getEssentialAttributes(ArrayList<FunctionalDependency> funcDeps){
// 		HashSet<Attribute> eA = new HashSet<Attribute>();
// 		HashSet<Attribute> nEA = new HashSet<Attribute>(this.attributes);
// 		Set<Attribute> S = new HashSet<Attribute>();
// 		for(FunctionalDependency f : funcDeps){
// 			eA.addAll(f.getLeftSideAttributes());
// 			S.addAll(f.getRightSideAttributes());
// 			eA.addAll(f.getRightSideAttributes());
// 		}
// 		eA.removeAll(S);
// 		nEA.removeAll(eA);
// 		this.essentialAttributes = new ArrayList<Attribute>(eA);
// 		Utils.sortAttributes(this.essentialAttributes);
// 		this.nonEssentialAttributes = new ArrayList<Attribute>(nEA);
// 		Utils.sortAttributes(this.nonEssentialAttributes);
// 	}

// 	/** Computes Closures of all Attributes combinantions 
// 		that has the essential attributes for a relation */
// 	public void computeClosures(){	// Driver Method
// 		this.closures = new ArrayList<Closure>();
// 		this.getEssentialAttributes(funcDeps);
// 		this.closures.add(Closure.computeClosure(this.essentialAttributes, this.funcDeps));
// 		this.computeClosures(0, this.essentialAttributes, this.nonEssentialAttributes);
// 	}
// 	// Actual Method
// 	public void computeClosures(int index, ArrayList<Attribute> essentialAttributeList, ArrayList<Attribute> nonEssentialAttributeList){
// 		for(int i=index; i < nonEssentialAttributeList.size(); i++){
// 			essentialAttributeList.add(nonEssentialAttributeList.get(i));
// 			Closure computedClosure = Closure.computeClosure(essentialAttributeList, this.funcDeps);
// 			if(computedClosure != null){
// 				this.closures.add(computedClosure);
// 			}
// 			if(i < this.noOfAttr){
// 				this.computeClosures(i+1, essentialAttributeList, nonEssentialAttributeList);
// 			}
// 			essentialAttributeList.remove(essentialAttributeList.size() - 1);
// 		}
// 		Utils.sortClosure(this.closures);
// 	}

// 	/** Computes Super Keys for a relation */
// 	public void computeSuperKeys(){
// 		this.superKeys = new ArrayList<ArrayList<Attribute>>();
// 		for(Closure c : closures){
// 			if(c.getRightSide().equals(attributes)){
// 				superKeys.add(c.getLeftSide());
// 			}
// 		}
// 		for(ArrayList<Attribute> key : this.superKeys){
// 			Utils.sortAttributes(key);
// 		}
// 	}
	
// 	/** Computes Canidate Keys for a relation */
// 	public void computeCandiadteKey(){
// 		int len = this.getMinimalSize();
// 		this.candidate_key = new ArrayList<ArrayList<Attribute>>();
// 		HashSet<Attribute> kA = new HashSet<Attribute>();
// 		HashSet<Attribute> nKA = new HashSet<Attribute>(this.attributes);
// 		for(ArrayList<Attribute> k : superKeys){
// 			if(k.size() == len){
// 				candidate_key.add(k);
// 				kA.addAll(k);
// 			}
// 		}
// 		this.keyAttributes = new ArrayList<Attribute>(kA);
// 		Utils.sortAttributes(this.keyAttributes);
// 		nKA.removeAll(kA);
// 		this.nonKeyAttributes  = new ArrayList<Attribute>(nKA);
// 		Utils.sortAttributes(this.nonKeyAttributes);
// 	}
// 	private int getMinimalSize(){
// 		int size = this.superKeys.size();
// 		int min = this.superKeys.get(0).size();
// 		for(int i=1; i<size; i++){
// 			if(this.superKeys.get(i).size() <= min){
// 				min = this.superKeys.get(i).size();
// 			}
// 		}
// 		return min;
// 	}

// 	/** Gets Primary Key for a relation */
// 	public ArrayList<Attribute> getPrimaryKey(){
// 		return this.candidate_key.get(0);
// 	}

// 	/** Computes Normal of the relation for a relation */
// 	public void computeNormalForm(){
// 		for(FunctionalDependency f : this.funcDeps){
// 			f.computeNormalForm(this.keyAttributes, this.nonKeyAttributes, this.candidate_key);
// 		}
// 		boolean is2NF = true;
// 		for(FunctionalDependency f : this.funcDeps){
// 			if(f.getNormalForm()<2){
// 				is2NF = false;
// 				break;
// 			}
// 		}
// 		if(is2NF){
// 			this.normalForm = 2;
// 		}
// 		if(this.normalForm == 2){
// 			boolean is3NF = true;
// 			for(FunctionalDependency f : this.funcDeps){
// 				if(f.getNormalForm()<3){
// 					is3NF = false;
// 					break;
// 				}
// 			}
// 			if(is3NF){
// 				this.normalForm = 3;
// 			}
// 		}
// 		if(this.normalForm == 3){
// 			boolean isBCNF = true;
// 			for(FunctionalDependency f : this.funcDeps){
// 				if(f.getNormalForm()<4){
// 					isBCNF = false;
// 					break;
// 				}
// 			}
// 			if(isBCNF){
// 				this.normalForm = 4;
// 			}
// 		}
// 	}

// 	/** Computes Minimal Cover for a relation */
// 	public void computeMinimalCover(){
// 		// Step 1 and Step 2 : F_min = F and Making single valued dependency
// 		this.minimalCover = new ArrayList<FunctionalDependency>();
// 		ArrayList<FunctionalDependency> minimalCoverCopy = new ArrayList<FunctionalDependency>();
// 		for(FunctionalDependency f : this.funcDeps){
// 			String funcDep = f.getName();
// 			if(f.isMultivaluedDependency()){
// 				// System.out.println("	It is a multivalues FD");
// 				StringTokenizer st = new StringTokenizer(funcDep, "->");
// 				String leftAttr = st.nextToken();
// 				StringBuilder sb = new StringBuilder();
// 				sb.append(leftAttr);sb.append("->");
// 				for(Attribute a : f.getRightSideAttributes()){
// 					sb.append(a.getName());
// 					// System.out.println("		" + sb.toString());
// 					this.minimalCover.add(new FunctionalDependency(this, sb.toString()));
// 					minimalCoverCopy.add(new FunctionalDependency(this, sb.toString()));
// 					sb.deleteCharAt(sb.length()-1);
// 				}
// 			} else {
// 				this.minimalCover.add(new FunctionalDependency(this, funcDep));
// 				minimalCoverCopy.add(new FunctionalDependency(this, funcDep));
// 			}
// 		}
// 		Utils.sortFunctionalDependency(this.minimalCover);
// 		Utils.sortFunctionalDependency(minimalCoverCopy);
// 		// System.out.println("\n   1) Minimal Cover: After Step 1 and Step 2");
// 		// System.out.println("\n        Minimal Cover: " + this.minimalCover);
// 		// System.out.println("\n        Minimal Cover: " + minimalCoverCopy);
		
// 		// Step 3 : Minimizing the LHS
// 		int noOfFDs = minimalCoverCopy.size();
// 		for(int i=0; i<minimalCoverCopy.size(); i++){
// 			if(minimalCoverCopy.get(i).getLeftSideAttributes().size()>=2){
// 				FunctionalDependency g = minimalCoverCopy.get(i);
// 				// ArrayList<Attribute> f_left = ();
// 				// int noOfLeftAttr = ;
// 				for(int j=0; j<g.getLeftSideAttributes().size(); j++){
// 					FunctionalDependency f = minimalCoverCopy.remove(i);
// 					ArrayList<Attribute> f_left = f.getLeftSideAttributes();
// 					Attribute b = f_left.remove(j);
// 					// System.out.println("          Adding: " + f + " at " + i);
// 					// minimalCoverCopy.add(i, f);
// 					minimalCoverCopy.add(i, new FunctionalDependency(this, f.getLeftSideAttributes(), f.getRightSideAttributes()));
// 					// System.out.println("          " + this.minimalCover);
// 					// System.out.println("          " + minimalCoverCopy);
// 					boolean equivalent = Closure.equivalentClosures(this.minimalCover, minimalCoverCopy);
// 					// System.out.println("          " + minimalCoverCopy);
// 					// System.out.println("          Are Equivalent: " + equivalent);
// 					if(!equivalent){
// 						f_left.add(j,b);
// 						// System.out.println("          " + minimalCoverCopy);
// 						// System.out.println("          Setting: " + f + " at " + i);
// 						// minimalCoverCopy.remove(i);
// 						// System.out.println("          " + minimalCoverCopy);
// 						minimalCoverCopy.set(i,f);
// 						// minimalCoverCopy.add(i,f);
// 						// System.out.println("          " + this.minimalCover);
// 						// System.out.println("          " + minimalCoverCopy + "\n");
// 					}
// 					// f_left.add(j,b);
// 					// minimalCoverCopy.add(i, f);
// 					// if(Closure.equivalentClosures(minimalCoverCopy, this.minimalCover)){
// 					// }
// 				}
// 			}
// 		}
// 		// System.out.println("\n   2) Minimal Cover: After Step 3");
// 		// System.out.println("\n        Minimal Cover: " + this.minimalCover);
// 		// System.out.println("\n        Minimal Cover: " + minimalCoverCopy);

// 		// Step 4 : Minimizing the RHS
// 		for(int i=0; i<minimalCoverCopy.size(); i++){
// 			FunctionalDependency f = minimalCoverCopy.remove(i);
// 			boolean equivalent = Closure.equivalentClosures(this.minimalCover, minimalCoverCopy);
// 			if(!equivalent){
// 				minimalCoverCopy.add(i, f);
// 			}
// 		}
// 		// System.out.println("\n   3) Minimal Cover: After Step 4");
// 		// System.out.println("\n        Minimal Cover: " + this.minimalCover);
// 		// System.out.println("\n        Minimal Cover: " + minimalCoverCopy + "\n");
// 		Utils.sortFunctionalDependency(minimalCoverCopy);
// 		for(int i=0; i<minimalCoverCopy.size(); i++){
// 			FunctionalDependency f = minimalCoverCopy.get(i);
// 			// System.out.println("        For: " + f);
// 			for(int j=0; j<minimalCoverCopy.size(); j++){
// 				FunctionalDependency g = minimalCoverCopy.get(j);
// 				// System.out.println("            Checking: " + g);
// 				if(!f.equals(g) && f.getLeftSideAttributes().equals(g.getLeftSideAttributes())){
// 					for(Attribute a : g.getRightSideAttributes()){
// 						if(!f.getRightSideAttributes().contains(a)){
// 							f.getRightSideAttributes().add(a);
// 						}
// 					}
// 					// System.out.println("            Removing " + g);
// 					minimalCoverCopy.remove(g);
// 				}
				
// 			}
// 		}

// 		this.minimalCover.clear();
// 		Utils.copyFunctionalDependencies(minimalCoverCopy, this.minimalCover);
// 		minimalCoverCopy.clear();
// 		// System.out.println("\n\n        Final Minimal Cover: " + this.minimalCover);
// 	}

// 	// public void c_computeMinimalCover(){
// 	// 	this.minimalCover = new ArrayList<FunctionalDependency>();
// 	// 	for(FunctionalDependency f : this.funcDeps){
// 	// 		// System.out.println("For " + f + " :");
// 	// 		if(f.isMultivaluedDependency()){
// 	// 			// System.out.println("	It is a multivalues FD");
// 	// 			String funcDep = f.getName();
// 	// 			StringTokenizer st = new StringTokenizer(funcDep, "->");
// 	// 			String leftAttr = st.nextToken();
// 	// 			StringBuilder sb = new StringBuilder();
// 	// 			sb.append(leftAttr);sb.append("->");
// 	// 			for(Attribute a : f.getRightSideAttributes()){
// 	// 				sb.append(a.getName());
// 	// 				// System.out.println("		" + sb.toString());
// 	// 				this.minimalCover.add(new FunctionalDependency(this, sb.toString()));
// 	// 				sb.deleteCharAt(sb.length()-1);
// 	// 			}
// 	// 		} else {
// 	// 			this.minimalCover.add(f);
// 	// 		}
// 	// 	}
// 	// 	ArrayList<Closure> minimalCoverClosure = Closure.computeClosure(this.minimalCover); // Computing Minimal Cover Closure
// 	// 	System.out.println("  1) Closure of Minimal Cover: ");
// 	// 	for(Closure c : minimalCoverClosure){
// 	// 		System.out.println("      " + c);
// 	// 	}
// 	// 	ArrayList<FunctionalDependency> minimalCoverCopy = new ArrayList<FunctionalDependency>();
// 	// 	Utils.copyFunctionalDependencies(this.minimalCover, minimalCoverCopy);
// 	// 	System.out.println("  2) Minimal Cover After Step 2: (minimalCoverCopy)");
// 	// 	System.out.println("      " + minimalCoverCopy);
// 	// 	System.out.println("\n  3) Starting Step 3: ");
// 	// 	for(int i=0; i<minimalCoverCopy.size(); i++){
// 	// 		FunctionalDependency tempFD = minimalCoverCopy.get(i);
// 	// 		ArrayList<Attribute> tempFDLeft = tempFD.getLeftSideAttributes();
// 	// 		if(tempFDLeft.size() >= 2){
// 	// 			for(int j=0; j<tempFDLeft.size(); j++){
// 	// 				Attribute tempA = tempFDLeft.get(j);
// 	// 				tempFDLeft.remove(tempA);
// 	// 				// If Equivalent
// 	// 				System.out.print("    -> Minimal Cover Copy: ");
// 	// 				for(FunctionalDependency f : minimalCoverCopy){
// 	// 					System.out.print(f + "  ");
// 	// 				}System.out.println("");
// 	// 				ArrayList<Closure> minimalCoverCopyClosure = Closure.computeClosure(minimalCoverCopy);
// 	// 				System.out.println("    -> Closure of Minimal Cover Copy: ");
// 	// 				for(Closure c : minimalCoverCopyClosure){
// 	// 					System.out.println("       " + c);
// 	// 				}
// 	// 				boolean equivalent = minimalCoverClosure.equals(minimalCoverCopyClosure);// || minimalCoverClosure.containsAll(minimalCoverCopyClosure) || minimalCoverCopyClosure.containsAll(minimalCoverClosure);
// 	// 				// boolean equivalent = Closure.equivalentClosures(this.minimalCover, minimalCoverCopy);
// 	// 				System.out.println("    -> Are they Equivalent: " + equivalent + "\n");
// 	// 				if(equivalent){
// 	// 					break;
// 	// 				} else {
// 	// 					tempFDLeft.add(j, tempA);
// 	// 				}
// 	// 			}
// 	// 		}
// 	// 	}
// 	// 	System.out.println("    Minimal Cover After Step 3: (minimalCoverCopy)");
// 	// 	System.out.println("      " + minimalCoverCopy);
// 	// 	System.out.println("\n  4) Starting Step 4: ");
// 	// 	System.out.println("    -> Minimal Cover Copy: " + minimalCoverCopy);
// 	// 	for(int i=0; i<minimalCoverCopy.size(); i++){
// 	// 		FunctionalDependency tempFD = minimalCoverCopy.get(i);
// 	// 		System.out.println("\n    -> Removing: " + tempFD);
// 	// 		minimalCoverCopy.remove(tempFD);
// 	// 		ArrayList<Closure> minimalCoverCopyClosure = Closure.computeClosure(minimalCoverCopy);
// 	// 		System.out.println("    -> Closure of Minimal Cover Copy: ");
// 	// 		for(Closure c : minimalCoverCopyClosure){
// 	// 			System.out.println("       " + c);
// 	// 		}
// 	// 		boolean equivalent = minimalCoverClosure.equals(minimalCoverCopyClosure);// || minimalCoverClosure.containsAll(minimalCoverCopyClosure) || minimalCoverCopyClosure.containsAll(minimalCoverClosure);
// 	// 		// boolean equivalent = Closure.equivalentClosures(this.minimalCover, minimalCoverCopy);
// 	// 		if(!equivalent){
// 	// 			minimalCoverCopy.add(i, tempFD);
// 	// 		}
// 	// 	}
// 	// 	System.out.println("\n    Minimal Cover After Step 4: (minimalCoverCopy)");
// 	// 	System.out.println("      " + minimalCoverCopy);
// 	// }

// 	public ArrayList<Relation> normalizeRelationByOneLevel(){
// 		if(!(this.normalForm >= 2)){
// 			this.decomposeInto2NFRelations();
// 			return this.twoNFRelations;
// 		}
// 		if(!(this.normalForm >= 3)){
// 			this.decomposeInto3NFRelations();
// 			return this.threeNFRelations;
// 		}
// 		if(!(this.normalForm >= 4)){
// 			this.decomposeIntoBCNFRelations();
// 			return this.bcNFRelations;
// 		}
// 		return null;
// 	}

// 	public void normalizeRelation(){
// 		if(!(this.normalForm >= 2)){
// 			this.decomposeInto2NFRelations();
// 			this.decompose2NFInto3NFRelations();
// 			this.decompose3NFIntoBCNFRelations();
// 		}
// 		if(!(this.normalForm >= 3)){
// 			this.decomposeInto3NFRelations();
// 			this.decompose3NFIntoBCNFRelations();
// 		}
// 		if(!(this.normalForm >= 4)){
// 			this.decomposeIntoBCNFRelations();
// 		}
// 	}

// 	public void decomposeInto2NFRelations(){
// 		// System.out.println("\n Decomposing to 2NF Relations: ");
// 		if(!(this.normalForm >= 2)){
// 			this.twoNFRelations = Decompositions.decomposeInto2NFScheme(this);
// 		}
// 	}

// 	public void decompose2NFInto3NFRelations(){
// 		System.out.println("\n Decomposing to 3NF Relations: ");
// 		if(this.twoNFRelations != null){
// 			this.threeNFRelations = new ArrayList<Relation>();
// 			for(Relation r : this.twoNFRelations){
// 				int relationNormalForm = r.getNormalForm();
// 				if(!(relationNormalForm >= 3)){
// 					System.out.println(r);
// 					ArrayList<Relation> tempDecomposiiton = Decompositions.decomposeInto3NFScheme(r);
// 					this.threeNFRelations.addAll(tempDecomposiiton);
// 				} else {
// 					this.threeNFRelations.add(r);
// 				}
// 			}
// 		}
// 	}


// 	public void decomposeInto3NFRelations(){
// 		if(!(this.normalForm >= 3)){
// 			this.threeNFRelations = Decompositions.decomposeInto3NFScheme(this);
// 		}
// 	}

// 	public void decompose3NFIntoBCNFRelations(){
// 		// System.out.println("Need to add the For looping method for 3NF relations to BCNF");
// 		if(this.threeNFRelations != null){
// 			this.bcNFRelations = new ArrayList<Relation>();
// 			for(Relation r : this.threeNFRelations){
// 				int relationNormalForm = r.getNormalForm();
// 				if(!(relationNormalForm >= 4)){
// 					System.out.println(r);
// 					ArrayList<Relation> tempDecomposiiton = Decompositions.decomposeIntoBCNFScheme(r);
// 					this.bcNFRelations.addAll(tempDecomposiiton);
// 				} else {
// 					this.bcNFRelations.add(r);
// 				}
// 			}
// 		}
// 	}

// 	public void decomposeIntoBCNFRelations(){
// 		if(!(this.normalForm >= 4)){
// 			this.bcNFRelations = Decompositions.decomposeIntoBCNFScheme(this);
// 		}
// 	}

// 	///////////////////////// Printing Methods /////////////////////////
// 	public void printAttributes(){
// 		for(Attribute a : this.attributes){
// 			System.out.print(a.getName() + " ");
// 		}
// 		System.out.println("");
// 	}

// 	private String printListAttributes(ArrayList<Attribute> attributes){
// 		StringBuilder s = new StringBuilder();
// 		for(Attribute a : attributes){
// 			s.append(a.getName() + ",");
// 		}
// 		return s.toString();
// 	}

// 	public void printFDs(){
// 		for(FunctionalDependency f : funcDeps){
// 			System.out.println("   >" + f);
// 		}
// 	}

// 	public void printEssentialAttributes(){
// 		for(Attribute a : this.essentialAttributes){
// 			System.out.print(a.getName());
// 		}
// 		System.out.println("");
// 	}

// 	public void printNonEssentialAttributes(){
// 		for(Attribute a : this.nonEssentialAttributes){
// 			System.out.print(a.getName());
// 		}
// 		System.out.println("");
// 	}

// 	public void printSuperKeys(){
// 		for(ArrayList<Attribute> k : this.superKeys){
// 			System.out.print(Utils.stringifyAttributeList(k) + " ");
// 		}
// 		System.out.println("");
// 	}
	
// 	public void printCandidateKeys(){
// 		for(ArrayList<Attribute> k : this.candidate_key){
// 			System.out.print(Utils.stringifyAttributeList(k) + " ");
// 		}
// 		System.out.println("");
// 	}

// 	public void printKeyAttributes(){
// 		for(Attribute a : this.keyAttributes){
// 			System.out.print(a.getName());
// 		}
// 		System.out.println("");
// 	}

// 	public void printNonKeyAttributes(){
// 		for(Attribute a : this.nonKeyAttributes){
// 			System.out.print(a.getName());
// 		}
// 		System.out.println("");
// 	}

// 	public void printNormalForm(){
// 		switch(this.normalForm){
// 			case 1 :{
// 				System.out.print("Relation is in 1NF\n");
// 				break;
// 			}
// 			case 2 :{
// 				System.out.print("Relation is in 2NF\n");
// 				break;
// 			}
// 			case 3 :{
// 				System.out.print("Relation is in 3NF\n");
// 				break;
// 			}
// 			case 4 :{
// 				System.out.print("Relation is in BCNF\n");
// 				break;
// 			}
// 		}
// 	}

// 	public void printNormalFormsTable(){
// 		StringBuilder s = new StringBuilder();
// 		int len = 52;
// 		for(int i=0; i<len; i++){
// 			s.append("-");
// 		}
// 		s.append("\n");
// 		s.append("|  Functional Dependency  | 1NF | 2NF | 3NF | BCNF |\n");
// 		for(int i=0; i<len; i++){
// 			s.append("-");
// 		}
// 		s.append("\n");
// 		for(FunctionalDependency f : this.funcDeps){
// 			String name = f.getName();
// 			int normalForm = f.getNormalForm();
// 			double spaces = (25 - name.length())/2;
// 			s.append("|");
// 			for(int i=0; i<Math.floor(spaces); i++){
// 				s.append(" ");
// 			}
// 			s.append(name);
// 			for(int i=0; i<Math.ceil(spaces)+1; i++){
// 				s.append(" ");
// 			}
// 			s.append("|");
// 			switch(normalForm){
// 				case 1 :{
// 					s.append("  *  |  X  |  X  |  X   |\n");
// 					break;
// 				}
// 				case 2 :{
// 					s.append("  *  |  *  |  X  |  X   |\n");
// 					break;
// 				}
// 				case 3 :{
// 					s.append("  *  |  *  |  *  |  X   |\n");
// 					break;
// 				}
// 				case 4 :{
// 					s.append("  *  |  *  |  *  |  *   |\n");
// 					break;
// 				}
// 			}
// 		}
// 		for(int i=0; i<len; i++){
// 			s.append("-");
// 		}
// 		s.append("\n");
// 		System.out.print(s.toString());
// 	}

// 	public void printMinimalCover(){
// 		if(this.minimalCover != null){
// 			StringBuilder s = new StringBuilder();
// 			s.append("{ ");
// 			for(FunctionalDependency f : this.minimalCover){
// 				s.append(f.toString() + ", ");
// 			}
// 			s.deleteCharAt(s.toString().length() - 1);
// 			s.deleteCharAt(s.toString().length() - 1);
// 			s.append(" }");
// 			System.out.println(s.toString());
// 			return ;
// 		}
// 		System.out.println("Not yet computed :(");
// 	}

// 	public void print2NFRelations(){
// 		for(Relation r : this.twoNFRelations){
// 			System.out.println("	" + r);
// 		}
// 		System.out.println("");
// 	}

// 	public void print3NFRelations(){
// 		for(Relation r : this.threeNFRelations){
// 			System.out.println("	" + r);
// 		}
// 		System.out.println("");
// 	}

// 	public String toString(){
// 		return this.relation;
// 	}

// 	private String generateRelationString(ArrayList<Attribute> attributes){
// 		StringBuilder s = new StringBuilder();
// 		s.append("R(");
// 		ArrayList<Attribute> attr = new ArrayList<Attribute>(attributes);
// 		Utils.sortAttributes(attr);
// 		for(Attribute a : attr){
// 			s.append(a.getName() + ",");
// 		}
// 		s.deleteCharAt(s.toString().length() - 1);
// 		s.append(")");
// 		return s.toString();
// 	}

// 	///////////////////////// Getter and Setter Methods /////////////////////////
// 	public int getNoOfAttr(){
// 		return this.noOfAttr;
// 	}
// 	public Attribute getAttribute(String attrName){
// 		for(Attribute a : this.attributes){
// 			if(a.getName().equals(attrName)){
// 				return a;
// 			}
// 		}
// 		return null;
// 	}
// 	public ArrayList<Attribute> getAttributes(){
// 		return this.attributes;
// 	}
// 	public ArrayList<FunctionalDependency> getFunctionalDependencies(){
// 		return this.funcDeps;
// 	}
// 	public ArrayList<ArrayList<Attribute>> getKeys(){
// 		return this.superKeys;
// 	}
// 	public int getNormalForm(){
// 		return this.normalForm;
// 	}
// 	public ArrayList<Closure> getClosures(){
// 		return this.closures;
// 	}
// 	public ArrayList<FunctionalDependency> getMinimalCover(){
// 		return this.minimalCover;
// 	}
// 	public ArrayList<Relation> get2NFRelations(){
// 		return this.twoNFRelations;
// 	}
// 	public ArrayList<Relation> get3NFRelations(){
// 		return this.threeNFRelations;
// 	}
// }