package RelationalDatabase;

/**
 * The Attribute class is used to hold the each attribute of a relation
 * as an object by itself for easier use and protecting from tampering
 * attribute name.
 * @author Karthik Rangasai
 */
public class Attribute{
	///////////////////////// Class Members /////////////////////////
	/**
 	* The name of the attribute used in the Relation.
 	*/
	private String name;
	
	///////////////////////// Class Constructors /////////////////////////
	/**
	* <p>Creates the Attribute Object with it's name.</p>
	* @param name the name of the attribute in the relation.
	*/
	public Attribute(String name){
		this.name = name;
	}

	///////////////////////// Class Members /////////////////////////
	/**
	* <p>Compares the specified object with the name parameter.</p>
	*/
	public boolean equals(Object o){
		Attribute a = (Attribute)o;
		return this.name.equals(a.getName());
	}
	
	/**
	* <p>Returns the Attibute's name back.</p>
	*/
	public String toString(){
		return this.name;
	}

	///////////////////////// Getter and Setter Methods /////////////////////////
	/**
	* <p>Fteches the name of the private field name of the Attribute Class.</p>
	* @return the name of the Attribute
	*/
	public String getName(){
		return this.name;
	}
}