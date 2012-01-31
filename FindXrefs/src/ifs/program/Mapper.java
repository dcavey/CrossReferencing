/**
 * 
 */
package ifs.program;

import ifs.engine.ProgramTablesEngine;
import ifs.engine.TablesEngine;
import ifs.jl.CheckRefs;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author TDEWEERD
 * db.csv
 */

public class Mapper {	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.printf ("Time at start = %s \n", new Date() );
		//print to output.txt -prog / db-
		try {
			clearFiles();
			//CheckReferences cr = new CheckReferences(new FileInputStream(Constants.SOURCE_CODE));
			CheckRefs cr = new CheckRefs();
			cr.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			TablesEngine.getTablesCollection(new File(Constants.TABLEFILE));
		
			ProgramTablesEngine.generateTableToProgramsMapping(new File(Constants.TEXTOUTPUTFILE));
			//printOutput(h);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf ("Time at end = %s", new Date() );
	}
	
	private static void clearFiles(){
		if(!(new File(Constants.CSVOUTPUTFILE).delete()
				&& new File(Constants.GPROGOUTPUT).delete()
				&& new File(Constants.TEXTOUTPUTFILE).delete()
				&& new File(Constants.SKIPPEDLINES).delete())) {
			System.err.println("Please close all files!");
			System.exit(-1);
		}
	}
}
