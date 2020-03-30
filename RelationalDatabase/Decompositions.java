package RelationalDatabase;

import java.util.*;

public class Decompositions{
	
	public static ArrayList<Relation> decomposeInto2NFScheme(Relation relation){
		// System.out.println("\n  Decomposing Into 2NF Relations");
		if(relation.getNormalForm() < 2){
			ArrayList<Relation> twoNFRelations = new ArrayList<Relation>();
			Set<Attribute> attributes = new HashSet<Attribute>();
			ArrayList<FunctionalDependency> funcDeps = new ArrayList<FunctionalDependency>();
			Utils.generateFunctionalDependencies(relation.getFunctionalDependencies(), funcDeps);
			// for(FunctionalDependency f : relation.getFunctionalDependencies()){
			// 	funcDeps.add(new FunctionalDependency(relation, f.getLeftSideAttributes(), f.getRightSideAttributes()));
			// }
			// System.out.println("  " + funcDeps);
			// for(FunctionalDependency f : funcDeps){
			// 	System.out.println("	For " + f + " Normal Form is " + f.getNormalForm());
			// 	if(f.getNormalForm() < 2){
			// 		System.out.println("		Generating relation.");
			// 		attributes.addAll(f.getLeftSideAttributes());
			// 		attributes.addAll(f.getRightSideAttributes());
			// 		twoNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
			// 		attributes.clear();
			// 	}
			// }
			// System.out.println("  " + funcDeps);
			Iterator<FunctionalDependency> itr = funcDeps.iterator();
			while(itr.hasNext()){
				FunctionalDependency f = itr.next();
				// System.out.println("	For " + f + " Normal Form is " + f.getNormalForm());
				if(f.getNormalForm() < 2){
					// System.out.println("		Generating relation.");
					attributes.addAll(f.getLeftSideAttributes());
					attributes.addAll(f.getRightSideAttributes());
					twoNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
					itr.remove();
					attributes.clear();
				}
			}
			// System.out.println("  " + funcDeps);
			for(FunctionalDependency f : funcDeps){
				attributes.addAll(f.getLeftSideAttributes());
				attributes.addAll(f.getRightSideAttributes());
			}
			// System.out.println("  " + attributes);
			twoNFRelations.add(new Relation(attributes, funcDeps)); 
			// *************** Write New Constructor in Relation(Set<Attribute>, ArrayList<FunctionalDepenedency>) ******************** //
			return twoNFRelations;
		}
		return null;
	}
	
	public static ArrayList<Relation> decomposeInto3NFScheme(Relation relation){
		ArrayList<Relation> threeNFRelations = new ArrayList<Relation>();
		Set<Attribute> relationAttributes = new HashSet<Attribute>(relation.getAttributes());
		Set<Attribute> finsihedAttributes = new HashSet<Attribute>();
		Set<Attribute> attributes = new HashSet<Attribute>();
		ArrayList<FunctionalDependency> minimalCover = new ArrayList<FunctionalDependency>();
		Utils.generateFunctionalDependencies(relation.getMinimalCover(), minimalCover);
		
		Iterator<FunctionalDependency> itr = minimalCover.iterator();
		while(itr.hasNext()){
			FunctionalDependency f = itr.next();
			System.out.println(f);
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
		relationAttributes.removeAll(finsihedAttributes);
		if(!relationAttributes.isEmpty()){
			threeNFRelations.add(new Relation(relationAttributes, new ArrayList<Attribute>(relationAttributes), new ArrayList<Attribute>(relationAttributes)));
		}
		return threeNFRelations;
	}

	// public static ArrayList<Relation> decomposeIntoBCNFScheme(Relation relation){
	// 	ArrayList<Relation> bcNFRelations = new ArrayList<Relation>();
	// 	bcNFRelations.add(relation);

	// 	Iterator itr = bcNFRelations.iterator();
	// 	do{
	// 		while(itr.hasNext()){
	// 			Relation r = itr.next();
	// 		}
	// 	}while(Decompositions.relationSetNotBCNF(bcNFRelations));
	// 	Set<Attribute> relationAttributes = new HashSet<Attribute>(relation.getAttributes());
	// 	Set<Attribute> finsihedAttributes = new HashSet<Attribute>();
	// 	Set<Attribute> attributes = new HashSet<Attribute>();
	// 	ArrayList<FunctionalDependency> minimalCover = new ArrayList<FunctionalDependency>();
	// 	Utils.generateFunctionalDependencies(relation.getMinimalCover(), minimalCover);
		
	// 	Iterator<FunctionalDependency> itr = minimalCover.iterator();
	// 	while(itr.hasNext()){
	// 		FunctionalDependency f = itr.next();
	// 		System.out.println(f);
	// 		// if(!(f.getNormalForm() >= 3)){
	// 			finsihedAttributes.addAll(f.getLeftSideAttributes());
	// 			finsihedAttributes.addAll(f.getRightSideAttributes());
	// 			attributes.addAll(f.getLeftSideAttributes());
	// 			attributes.addAll(f.getRightSideAttributes());
	// 			threeNFRelations.add(new Relation(attributes, f.getLeftSideAttributes(), f.getRightSideAttributes()));
	// 			itr.remove();
	// 			attributes.clear();
	// 		// }
	// 	}
	// 	relationAttributes.removeAll(finsihedAttributes);
	// 	if(!relationAttributes.isEmpty()){
	// 		threeNFRelations.add(new Relation(relationAttributes, new ArrayList<Attribute>(relationAttributes), new ArrayList<Attribute>(relationAttributes)));
	// 	}
	// 	return threeNFRelations;
	// }

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