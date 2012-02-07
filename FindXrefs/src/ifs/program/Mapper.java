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
	private static final String ERROR_CLOSE_FILES = "Please close all files!";
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
		if(new File(Constants.CSVOUTPUTFILE).exists()){
			if(!(new File(Constants.CSVOUTPUTFILE).delete())) {
				System.err.println(ERROR_CLOSE_FILES);
				System.exit(-1);
			}
		}
		if(new File(Constants.GPROGOUTPUT).exists()){
			if(!(new File(Constants.GPROGOUTPUT).delete())) {
				System.err.println(ERROR_CLOSE_FILES);
				System.exit(-1);
			}
		}
		if(new File(Constants.TEXTOUTPUTFILE).exists()){
			if(!(new File(Constants.TEXTOUTPUTFILE).delete())) {
				System.err.println(ERROR_CLOSE_FILES);
				System.exit(-1);
			}
		}
		if(new File(Constants.SKIPPEDLINES).exists()){
			if(!(new File(Constants.SKIPPEDLINES).delete())) {
				System.err.println(ERROR_CLOSE_FILES);
				System.exit(-1);
			}
		}
		if(new File(Constants.MWOUTPUT).exists()){
			if(!(new File(Constants.MWOUTPUT).delete())) {
				System.err.println(ERROR_CLOSE_FILES);
				System.exit(-1);
			}
		}
	}
}
