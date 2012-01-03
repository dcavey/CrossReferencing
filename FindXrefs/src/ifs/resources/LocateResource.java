/**
 * 
 */
package ifs.resources;

import java.net.URL;


/**
 * @author TDEWEERD
 *
 */
public class LocateResource {

	/**
	 * Retrieves a resource path + filename
	 * @param filename filename to retrieve
	 * @return path + filename
	 */
	public static String getResource(String filename) {
		URL resource = LocateResource.class.getResource(filename);
		return resource.getPath();
	}
}
