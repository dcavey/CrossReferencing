/**
 * 
 */
package ifs.datamodel;

/**
 * @author TDEWEERD
 *
 */
public interface Program {

	public String getProgramName();
	
	public String getProgramType();
	
	public String getCrudOperation();

	public void pushCrudOperation(String crudOperation);

	String getCrudOperation(char c);
}
