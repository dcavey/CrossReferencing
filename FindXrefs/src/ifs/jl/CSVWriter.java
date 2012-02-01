package ifs.jl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author JLIEBAER
 *
 */
public class CSVWriter {

	public void writeLineToFile(String filename, String lineToWrite) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(filename, true));
			pw.println(lineToWrite);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}
}