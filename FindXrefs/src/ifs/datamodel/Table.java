/**
 * 
 */
package ifs.datamodel;

/**
 * @author TDEWEERD
 *
 */
public interface Table {
	
	public static String AUTO_ENTRY = "C";
	public static String DETERMINE = "R";
	public static String FLAG = "U";
	public static String PURGE = "D";

	public String getTableName();
	
	public Owner getTableOwner();
	
	public String getTableDescription();
	
	public String getCrudOperation();
	
	public void pushCrudOperation(String crudOperation);

	public String getCrudOperation(char c);
}
