package com.focaplo.mylocal;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class NetUtility {
	protected final static Logger log = Logger.getLogger(NetUtility.class);

	// public static void uploadFile(String url, String[] params, String[]
	// values, String fileName, byte[] data) throws HttpException, IOException{
	//
	// PostMethod filePost = new PostMethod(url);
	// ByteArrayPartSource baps = new ByteArrayPartSource(fileName, data);
	// Part[] parts = new Part[params.length+1];
	// for(int i=0;i<params.length;i++){
	// parts[i] = new StringPart(params[i], values[i]);
	// }
	// parts[parts.length-1] = new FilePart("myFile", baps);
	//
	// filePost.setRequestEntity(
	// new MultipartRequestEntity(parts, filePost.getParams())
	// );
	// HttpClient client = new HttpClient();
	// int status = client.executeMethod(filePost);
	// log.info("uploaded file " + fileName + " status " + status);
	// }

	public static void uploadFile(String urlString, String[] params,
			String[] values, String contentType, String filePath) throws IOException{
		//read the image file bytes
		File file = new File(filePath); 
	      //File length
	      int size = (int)file.length(); 
	      if (size > Integer.MAX_VALUE){
	        System.out.println("File is to larger");
	      }
	      byte[] bytes = new byte[size]; 
	      DataInputStream dis = new DataInputStream(new FileInputStream(file)); 
	      int read = 0;
	      int numRead = 0;
	      while (read < bytes.length && (numRead=dis.read(bytes, read,
	                                                bytes.length-read)) >= 0) {
	        read = read + numRead;
	      }
	      System.out.println("File size: " + read);
	      // Ensure all the bytes have been read in
	      if (read < bytes.length) {
	        System.out.println("Could not completely read: "+file.getName());
	      }
	}
	
	public static void uploadFileContent(String urlString, String[] params,
			String[] values, String contentType, String fileName, byte[] data) throws IOException {
		log.info("trying to upload file to " + urlString + " file name:" + fileName);
		URL url = new URL(urlString);
		// create a boundary string
		String boundary = MultiPartFormOutputStream.createBoundary();
		URLConnection urlConn = MultiPartFormOutputStream.createConnection(url);
		urlConn.setReadTimeout(15000);
		urlConn.setRequestProperty("Accept", "*/*");
		urlConn.setRequestProperty("Content-Type",
				MultiPartFormOutputStream.getContentType(boundary));
		// set some other request headers...
		urlConn.setRequestProperty("Connection", "Keep-Alive");
		urlConn.setRequestProperty("Cache-Control", "no-cache");
		// no need to connect because getOutputStream() does it
		MultiPartFormOutputStream out = new MultiPartFormOutputStream(
				urlConn.getOutputStream(), boundary);
		// write a text field element - This should be removed - left over from
		// multipart library demo code
		for(int i=0;i<params.length;i++){
			out.writeField(params[i], values[i]);
		}
		// write bytes directly
		out.writeFile("myFile",contentType, fileName, data);
		out.close();

		// read response from server
		InputStream in = urlConn.getInputStream();
		BufferedReader responseIn = new BufferedReader(new InputStreamReader(
				in));
		StringBuilder redirectResponse = new StringBuilder();
		String line = "";
		while ((line = responseIn.readLine()) != null) {
			redirectResponse.append(line);
		}
		in.close();
	}
}
