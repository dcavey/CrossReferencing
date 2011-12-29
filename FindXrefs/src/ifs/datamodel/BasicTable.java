/**
 * 
 */
package ifs.datamodel;

/**
 * @author TDEWEERD
 *
 */
public class BasicTable implements Table{
	
	private String name;
	private Owner owner;
	private String description;

	public BasicTable(String name, Owner owner, String description) {
		this.name = name;
		this.owner = owner;
		this.description = description;
	}

	@Override
	public String getTableName() {
		return name;
	}

	@Override
	public Owner getTableOwner() {
		return owner;
	}

	@Override
	public String getTableDescription() {
		return description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return name.equals((String)obj);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
