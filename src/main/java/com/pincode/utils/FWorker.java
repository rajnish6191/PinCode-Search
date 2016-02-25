package com.pincode.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FWorker {

	private static final Logger LOG = Logger.getLogger(FWorker.class);

	private String path;
	private FileWriter fw;
	private File file;

	public FWorker(String path) throws IOException {
		this.path = path;
		file = new File(path);
		if(file.exists() && file.isFile()){
			file.delete();
		}
	}

	/**
	 * Write to file
	 * 
	 * @param append
	 *            - append mode if "true"
	 * @throws IOException
	 */
	public void write(String string) {
		try {
			fw = new FileWriter(file, true);
			fw.write(string+"\r\n");
		} catch (IOException e) {
			LOG.error("Write error -> " + e);
		} finally{
			try {
				fw.close();
			} catch (IOException e1) {
			}
		}
	}
	
	public static List<String> readline(String file) {
		List<String> str = new ArrayList<String>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOG.info(e);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		
		String line = null;
        try {
			while ((line=reader.readLine())!=null) {
				str.add(line);
			}
		} catch (IOException e) {
			LOG.error(e);
		}finally {
			try {
				reader.close();
				fis.close();
			} catch (IOException e1) {
				LOG.error(e1);
			}
		}
        
        return str;
	}
}
