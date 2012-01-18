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
	
	public String getProgramDecription();
	
	public String getCrudOperation();

	public void pushCrudOperation(String crudOperation);

	String getCrudOperation(char c);
}
