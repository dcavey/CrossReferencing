/**
 * 
 */
package ifs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;


/**
 * @author TDEWEERD
 *
 */
public class LocateResource {

	/**
	 * Retrieves a resource path + filename
	 * @param filename filename to retrieve
	 * @return path + filename
	 * @throws UnsupportedEncodingException on wrong url
	 */
	public static String getResource(String filename) throws UnsupportedEncodingException {
		URL resource = LocateResource.class.getResource(filename);
		return URLDecoder.decode(resource.getPath(),"utf-8");
	}
}
