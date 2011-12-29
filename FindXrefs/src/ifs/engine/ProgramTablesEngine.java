/**
 * 
 */
package ifs.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author TDEWEERD
 *
 */
public class ProgramTablesEngine {

	
	public static HashMap<String, ArrayList<String>> generateTableToProgramsMapping(File file) {
		HashMap<String, ArrayList<String>> tables = new HashMap<String, ArrayList<String>>();
		
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		String program = null;
		
		boolean readingProgram = true;
		
		//read each line of text file
		try {
			while((line = bufRdr.readLine()) != null)
			{
				if (line.equals("----------------------------")) {
					readingProgram = true;
					continue;
				}
				if (readingProgram) {
					program = line;
					readingProgram = false;
				} else {
					ArrayList<String> programs = null;
					if(tables.containsKey(line)) {
						programs = tables.get(line);
					} else {
						programs = new ArrayList<String>();
					}
					programs.add(program);
					tables.put(line, programs);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//close the file
		try {
			if (bufRdr != null)
				bufRdr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tables;
	}
}
