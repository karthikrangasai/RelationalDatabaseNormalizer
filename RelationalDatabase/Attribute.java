package RelationalDatabase;

public class Attribute{
	///////////////////////// Class Members /////////////////////////
	private String name;
	
	///////////////////////// Class Constructors /////////////////////////
	public Attribute(String name){
		this.name = name;
	}

	///////////////////////// Class Members /////////////////////////
	public boolean equals(Object o){
		Attribute a = (Attribute)o;
		return this.name.equals(a.getName());
	}
	
	public String toString(){
		return this.name;
	}

	///////////////////////// Getter and Setter Methods /////////////////////////
	public String getName(){
		return this.name;
	}
}