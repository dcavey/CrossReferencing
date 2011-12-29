/**
 * 
 */
package ifs.engine;

import ifs.datamodel.BasicProgram;
import ifs.datamodel.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * @author TDEWEERD
 *
 */
public class ProgramsEngine {

	public static Collection<Program> getProgramsCollection(File file) {
		Collection<Program> programs = new ArrayList<Program>();
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		 
		//read each line of text file
		try {
			while((line = bufRdr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,";");
				int col = 0;
				
				String programName = "";
				String programDescription = "";
whileLoop:
				while (st.hasMoreTokens())
				{
					//get next token and store it in the array
					switch(col) {
					case 0:
						st.nextToken();
						break;
					case 1:
						programName = (String)st.nextToken();
						break;
					case 2:
						programDescription = (String)st.nextToken();
						break;
					case 3:
						break whileLoop;
					}
					col++;
				}
				Program program = new BasicProgram(programName, programDescription);
				programs.add(program);
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
		return programs;
	}
}
