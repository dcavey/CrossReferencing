package ifs.jl;

import ifs.resources.LocateResource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class CheckReferences extends StreamTokenizer{

	private static final String DBFILE = "db.csv";
	private static final String PROGFILE = "progs.csv";
	private static final String OUTPUTFILE = "output.txt";
	
	private ArrayList<String> databases;
	private ArrayList<String> programs;
	private ArrayList<Integer> unmatchedLines = new ArrayList<Integer>();
	
	public CheckReferences(InputStream in){
		super(in);
		databases = readDbs();
		programs = readProgs();
	}
	
	public void run() throws IOException{
		eolIsSignificant(true);
		int lineNr = -1;
		String currentProg = "";
		HashMap<String,ArrayList<String>> references = new HashMap<String,ArrayList<String>>();
		ArrayList<String> dbs = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		while(nextToken() != StreamTokenizer.TT_EOF){
			if (ttype == StreamTokenizer.TT_EOL) {
				builder = new StringBuilder();
			}
			if (sval != null)
				builder.append(sval);
			if(ttype == StreamTokenizer.TT_WORD){
				if (lineno() != lineNr){
					if(programs.contains(currentProg)){
						references.put(currentProg, dbs);
					}
					if(!references.containsKey(sval)){
						dbs = new ArrayList<String>();
						dbs.clear();
						currentProg = sval;
					} else {
						dbs = references.get(sval);
					}
					lineNr = lineno();
				} else {
					int i = 0;
					while(i < databases.size() && !match(i, builder, lineno())){
						i++;
					}
					if(i != databases.size()){
						if(!dbs.contains(databases.get(i))){
							dbs.add(databases.get(i));
						}
					}
				}
			}
			}
		printOutput(references);
	}

	private boolean match(int i, StringBuilder builder, int lineNr) {
		String line = builder.toString();
		
		boolean match = false;
		
		boolean dt = sval.matches("P" + databases.get(i) + "[a-zA-Z0-9]+");   // was "[0-9]+"
		boolean flag = sval.matches(databases.get(i) + "\\.+.+");
		boolean rest = sval.matches(databases.get(i));
		
		if (dt) {
			if( ( line.matches(".*DT.*")) || (line.matches(".*DETERMINE.*")) ) 
				match = true;
		} else if (flag) {
			if( (line.matches(".*FL.*")) || (line.matches (".*FLAG.*") )     )
				match = true;
		} else if (rest) {
			if(line.matches(".*AUTO\\.ENTRY.*") || line.matches(".*AE.*"))
				match = true;
			else if( line.matches(".*PURGE.*") || line.matches(".*PU.*")) 
				match = true;
		}

		if (match) {
			// System.out.printf ("SELECTING:  %s \n" , line);
		}
					
		if (rest && !match)
		{
			unmatchedLines.add(lineNr);
			System.out.println(lineNr);
		}
		
		return match;
	}
	
	private ArrayList<String> readDbs() {
		ArrayList<String> outputList = new ArrayList<String>();
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream(LocateResource.getResource(DBFILE));
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String[] output = strLine.split(";");
				outputList.add(output[1]);
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return outputList;
	}
	
	private ArrayList<String> readProgs() {
		ArrayList<String> outputList = new ArrayList<String>();
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream(LocateResource.getResource(PROGFILE));
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String[] output = strLine.split(";");
				outputList.add(output[1]);
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return outputList;
	}
	
	public void printOutput(HashMap<String, ArrayList<String>> references) {
		//print output
		try {
			// Create file
			FileWriter fstream = new FileWriter(LocateResource.getResource(OUTPUTFILE));
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<Entry<String, ArrayList<String>>> it = references.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, ArrayList<String>> e = it.next();
				out.write(e.getKey());
				out.newLine();
				for(String s : e.getValue()){
					out.write(s);
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
		
		//print skipped lines
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream("C:/tempSource/ifsprd.mdl");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			int linenr = 0;
			int lnr = 0;
			Iterator<Integer> iterator = unmatchedLines.iterator();
			//
			File newFile = new File("FindXrefs/src/ifs/resources/skippedlines.txt");
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

}
