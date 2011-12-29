/**
 * 
 */
package ifs.program;

import ifs.engine.ProgramTablesEngine;
import ifs.engine.TablesEngine;
import ifs.jl.CheckReferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author TDEWEERD
 *
 */
public class Mapper {
	private static final String FS_DATABASE_CSV = "D:/workspace/IFS/src/ifs/resources/ifs_database.csv";
	private static final String OUTPUTFILE = "D:/workspace/IFS/src/ifs/resources/table_owner_program.csv";
	private static final String INPUTFILE = "D:/workspace/IFS/src/ifs/resources/output.txt";
	private static final String CODEFILE = "C:/tempSource/ifsprd.mdl";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CheckReferences cr = new CheckReferences(new FileInputStream(CODEFILE));
			cr.run();
		} catch (Exception e) {
			System.err.println("Exception :" + e);
		}
		
		TablesEngine.getTablesCollection(new File(FS_DATABASE_CSV));
		HashMap<String, ArrayList<String>> h = ProgramTablesEngine.generateTableToProgramsMapping(new File(INPUTFILE));
		printOutput(h);
	}
	
	public static void printOutput(HashMap<String, ArrayList<String>> references) {

		try {
			// Create file
			FileWriter fstream = new FileWriter(OUTPUTFILE);
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<Entry<String, ArrayList<String>>> it = references.entrySet()
					.iterator();
			out.write("Table;Owner;Program");
			out.newLine();
			while (it.hasNext()) {
				Entry<String, ArrayList<String>> e = it.next();
				if (!TablesEngine.tableAndOwner.containsKey(e.getKey()))
					continue;
				
				for(String s : e.getValue()){
					//table
					out.write(e.getKey());
					out.write(';');
					//owner
					out.write(TablesEngine.tableAndOwner.get(e.getKey()));
					out.write(';');					
					//program
					out.write(s);
					out.write(';');
					out.newLine();
				}
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
