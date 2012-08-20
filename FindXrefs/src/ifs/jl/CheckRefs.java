package ifs.jl;

import ifs.datamodel.BasicTable;
import ifs.datamodel.Table;
import ifs.program.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class CheckRefs {
	private ArrayList<String> databases;
	private ArrayList<String> programs;
	private ArrayList<String> glPrograms;
	private HashMap<String,ArrayList<Table>> references;
	private HashMap<String, ArrayList<String>> glreferences;
	private HashMap<String, ArrayList<String>> midwarereferences;
	private ArrayList<Integer> unmatchedLines;

	public CheckRefs() {
		databases = readDbs();
		programs = new ArrayList<String>();
		glPrograms = new ArrayList<String>();
		readProgs();
		references = fillReferences(programs);
		glreferences = fillGLReferences(glPrograms);
		midwarereferences = new HashMap<String, ArrayList<String>>();
		unmatchedLines = new ArrayList<Integer>();
	}

	public void run() {
		try {
			FileInputStream fstream = new FileInputStream(Constants.SOURCE_CODE);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			int lineNr = -1;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				lineNr++;
				
				String stringMinusX1 = strLine.replaceAll("  +", " ");
				String stringMinusX2 = stringMinusX1.replaceAll("\\.(?!.*\\.)"," ");
				
				
				String[] text = stringMinusX2.replaceAll("  +", " ").split(" ");
				
				if(strLine.matches(".*DT;.*") || strLine.matches(".*DETERMINE.*") || strLine.matches(".*LU;.*")){
					saveTable(text, Table.DETERMINE);
				} else if(strLine.matches(".*FL;.*") || strLine.matches(".*FLAG.*")){
					saveTable(text, Table.FLAG);
//				} else if(strLine.matches(".*AUTO\\.ENTRY.*") || strLine.matches(".*AE;.*")   ){
				} else if(strLine.matches(".*AUTO\\.ENTRY.*") || strLine.matches(".*AE;.*") || strLine.matches(".*AUTO.*")  ){
					
					/* if(strLine.matches(".*30PIAUTO102         IAUTO.*")) {
						System.out.println("need to look here");  	// Debug anchor point
					} */

					
					saveTable(text, Table.AUTO_ENTRY);
				} else if(strLine.matches(".*PURGE.*") || strLine.matches(".*PU;.*")){
					saveTable(text, Table.PURGE);
				} else {
					boolean passedGL = checkGPrograms(strLine);
					boolean passedMW = checkMiddleware(strLine);
					if(!(passedGL && passedMW)){
						unmatchedLines.add(lineNr);
					}
				}
				checkMiddleware(strLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(String glProg : glPrograms){
			crudInheritanceFromGL(glProg);
		}
		for(String glProg : glPrograms){
			referenceInheritanceFromGL(glProg);
		}
		
		printGPrograms();
		printMWList();
		printOutput(references);
		printSkippedLines();
	}
	
	private void referenceInheritanceFromGL(String glProg){
		ArrayList<String> programs = glreferences.get(glProg);
		ArrayList<String> newPrograms = (ArrayList<String>)programs.clone();
		for(String program : programs){
			if(glPrograms.contains(program)){
				if(!program.equals(glProg)){
					referenceInheritanceFromGL(program);
					ArrayList<String> programsToAdd = glreferences.get(program);
					for(String programToAdd : programsToAdd){
						if(!newPrograms.contains(programToAdd)){
							newPrograms.add(programToAdd);
						}
					}
				}
			}
		}
		glreferences.put(glProg, newPrograms);
	}
	
	private void crudInheritanceFromGL(String glProg) {
		boolean changed = false;
		// Get all programs who use this GL
		ArrayList<String> inherProgs = glreferences.get(glProg);
		// Get all tables used by this GL
		ArrayList<Table> glTables = references.get(glProg);
		if(inherProgs != null && glTables != null){
			// Inheritance for each program used by the GL
			for(String prog : inherProgs){
				ArrayList<Table> toRemove = new ArrayList<Table>();
				ArrayList<Table> currentTables = references.get(prog);
				ArrayList<Table> newTables = new ArrayList<Table>();
				// Push all tables from the GL into the program
				for(Table glTable : glTables){
					int j = 0;
					while(j < currentTables.size() && !currentTables.get(j).getTableName().equals(glTable.getTableName())){
						j++;
					}
					// If the program already uses the table, adapt the crud
					if(j < currentTables.size()){
						Table newTable = new BasicTable(glTable.getTableName(),"");
						Table findTab = currentTables.get(j);
						if(!glTable.getCrudOperation('C').isEmpty() || !findTab.getCrudOperation('C').isEmpty()){
							newTable.pushCrudOperation("C");
						}
						if(!glTable.getCrudOperation('R').isEmpty() || !findTab.getCrudOperation('R').isEmpty()){
							newTable.pushCrudOperation("R");
						}
						if(!glTable.getCrudOperation('U').isEmpty() || !findTab.getCrudOperation('U').isEmpty()){
							newTable.pushCrudOperation("U");
						}
						if(!glTable.getCrudOperation('D').isEmpty() || !findTab.getCrudOperation('D').isEmpty()){
							newTable.pushCrudOperation("D");
						}
						if(!newTable.getCrudOperation().equals(findTab.getCrudOperation())){
							// Update list
							newTables.add(newTable);
							toRemove.add(findTab);
							changed = true;
						}
					} else {
						newTables.add(glTable);
						changed = true;
					}
				}
				// Update the references
				currentTables.removeAll(toRemove);
				newTables.addAll(currentTables);
				references.put(prog, newTables);
				// Check recursion
				if(glPrograms.contains(prog) && changed){
					crudInheritanceFromGL(prog);
				}
			}
		}
	}
	
	private void saveTable(String[] text, String crud) {
		
		ArrayList<String> tables = findDBTables(text, crud);
		
		if (!isLINCProgram(text[0]))  {
			System.out.println("Trying to push a reference for non-LINC program: " + text[0]);
			return;
		}
		
		
		if(crud.equals(Table.FLAG) && tables.size()>1){
			pushInReferences(text[0].substring(2), tables.get(0), Table.DETERMINE);
			pushInReferences(text[0].substring(2), tables.get(1), crud);
		} else if(tables.size() > 1) {
			if( crud.equals(Table.DETERMINE)){
				for(int i=0; i < tables.size(); i++){
					pushInReferences(text[0].substring(2), tables.get(i), crud);
				}
			} else {
				System.err.println("MULTIPLE TABLES ON ONE LINE");
			}
		} else if(!tables.isEmpty()){
			pushInReferences(text[0].substring(2), tables.get(0), crud);
		}				
	}
	
	private void pushInReferences(String program, String table, String crud){
		ArrayList<Table> dbs = new ArrayList<Table>();
			
		dbs = references.get(program);
		if(dbs != null){
			Table table1 = new BasicTable(table,crud);
			if(!dbs.contains(table1)){
				dbs.add(table1);
			} else {
				Iterator<Table> iterator = dbs.iterator();
				while (iterator.hasNext()) {
					Table next = iterator.next();
					if (next.equals(table1)) {
						next.pushCrudOperation(crud);
					}
				}
			}
		}
	}

	private ArrayList<String> findDBTables(String[] text, String crud){
		ArrayList<String> dbs = new ArrayList<String>();
		boolean databasesContainsTable ;
		boolean dbsContainsTable ;
		String trimmedText;
		int i = 1;
		int j = -1;
		boolean found = false;
		while(!found && i<text.length){
			if(text[i].equals(":")){
				break;
			}
			trimmedText = text[i].trim();
			databasesContainsTable = databases.contains(trimmedText);
			dbsContainsTable = dbs.contains(trimmedText);
			
			if(databasesContainsTable){
				if(!dbsContainsTable){
					dbs.add(text[i].trim());
				}
				found = true;
				j = -1;
			} else {
				j = 0;
				while(!found && j < databases.size()){
					if(text[i].trim().matches(databases.get(j) + "\\.+.+") && crud.equals(Table.FLAG)){
						//For FLAG
						found = true;
					} else if(text[i].trim().matches("P" + databases.get(j) + "[a-zA-Z0-9]+.*") && crud.equals(Table.DETERMINE)){
						// ... KEYED access of the table 
						//For DT
						found = true;
					} else if(text[i].trim().matches(".*(" + databases.get(j) + ").*") && crud.equals(Table.DETERMINE)){
						//For LU
						found = true;
					} else {
						j++;
					}
				}
				i++;
			}
			if(found && j!=-1){
				if(!dbs.contains(databases.get(j))){
					dbs.add(databases.get(j));
				}
				found = false;
			}
		}
		return dbs;
	}
	
	private boolean checkGPrograms(String strLine){
		CSVWriter csvW = new CSVWriter();
		if (strLine.matches(".*INSERT.*")){
			strLine.replace("  +", " ");
			String[] strWords = strLine.split(" "); 
			for(int i = 1; i < strWords.length; i++){
				if(glPrograms.contains(strWords[i]) && programs.contains(strWords[0].substring(2))){
					if(!glreferences.get(strWords[i]).contains(strWords[0].substring(2))){
						glreferences.get(strWords[i]).add(strWords[0].substring(2));
						//csvW.writeLineToFile(Constants.GPROGOUTPUT, "GLOBAL_LOGIC;"+strWords[i]+";PROGRAM;"+strWords[0].substring(2));
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void printGPrograms(){
		CSVWriter csvW = new CSVWriter();
		Set<String> glprogs = glreferences.keySet();
		for(String glprog : glprogs){
			ArrayList<String> references = glreferences.get(glprog);
			for(String reference : references){
				csvW.writeLineToFile(Constants.GPROGOUTPUT, "GLOBAL_LOGIC;"+glprog+";PROGRAM;"+reference);
			}
		}
	}
	
	private boolean checkMiddleware(String strLine){
		CSVWriter csvW = new CSVWriter();
		if ( (strLine.contains("BP-") ) &&  (strLine.contains("INS") ) ){
			int beginIndex = strLine.indexOf("BP-"); 
			int endIndex = beginIndex + 18;
			int endIndexBU = strLine.indexOf(" ", beginIndex);
			if(endIndexBU != -1 && endIndexBU < endIndex){
				endIndex = endIndexBU;
			}
			String callee= strLine.substring(beginIndex, endIndex).trim();
			
			String tempString = strLine.substring(2,19); 
			endIndex = tempString.indexOf (" ");
			endIndexBU = tempString.indexOf(";");
			if(endIndexBU != -1 && endIndexBU < endIndex){
				endIndex = endIndexBU;
			}
			String caller = strLine.substring(2, 2+endIndex).trim();
			
			fillMiddlewareList(callee,caller);
			return true;
		} else if (strLine.contains   ("IFSYS/WF/TDFXFB")) {
			fillMiddlewareList("DEFAULT XFB-routine", strLine.substring(2,12));
			return true;
		} else {
			return false;
		}
	}

	private void fillMiddlewareList(String caller, String callee){
		ArrayList<String> tmpList;
		if(midwarereferences.containsKey(caller)){
			tmpList = midwarereferences.get(caller);
		} else {
			tmpList = new ArrayList<String>();
		}
		if(!tmpList.contains(callee)){
			tmpList.add(callee);
			midwarereferences.put(caller, tmpList);
		}
	}
	
	private void printMWList(){
		CSVWriter csvW = new CSVWriter();
		Set<String> callees = midwarereferences.keySet();
		for(String callee : callees){
			if(callee.equals("DEFAULT XFB-routine")){
				ArrayList<String> callers = midwarereferences.get(callee);
				if(callers!=null){
					for(String caller : callers){
						//System.out.printf ("XFB: CALLER=%s; CALLEE=%s; TACSYTYPE=%s \n", caller, callee, "-" );
						csvW.writeLineToFile(Constants.MWOUTPUT, "XFB;CALLER;" + caller + ";CALLEE;" + callee + ";TACSYTYPE;" + "-");
					}
				}
			} else {
				ArrayList<String> callers = glreferences.get(callee);
				if(callers!=null){
					for(String caller : callers){
						String tacsyType;
						if (callee.contains("SAG")) {
							tacsyType = "SAGE";
						} else {
							tacsyType = "EASY";
						}
						//System.out.printf ("TACSY: CALLER=%s; CALLEE=%s; TACSYTYPE=%s \n", caller, callee, tacsyType );
						csvW.writeLineToFile(Constants.MWOUTPUT, "TACSY;CALLER;" + caller + ";CALLEE;" + callee + ";TACSYTYPE;" + tacsyType);
					}
				}
			}
		}
	}
	
	private ArrayList<String> readDbs() {
		ArrayList<String> outputList = new ArrayList<String>();
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream(Constants.TABLEFILE);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String[] output = strLine.split(";");
				outputList.add(output[0]);
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return outputList;
	}

	private void readProgs() {
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream(Constants.PROGFILE);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String[] output = strLine.split(";");
				programs.add(output[1]);
				if(output[0].equals("G")){
					glPrograms.add(output[1]);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	private HashMap<String,ArrayList<Table>> fillReferences(ArrayList<String> progs){
		HashMap<String, ArrayList<Table>> refs = new HashMap<String, ArrayList<Table>>();
		for(int i = 0; i < progs.size(); i++){
			ArrayList<Table> tabs = new ArrayList<Table>();
			refs.put(progs.get(i), tabs);
		}
		return refs;
	}
	
	private HashMap<String,ArrayList<String>> fillGLReferences(ArrayList<String> glprogs){
		HashMap<String, ArrayList<String>> refs = new HashMap<String, ArrayList<String>>();
		for(int i = 0; i < glprogs.size(); i++){
			ArrayList<String> progs = new ArrayList<String>();
			refs.put(glprogs.get(i), progs);
		}
		return refs;
	}
	
	public void printOutput(HashMap<String,ArrayList<Table>> references) {
		//print output
		try {
			// Create file
			FileWriter fstream = new FileWriter(Constants.TEXTOUTPUTFILE);
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<Entry<String, ArrayList<Table>>> it = references.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, ArrayList<Table>> e = it.next();
				out.write(e.getKey());
				out.newLine();
				for(Table s : e.getValue()){
					out.write(s.getTableName() + "," + s.getCrudOperation());
					out.newLine();
				}
				out.write("----------------------------");
				out.newLine();
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Function prints all lines that where not included in the output. These lines are used to validate if no usefull info
	 * was emitted from the output.
	 */
	private void printSkippedLines() {
		//print skipped lines
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream(Constants.SOURCE_CODE);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			int linenr = 0;
			int lnr = 0;
			Iterator<Integer> iterator = unmatchedLines.iterator();
			//
			File newFile = new File(Constants.SKIPPEDLINES);
			newFile.createNewFile();
			FileWriter filestream = new FileWriter(newFile);
			BufferedWriter out = new BufferedWriter(filestream);
			// Read File Line By Line
			boolean proceedIterator = true;
			while ((strLine = br.readLine()) != null) {
				
				
				if (iterator.hasNext() && proceedIterator) {
					Integer integer = iterator.next();
					lnr = integer.intValue();
					while (lnr == linenr-1) {
						integer = iterator.next();
						lnr = integer.intValue();
					}
					proceedIterator = false;
				}
				
				if (lnr == linenr) {
						out.write(strLine);
						out.newLine();
						proceedIterator = true;
				}
				linenr++;
			}
			// Close the input stream
			in.close();
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	private boolean isLINCProgram(String program) {
		
		boolean thisIsALincProgram = false;
		
		if ( (program.startsWith("10")) ||		// ISPEC
			 (program.startsWith("30")) ||      // don't know what this is -- don't need them
 			 (program.startsWith("40")) ||
			 (program.startsWith("41")) ||
			 (program.startsWith("42")) ||
			 (program.startsWith("43")) ||
			 (program.startsWith("44")) ||
			 (program.startsWith("50")) ||  	// REPORT
			 (program.startsWith("60")) ||      // PROFILE 		==> don't need them  		 
			 (program.startsWith("70")) ||  	// GLOBAL LOGIC
			 (program.startsWith("71")) ||  
			 (program.startsWith("90")) 
			 ) {
			thisIsALincProgram = true;	
		} 
		
		if ( program.startsWith("30P") ) {
				thisIsALincProgram = false;		// trying to get rid of PIAUTO102 ;-)	
			} 

		return thisIsALincProgram;
	}
	
}
