/**
 * 
 */
package ifs.datamodel;

/**
 * @author TDEWEERD
 *
 */
public class BasicOwner implements Owner {

	private String name;

	public BasicOwner(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see ifs.datamodel.Owner#getOwnerName()
	 */
	@Override
	public String getOwnerName() {
		return name;
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
