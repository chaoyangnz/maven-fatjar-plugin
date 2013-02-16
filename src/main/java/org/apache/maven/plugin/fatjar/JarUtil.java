/*
 *  Copyright 2009-2010 The Inframesh Software Laboratory (ISL)
 *
 *  Licensed under the Inframesh Free Software License (the "License"), 
 *	Version 1.0 ; you may obtain a copy of the license at
 *
 *  	http://www.inframesh.org/licenses/LICENSE-1.0
 *
 *  Software distributed under the License is distributed  on an "AS IS" 
 *  BASIS but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the License 
 *  for more details.
 *
 *  Inframesh, Websquare, Jex are all reserved marks.
 */
package org.apache.maven.plugin.fatjar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @since maven-fatjar-plugin
 * @version $Revision: 1.0 $Date:2010-2-7 ÏÂÎç03:13:41 $
 * 
 * @author <a href="mailto:ychao@bankcomm.com">Josh Yang</a>
 */
public class JarUtil {
	
	public static void decompress(String filepath, String outpath) throws IOException {
		decompress(filepath, outpath, null);
	}
	
	public static void decompress(String filepath, File outpathdir, String includes) throws IOException {
		decompress(filepath, outpathdir.getAbsolutePath(), includes);
	}
	
	public static void decompress(String filepath, String outpath, String includes) throws IOException {
		if (!outpath.endsWith(File.separator)) {  
			outpath += File.separator;  
        }
		
		String regex = toRegex(includes);
		JarFile jar = new JarFile(filepath);

		Enumeration<JarEntry> entries = jar.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if(entry.getName().matches(regex)) {
				String outFileName = outpath + entry.getName();
				File f = new File(outFileName);
				if (outFileName.endsWith("/") || outFileName.endsWith("\\") || outFileName.endsWith(File.separator)) {  
	                f.mkdir();  
				} else {
					InputStream in = jar.getInputStream(entry);  					  
	                OutputStream out = new BufferedOutputStream(new FileOutputStream(f));  
	  
	                byte[] buffer = new byte[2048];    
	                int nBytes = 0;  
	  
	                while ((nBytes = in.read(buffer)) > 0) {    
	                    out.write(buffer, 0, nBytes);    
	                }  
	  
	                out.flush();  
	                out.close();    
	                in.close(); 
				}
			}
		}
	}

	/**
	 * @param includes
	 * @return
	 */
	private static String toRegex(String includes) {
		return includes == null ? ".*" : includes.replace(".","/").replace("*", ".*");
	}
	
	public static void main(String[] args) throws IOException {
		decompress("D:\\inframesh-jex-1.0.0-fatjar.jar", "D:\\1", "org.*");
	}
}
