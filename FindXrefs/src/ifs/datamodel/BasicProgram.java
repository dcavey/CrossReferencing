/**
 * 
 */
package ifs.datamodel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TDEWEERD
 *
 */
public class BasicProgram implements Program {

	private String name;
	private String programType;
	private Set<String> crud = new HashSet<String>();
	
	public BasicProgram(String name, String programType) {
		this.name = name;
		this.programType = programType;
	}
	/* (non-Javadoc)
	 * @see ifs.datamodel.Program#getProgramName()
	 */
	@Override
	public String getProgramName() {
		return name;
	}
	
	@Override
	public String getProgramType() {
		return programType;
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
	@Override
	public String getCrudOperation() {
		StringBuilder s = new StringBuilder();
		
		if (crud.contains(Table.AUTO_ENTRY))
			s.append('C');
		if (crud.contains(Table.DETERMINE))
			s.append('R');
		if (crud.contains(Table.FLAG))
			s.append('U');
		if (crud.contains(Table.PURGE))
			s.append('D');
		return s.toString();
	}
	
	@Override
	public void pushCrudOperation(String crudOperation) {
		this.crud.add(crudOperation);
	}

	@Override
	public String getCrudOperation(char c) {
		if (crud.contains(""+c)) {
			return ""+c;
		}
		return "";
	}
	
	@Override
	public String toString() {
		return name + " (" + getCrudOperation() + ")";
	}
}
