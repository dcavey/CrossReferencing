/**
 * 
 */
package ifs.datamodel;

/**
 * @author TDEWEERD
 *
 */
public class BasicProgram implements Program {

	private String name;
	private String programDescription;
	
	public BasicProgram(String name, String programDescription) {
		this.name = name;
		this.programDescription = programDescription;
	}
	/* (non-Javadoc)
	 * @see ifs.datamodel.Program#getProgramName()
	 */
	@Override
	public String getProgramName() {
		return name;
	}
	
	@Override
	public String getProgramDecription() {
		return programDescription;
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
