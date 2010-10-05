package com.focaplo.mylocal;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

public class NetUtility {
	protected final static Logger log = Logger.getLogger(NetUtility.class);

	public static String doGet(String urlString) throws URISyntaxException{
		//first encode
		URI uri = new URI(urlString);
		StringBuffer buf = new StringBuffer();
		BufferedReader br = null;
		
		try {
			URL url = uri.toURL();
			log.debug("sending request:" + url);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			
			while((line=br.readLine())!=null){
				log.debug(line);
				buf.append(line);
			}
			
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			br=null;
		
		}
		return buf.toString();
	}
	
	public static String doPost(String urlString, String[] params, String[] values) throws Exception{
	    URL                 url;
	    URLConnection   urlConn;
	    DataOutputStream    printout;
	    DataInputStream     input = null;
	    // URL of CGI-Bin script.
	    url = new URL (urlString);
	    // URL connection channel.
	    urlConn = url.openConnection();
	    // Let the run-time system (RTS) know that we want input.
	    urlConn.setDoInput (true);
	    // Let the RTS know that we want to do output.
	    urlConn.setDoOutput (true);
	    // No caching, we want the real thing.
	    urlConn.setUseCaches (false);
	    // Specify the content type.
	    urlConn.setRequestProperty
	    ("Content-Type", "application/x-www-form-urlencoded");
	    // Send POST output.
	    printout = new DataOutputStream (urlConn.getOutputStream ());
	    StringBuffer buffer = new StringBuffer();
	    for(int i=0;i<params.length;i++){
	    	buffer.append("&"+ URLEncoder.encode(params[i],"UTF-8") + "=" + URLEncoder.encode(values[i],"UTF-8"));
	    }
	    buffer.deleteCharAt(0);
	    
	    printout.writeBytes (buffer.toString());
	    printout.flush ();
	    printout.close ();
	    // Get response data.
	    StringBuffer res = new StringBuffer();
	    try{
		    input = new DataInputStream (urlConn.getInputStream ());
		    BufferedReader br = new BufferedReader(new InputStreamReader(input));
		    
		    String str;
		    while (null != ((str = br.readLine())))
		    {
		    res.append(str);
		    }
	    }finally{
	    	input.close ();
	    }
	    return res.toString();
	}

	public static void uploadFile(String urlString, String[] params,
			String[] values, String contentType, String filePath) throws IOException{
		//read the image file bytes
		File file = new File(filePath); 
	      //File length
	      int size = (int)file.length(); 
	      if (size > Integer.MAX_VALUE){
	        log.debug("File is to larger");
	      }
	      byte[] bytes = new byte[size]; 
	      DataInputStream dis = new DataInputStream(new FileInputStream(file)); 
	      int read = 0;
	      int numRead = 0;
	      while (read < bytes.length && (numRead=dis.read(bytes, read,
	                                                bytes.length-read)) >= 0) {
	        read = read + numRead;
	      }
	      log.debug("File size: " + read);
	      // Ensure all the bytes have been read in
	      if (read < bytes.length) {
	        log.debug("Could not completely read: "+file.getName());
	      }
	      //
	      NetUtility.uploadFileContent(urlString, params, values, contentType, filePath.substring(filePath.lastIndexOf(File.separator)+1), bytes);
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
