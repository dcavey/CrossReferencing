/**
 * 
 */
package ifs.engine;

import ifs.datamodel.BasicOwner;
import ifs.datamodel.BasicTable;
import ifs.datamodel.Owner;
import ifs.datamodel.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author TDEWEERD
 *
 */
public class TablesEngine {

	public static HashMap<String,String> tableAndOwner = new HashMap<String, String>();
	
	public static Collection<Table> getTablesCollection(File file) {
		Collection<Table> tables = new ArrayList<Table>();
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
				
				String tableName = "";
				String tableDescription = "";
				String ownerName = "";
				while (st.hasMoreTokens())
				{
					//get next token and store it in the array
					switch(col) {
					case 0:
						tableName = (String)st.nextToken();
						break;
					case 1:
						tableDescription = (String)st.nextToken();
						break;
					case 2:
						ownerName = (String)st.nextToken();
						break;
					default:
						st.nextToken();
						break;
					}
					col++;
				}
				Owner owner = new BasicOwner(ownerName);
				Table table = new BasicTable(tableName,owner, tableDescription);
				tables.add(table);
				tableAndOwner.put(tableName,ownerName);
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
