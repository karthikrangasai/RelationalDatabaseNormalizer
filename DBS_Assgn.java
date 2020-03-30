import RelationalDatabase.*;

import java.util.*;

class DBS_Assgn {
	public static void main(String[] args){

		// String relation = "R(A,C)";
		// String funcDep = "C->A";

		// String relation = "R(A,B,C,D)";
		// String funcDep = "A,B->C;A,B->D;C->B;B->D";
		// String funcDep = "B->A;D->A;A,B->D";
		// String funcDep = "A,B->C,D;C->B;B->D";
		
		// String relation = "R(A,B,C,D,E)";
		// String funcDep = "A,B->C;A,B->D;B->E";

		// String relation = "R(A,B,C,D,E,F)";
		// String funcDep = "A,B,C->D,E;E->F";

		String relation = "R(A,B,C,D,E,F)";
		String funcDep = "A,B->C;C->A;D,C->B;A,C,D->B;D->E,F;B,E->C";

		/////// Example 1
		// String relation = "R(A,B,C,D,E)";
		// String funcDep = "A,B->C,D,E;A->C;B->D;C->E";
		/////// Example 2
		// String relation = "R(A,B,C,D,E)";
		// String funcDep = "A,B->C,D,E;B->E;C->B";
		/////// Example 3
		// String relation = "R(A,B,C,D,E,F)";
		// String funcDep = "A,D->C,E,F;B->E;F->D";
		/////// Example 4
		// String relation = "R(A,B,C,D,E,F)";
		// String funcDep = "A,D->B,C,F;B->E;E->F";
		
		// String relation = args[0];
		// String funcDep = args[1]; 

		Relation R = new Relation(relation, ((relation.length() - 2)/2), funcDep);
		System.out.print("*) Input Relation is: ");System.out.println(R);
		System.out.print("\n*) Attributes of the relation are: ");R.printAttributes();
		System.out.println("\n*) Functional Dependencies of the relation are: ");R.printFDs();

		R.computeClosures();
		System.out.println("\n*) Closures of all the combinations of attributes are: ");
		for(Closure c : R.getClosures()){
			System.out.println(c);
		}

		System.out.print("\n*) Essential Attributes are: ");R.printEssentialAttributes();
		System.out.print("*) Non-Essential Attributes are: ");R.printNonEssentialAttributes();

		R.computeSuperKeys();
		System.out.print("\n*) Super Keys are: ");R.printSuperKeys();
		R.computeCandiadteKey();
		System.out.print("*) Candidate Keys are: ");R.printCandidateKeys();

		System.out.print("\n*) Key Attributes are: ");R.printKeyAttributes();
		System.out.print("*) Non-Key Attributes are: ");R.printNonKeyAttributes();
	

		R.computeNormalForm();
		System.out.print("\n*) Checking for highest Normal Form: ");R.printNormalForm();
		R.printNormalFormsTable();

		R.computeMinimalCover();
		System.out.println("\n*) Minimal Cover of the relation is:");
		System.out.print("	");R.printMinimalCover();

		
		
		System.out.println("\n\n\n");
		R.decomposeInto2NFRelations();
		System.out.println("\n*) 2 NF Relations are:");R.print2NFRelations();
		ArrayList<Relation> twoNFRelations = R.get2NFRelations();
		for(Relation r: twoNFRelations){
			System.out.println("======================================================================");
			System.out.print("    *) Input Relation is: ");System.out.println(r);
			// System.out.print("\n    *) Attributes of the relation are: ");r.printAttributes();
			// System.out.println("\n    *) Functional Dependencies of the relation are: ");r.printFDs();
			// System.out.println("\n    *) Closures of all the combinations of attributes are: ");
			// for(Closure c : r.getClosures()){
			// 	System.out.println(c);
			// }
			// System.out.print("\n    *) Essential Attributes are: ");r.printEssentialAttributes();
			// System.out.print("    *) Non-Essential Attributes are: ");r.printNonEssentialAttributes();
			// System.out.print("\n    *) Super Keys are: ");r.printSuperKeys();
			// System.out.print("    *) Candidate Keys are: ");r.printCandidateKeys();
			System.out.print("\n    *) Key Attributes are: ");r.printKeyAttributes();
			// System.out.print("    *) Non-Key Attributes are: ");r.printNonKeyAttributes();
			System.out.print("\n    *) Checking for highest Normal Form: ");r.printNormalForm();
			r.printNormalFormsTable();
			System.out.println("\n    *) Minimal Cover of the relation is:");r.printMinimalCover();
		}


		System.out.println("\n\n\n");
		R.decomposeInto3NFRelations();
		System.out.println("\n*) 3 NF Relations are:");R.print3NFRelations();
		ArrayList<Relation> threeNFRelations = R.get3NFRelations();
		for(Relation r: threeNFRelations){
			System.out.println("======================================================================");
			System.out.print("    *) Input Relation is: ");System.out.println(r);
			System.out.print("\n    *) Attributes of the relation are: ");r.printAttributes();
			// System.out.println("\n    *) Functional Dependencies of the relation are: ");r.printFDs();
			// System.out.println("\n    *) Closures of all the combinations of attributes are: ");
			// for(Closure c : r.getClosures()){
			// 	System.out.println(c);
			// }
			// System.out.print("\n    *) Essential Attributes are: ");r.printEssentialAttributes();
			// System.out.print("    *) Non-Essential Attributes are: ");r.printNonEssentialAttributes();
			// System.out.print("\n    *) Super Keys are: ");r.printSuperKeys();
			// System.out.print("    *) Candidate Keys are: ");r.printCandidateKeys();
			System.out.print("\n    *) Key Attributes are: ");r.printKeyAttributes();
			// System.out.print("    *) Non-Key Attributes are: ");r.printNonKeyAttributes();
			System.out.print("\n    *) Checking for highest Normal Form: ");r.printNormalForm();
			r.printNormalFormsTable();
			System.out.println("\n    *) Minimal Cover of the relation is:");r.printMinimalCover();
		}

		System.out.println("\n");
		for(Relation r : twoNFRelations){
			r.printAttributes();
		}
		System.out.println("\n");
		for(Relation r : threeNFRelations){
			r.printAttributes();
		}
	}
}




// class RelationGraphRep{
// 	public Relation relation;
// 	public int[][] dependencies;
	
// 	// Constructor
// 	public RelationGraphRep(String relation, String funcDep, int noOfAttr){
// 		this.relation = new Relation(relation, noOfAttr);
// 		this.dependencies = new int[this.relation.noOfAttr][this.relation.noOfAttr];
// 		for(int i=0; i<this.relation.noOfAttr; i++){
// 			for(int j=0; j<this.relation.noOfAttr; j++){
// 				this.dependencies[i][j] = 0;
// 			}
// 		}
// 		StringTokenizer fds = new StringTokenizer(funcDep, ";");
// 		while(fds.hasMoreTokens()){
// 			StringTokenizer fd = new StringTokenizer(fds.nextToken(), "->");
// 			String x = fd.nextToken();
// 			String y = fd.nextToken();
// 			int l, r, neighbour=0;
// 			StringTokenizer x_a = new StringTokenizer(x,",");
// 			while(x_a.hasMoreTokens()){
// 				String X = x_a.nextToken();
// 				l = this.relation.attr_map.get(X.charAt(0));
// 				StringTokenizer y_a = new StringTokenizer(y,",");
// 				while(y_a.hasMoreTokens()){
// 					String Y = y_a.nextToken();
// 					r = this.relation.attr_map.get(Y.charAt(0));
// 					dependencies[l][r] = 1;
// 					++neighbour;
// 				}
// 				dependencies[l][l] = 1;
// 				++neighbour;
// 				this.relation.attributes[l].neighbours = neighbour;
// 			}
// 		}
// 		this.printDependencies();
// 	}


// 	// Sort the Relation Attributes according to the number of attributes 
// 	// they can point to in descending oreder
// 	public void sortRelationAttributes(){
// 		Arrays.sort(this.relation.attributes, new SortByNeighbours());
// 		this.printRelationAttributes();
// 	}

// 	private boolean completeDesc(ArrayList<Node> keys, int numAttr){
// 		int[] ones = new int[numAttr];
// 		int[] desc = new int[numAttr];
// 		Arrays.fill(ones, 1);
// 		Arrays.fill(desc, 0);
// 		for(int i=0; i<keys.size(); i++){
// 			for(int j=0; j<numAttr; j++){
// 				desc[j] = desc[j] | this.dependencies[i][j];
// 			}
// 		}
// 		return Arrays.equals(ones, desc);
// 	}

// 	// Method to get Key
// 	ArrayList<Node> getKey(){
// 		this.relation.keys = new ArrayList<Node>();
// 		int n = this.relation.noOfAttr;
// 		for(int i=0; i<n;i++){
// 			this.relation.keys.add(this.relation.attributes[i]);
// 		}
// 		Node recentAttr = null;
// 		while(this.completeDesc(this.relation.keys, n)){
// 			recentAttr = this.relation.keys.get(this.relation.keys.size()-1);
// 			this.relation.keys.remove(this.relation.keys.size()-1);
// 		}
// 		if(recentAttr!=null){
// 			this.relation.keys.add(recentAttr);
// 		}
// 		return this.relation.keys;
// 	}


// 	// Print Dependencies Matrix
// 	public void printDependencies(){
// 		int len = this.relation.noOfAttr;
// 		for(int i=0; i<len; i++){
// 			for(int j=0; j<len; j++){
// 				System.out.print(this.dependencies[i][j] + "  ");
// 			}
// 			System.out.println("");
// 		}
// 	}

// 	// Print Relation Attributes
// 	public void printRelationAttributes(){
// 		this.relation.printAttributes();
// 	}

// 	// Print the Keys
// 	public void printRelationKeys(){
// 		this.relation.printKeys();
// 	}
// }

