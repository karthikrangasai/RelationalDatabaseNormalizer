package RelationalDatabase;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;

/**
* The Decomposition Class provides us with the methods to decompose
* a relation to a higher Normal From.
* @author Karthik Rangasai
*/
public class Decompositions{
	
	// public static ArrayList<Relation> decomposeInto2NFScheme(Relation relation){
	// 	// System.out.println("\n  Decomposing Into 2NF Relations");
	// 	if(relation.getNormalForm() < 2){
	// 		ArrayList<Relation> twoNFRelations = new ArrayList<Relation>();
	// 		Set<Attribute> attributes = new HashSet<Attribute>();
	// 		ArrayList<FunctionalDependency> funcDeps = new ArrayList<FunctionalDependency>();
	// 		Utils.generateFunctionalDependencies(relation.getFunctionalDependencies(), funcDeps);
	// 		// for(FunctionalDependency f : relation.getFunctionalDependencies()){
	// 		// 	funcDeps.add(new FunctionalDependency(relation, f.getLeftSideAttributes(), f.getRightSideAttributes()));
	// 		// }
	// 		// System.out.println("  " + funcDeps);
	// 		// for(FunctionalDependency f : funcDeps){
	// 		// 	System.out.println("	For " + f + " Normal Form is " + f.getNormalForm());
	// 		// 	if(f.getNormalForm() < 2){
	// 		// 		System.out.println("		Generating relation.");
	// 		// 		attributes.addAll(f.getLeftSideAttributes());
	// 		// 		attributes.addAll(f.getRightSideAttributes());
	// 		// 		twoNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
	// 		// 		attributes.clear();
	// 		// 	}
	// 		// }
	// 		// System.out.println("  " + funcDeps);
	// 		Iterator<FunctionalDependency> itr = funcDeps.iterator();
	// 		// Set<Attribute> fdRightSideAttributes = new HashSet<Attribute>();
	// 		while(itr.hasNext()){
	// 			FunctionalDependency f = itr.next();
	// 			// System.out.println("	For " + f + " Normal Form is " + f.getNormalForm());
	// 			if(f.getNormalForm() < 2){
	// 				// System.out.println("		Generating relation.");
	// 				attributes.addAll(f.getLeftSideAttributes());
	// 				attributes.addAll(f.getRightSideAttributes());
	// 				twoNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
	// 				// fdRightSideAttributes.addAll(f.getRightSideAttributes());
	// 				// relation.removeAttributes(f.getRightSideAttributes());
	// 				itr.remove();
	// 				attributes.clear();
	// 			}
	// 		}
	// 		// System.out.println("  " + funcDeps);
	// 		// for(FunctionalDependency f : funcDeps){
				
	// 		// 	attributes.addAll(f.getLeftSideAttributes());
	// 		// 	attributes.addAll(f.getRightSideAttributes());
	// 		// 	f.getRightSideAttributes().removeAll(fdRightSideAttributes);
	// 		// }
	// 		for(FunctionalDependency f : funcDeps){
	// 			attributes.addAll(f.getLeftSideAttributes());
	// 			attributes.addAll(f.getRightSideAttributes());
	// 			// f.getRightSideAttributes().removeAll(fdRightSideAttributes);
	// 		}
	// 		// attributes.removeAll(fdRightSideAttributes);
	// 		// System.out.println("  " + attributes);
	// 		// System.out.println("Last 2NF relation Contructor: Attributes = " + attributes + " anf FDs = " + funcDeps);
	// 		twoNFRelations.add(new Relation(attributes, funcDeps)); 
	// 		// *************** Write New Constructor in Relation(Set<Attribute>, ArrayList<FunctionalDepenedency>) ******************** //
	// 		return twoNFRelations;
	// 	}
	// 	return null;
	// }

	/** 
	* Decomposes a relation to relation that will satisfy 2nd Normal Form
	* @param relation A Relation Class object we wish to decompose.
	* @return An ArrayList of Relation Objects whcih are in 2nd Normal Form or higher
	*/
	public static ArrayList<Relation> decomposeInto2NFScheme(Relation relation){
		// System.out.println("\n->Decomposing Into 2NF Relations");
		if(relation.getNormalForm() < 2){
			ArrayList<FunctionalDependency> relationFDs = new ArrayList<FunctionalDependency>();
			relationFDs.addAll(relation.getFunctionalDependencies());

			ArrayList<Relation> twoNFRelations = new ArrayList<Relation>();

			ArrayList<FunctionalDependency> fullFuncDeps = relation.getFullFunctionalDependencies();
			
			CopyOnWriteArrayList<FunctionalDependency> partialFuncDeps = new CopyOnWriteArrayList<FunctionalDependency>();
			Utils.generateFunctionalDependencies(relation.getPartialFunctionalDependencies(), partialFuncDeps);

			Set<Attribute> fullFDAttributes = new HashSet<Attribute>();
			for(FunctionalDependency f : fullFuncDeps){
				fullFDAttributes.addAll(f.getLeftSideAttributes());
				fullFDAttributes.addAll(f.getRightSideAttributes());
			}

			Set<Attribute> twoNFAttr = new HashSet<Attribute>();
			ArrayList<FunctionalDependency> twoNFFDs = new ArrayList<FunctionalDependency>();

			Iterator<FunctionalDependency> itr1 = partialFuncDeps.iterator();
			while(itr1.hasNext()){
				FunctionalDependency fd = itr1.next();
				if(fd.getNormalForm() < 2){
					Closure c = Closure.computeClosure(fd.getLeftSideAttributes(), relation.getFunctionalDependencies());
					twoNFAttr.addAll(c.getRightSide());
					twoNFFDs.add(fd);
					for(Attribute a : relation.getNonKeyAttributes()){
						if(c.getRightSide().contains(a)){
							fullFDAttributes.remove(a);
						}
					}
					Iterator<FunctionalDependency> itr2 = partialFuncDeps.iterator();
					while(itr2.hasNext()){
						FunctionalDependency f = itr2.next();
						if(!fd.equals(f) && c.getRightSide().containsAll(f.getLeftSideAttributes())){
							twoNFFDs.add(f);
							partialFuncDeps.remove(f);
						}
					}
					twoNFRelations.add(new Relation(twoNFAttr, new ArrayList<FunctionalDependency>(twoNFFDs)));
					// twoNFRelations.add(new Relation(twoNFAttr, twoNFFDs));
					partialFuncDeps.removeAll(twoNFFDs);
					relationFDs.removeAll(twoNFFDs);
				}
				twoNFAttr.clear();
				twoNFFDs.clear();
			}
			if(!fullFDAttributes.isEmpty()){
				twoNFRelations.add(new Relation(fullFDAttributes, new ArrayList<FunctionalDependency>(relationFDs)));
			}
			return twoNFRelations;
		}
		return null;
	}

	/** 
	* Decomposes a relation to relation that will satisfy 3rd Normal Form
	* @param relation A Relation Class object we wish to decompose.
	* @return An ArrayList of Relation Objects whcih are in 3rd Normal Form or higher
	*/
	public static ArrayList<Relation> decomposeInto3NFScheme(Relation relation){
		// System.out.println("Hello");
		ArrayList<Relation> threeNFRelations = new ArrayList<Relation>();
		Set<Attribute> relationAttributes = new HashSet<Attribute>(relation.getAttributes());
		Set<Attribute> finsihedAttributes = new HashSet<Attribute>();
		Set<Attribute> attributes = new HashSet<Attribute>();
		ArrayList<FunctionalDependency> minimalCover = new ArrayList<FunctionalDependency>();
		Utils.generateFunctionalDependencies(relation.getMinimalCover(), minimalCover);
		
		Iterator<FunctionalDependency> itr = minimalCover.iterator();
		while(itr.hasNext()){
			FunctionalDependency f = itr.next();
			// System.out.println(f);
			// if(!(f.getNormalForm() >= 3)){
				finsihedAttributes.addAll(f.getLeftSideAttributes());
				finsihedAttributes.addAll(f.getRightSideAttributes());
				attributes.addAll(f.getLeftSideAttributes());
				attributes.addAll(f.getRightSideAttributes());
				threeNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
				itr.remove();
				attributes.clear();
			// }
		}
		// System.out.println("Jabez is useless");
		relationAttributes.removeAll(finsihedAttributes);
		if(!relationAttributes.isEmpty()){
			threeNFRelations.add(new Relation(relationAttributes, new ArrayList<Attribute>(relationAttributes), new ArrayList<Attribute>(relationAttributes)));
		}
		// System.out.println("Jabez is useless");
		ArrayList<ArrayList<Attribute>> candidate_key = relation.getCandidateKeys();
		boolean exists = false;
		for(ArrayList<Attribute> key : candidate_key){
			for(Relation r : threeNFRelations){
				// System.out.println(r.getAttributes() + " has the entire key " + key);
				if(r.getAttributes().containsAll(key)){
					exists = true;
					break;
				}
			}
			if(exists){
				break;
			}
		}
		if(!exists){
			threeNFRelations.add(new Relation(new HashSet<Attribute>(candidate_key.get(0)), null));
		}
		return threeNFRelations;
	}

	// public static ArrayList<Relation> decomposeIntoBCNFScheme(Relation relation){
	// 	ArrayList<Relation> bcNFRelations = new ArrayList<Relation>();
	// 	bcNFRelations.add(relation);
	// 	Iterator<Relation> itr = bcNFRelations.iterator();
	// 	while(itr.hasNext()){
	// 		Relation r = itr.next();
	// 		Set<Attribute> relationAttributes = new HashSet<Attribute>(r.getAttributes());
	// 		ArrayList<FunctionalDependency> funcDeps = new ArrayList<FunctionalDependency>();
	// 		Utils.generateFunctionalDependencies(r.getFunctionalDependencies(), funcDeps);
	// 		Iterator<FunctionalDependency> itr_funcDep = funcDeps.iterator();
	// 		while(itr_funcDep.hasNext()){
	// 			FunctionalDependency f = itr_funcDep.next();
	// 			System.out.println("FD is : " + f + " and my NF is " + f.getNormalForm());
	// 			if(!f.inBCNF()){
	// 				System.out.println("FD is not in BCNF : " + f);
	// 				// Relation - (X U Y)
	// 				Set<Attribute> relationOneAttr = new HashSet<Attribute>(f.getLeftSideAttributes());
	// 				relationOneAttr.addAll(f.getRightSideAttributes());
	// 				bcNFRelations.add(new Relation(relationOneAttr, f.getLeftSideAttributes(), f.getRightSideAttributes()));
	// 				itr_funcDep.remove();
	// 				System.out.print("RelationOneAttr: ");
	// 				for(Attribute a : relationOneAttr){
	// 					System.out.print(a + "  ");
	// 				}
	// 				System.out.println("");
	// 				// Relation - (R - Y)
	// 				relationAttributes.removeAll(f.getRightSideAttributes());
	// 				for(FunctionalDependency fD : funcDeps){
	// 					System.out.println("Bool val: " + fD.hasAttributes(relationOneAttr));
	// 					if(!fD.equals(f) && fD.hasAttributes(relationOneAttr)){
	// 						fD.getLeftSideAttributes().removeAll(relationOneAttr);
	// 						fD.getRightSideAttributes().removeAll(relationOneAttr);
	// 					}
	// 				}
	// 				System.out.println("Test 123 RA " + relationAttributes);
	// 				System.out.println("Test 123 FD " + funcDeps);
	// 				bcNFRelations.add(new Relation(relationAttributes, funcDeps));
	// 				break;
	// 			}
	// 		}
	// 		itr.remove();
	// 	}
	// 	// Set<Attribute> relationAttributes = new HashSet<Attribute>(relation.getAttributes());
	// 	// Set<Attribute> finsihedAttributes = new HashSet<Attribute>();
	// 	// Set<Attribute> attributes = new HashSet<Attribute>();
	// 	// ArrayList<FunctionalDependency> minimalCover = new ArrayList<FunctionalDependency>();
	// 	// Utils.generateFunctionalDependencies(relation.getMinimalCover(), minimalCover);
		
	// 	// Iterator<FunctionalDependency> itr = minimalCover.iterator();
	// 	// while(itr.hasNext()){
	// 	// 	FunctionalDependency f = itr.next();
	// 	// 	System.out.println(f);
	// 	// 	// if(!(f.getNormalForm() >= 3)){
	// 	// 		finsihedAttributes.addAll(f.getLeftSideAttributes());
	// 	// 		finsihedAttributes.addAll(f.getRightSideAttributes());
	// 	// 		attributes.addAll(f.getLeftSideAttributes());
	// 	// 		attributes.addAll(f.getRightSideAttributes());
	// 	// 		threeNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
	// 	// 		itr.remove();
	// 	// 		attributes.clear();
	// 	// 	// }
	// 	// }
	// 	// relationAttributes.removeAll(finsihedAttributes);
	// 	// if(!relationAttributes.isEmpty()){
	// 	// 	threeNFRelations.add(new Relation(relationAttributes, new ArrayList<Attribute>(relationAttributes), new ArrayList<Attribute>(relationAttributes)));
	// 	// }
	// 	return bcNFRelations;
	// }

	/** 
	* Decomposes a relation to relation that will satisfy Boyce-Codd Normal Form
	* @param relation A Relation Class object we wish to decompose.
	* @return An ArrayList of Relation Objects whcih are in Boyce-Codd Normal Form or higher
	*/
	public static ArrayList<Relation> decomposeIntoBCNFScheme(Relation relation){
		CopyOnWriteArrayList<Relation> bcNFRelations = new CopyOnWriteArrayList<Relation>();
		ArrayList<Relation> relationDustbin = new ArrayList<Relation>();
		bcNFRelations.add(relation);
		Iterator<Relation> itr = bcNFRelations.iterator();
		while(itr.hasNext()){
			Relation r = itr.next();
			// itr.remove();
			relationDustbin.add(r);
			int nf = r.getNormalForm();
			if(nf < 4){
				ArrayList<FunctionalDependency> funcDeps = new ArrayList<FunctionalDependency>();
				Utils.generateFunctionalDependencies(r.getFunctionalDependencies(), funcDeps);
				Iterator<FunctionalDependency> itrFD = funcDeps.iterator();
				while(itrFD.hasNext()){
					FunctionalDependency fd = itrFD.next();
					if(!fd.inBCNF()){
						// Relation - X U Y
						Set<Attribute> relationOneAttributes = new HashSet<Attribute>();
						relationOneAttributes.addAll(fd.getLeftSideAttributes());
						relationOneAttributes.addAll(fd.getRightSideAttributes());
						bcNFRelations.add(new Relation(relationOneAttributes, fd.getLeftSideAttributes(), fd.getRightSideAttributes()));
						itrFD.remove();
						// Relation - R - Y
						Set<Attribute> relationTwoAttributes = new HashSet<Attribute>(r.getAttributes());
						relationTwoAttributes.removeAll(fd.getRightSideAttributes());
						ArrayList<FunctionalDependency> funcDepsTwo = null;
						for(FunctionalDependency f : funcDeps){
							if(f.hasAttributes(relationTwoAttributes)){
								if(funcDepsTwo == null){
									funcDepsTwo = new ArrayList<FunctionalDependency>();
									funcDepsTwo.add(f);
								}
								funcDepsTwo.add(f);
							}
						}
						bcNFRelations.add(new Relation(relationTwoAttributes, funcDepsTwo));
					}
				}
			}
			bcNFRelations.removeAll(relationDustbin);
		}
		return new ArrayList<Relation>(bcNFRelations);
	}

	/** 
	* Generates a string from the set of Attribute objects of a relation
	* @param attributes A Set of Attribute class objects
	* @return A String
	*/
	private static String generateRelationString(Set<Attribute> attributes){
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
}