/**
 * 
 */
package ifs.datamodel;

/**
 * @author TDEWEERD
 *
 */
public interface Table {

	public String getTableName();
	
	public Owner getTableOwner();
	
	public String getTableDescription();
}
