package com.focaplo.mylocal;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;

public class AdminClient{
	//String server="http://localhost:8082/yardsale?token=" + yardsaleServlet.token;
	String server="http://senselocal.appspot.com/yardsale";
	
	private SaleItem createSaleItem(int i) throws ParseException{
		SaleItem item = new SaleItem();
		item.setUserUniqueId("seller"+i);
		item.setAddress1("2200 Yale Cir");
		item.setCity("Hoffman Estates");
		item.setState("IL");
		item.setZipcode("60192");
		item.setCountryCode("US");
		item.setDescription("this is a test"+i);
//		double mylati=37.331689;
//		double mylagi=-122.030731;
		double mylati=42.06577;
		double mylagi=-88.21957;
		double deltaLati=0.01; //0.0001=0.01km,0.01-1km
		//mylagi+=0.01; //almost 1 KM
		item.setLatitude(Double.valueOf(mylati+i*deltaLati));
		item.setLongitude(Double.valueOf(mylagi));
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, 13);
		item.setStartDateDate(beginDate.getTime());
		item.setEndDateDate(endDate.getTime());
		
		item.getImages().add("http://images.google.com/"+i);
		item.getImages().add("http://images.google.com/2"+i);
		item.getImages().add("http://images.google.com/3"+i);
		return item;
	}
	
	@Test
	public void addTestingData() throws Exception{
		double mylati=37.331689;
		double mylagi=-122.030731;
//		double mylati=42.06577;
//		double mylagi=-88.21957;
		SaleService service = new SaleService();
		for(int i=0; i<10;i++){
			SaleItem si = this.createSaleItem(i);
			StringBuffer req=new StringBuffer(server);
			mylati+=0.01; //0.0001=0.01km,0.01-1km
			//mylagi+=0.01; //almost 1 KM
			
		
			this.doPost(server, "upload", service.toJson(si));
		}
	}
	
	public void clearOldData() throws UnsupportedEncodingException, URISyntaxException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String request="&command=purge&beforeDate=" + URLEncoder.encode("2009-08-01 10:00", "UTF-8");
		
		String req = server +  request;
		System.out.println(this.doGet(req));
	}
	@Test
	public void browseAllData() throws URISyntaxException{
		String req = server +  "?token="+yardsaleServlet.token+"&command=browse&start=1&end=100";
		String allData = this.doGet(req);
		System.out.println(allData);
		
	}
	private String doGet(String urlString) throws URISyntaxException{
		//first encode
		URI uri = new URI(urlString);
		StringBuffer buf = new StringBuffer();
		BufferedReader br = null;
		
		try {
			URL url = uri.toURL();
			System.out.println("sending request:" + url);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			
			while((line=br.readLine())!=null){
				System.out.println(line);
				buf.append(line);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			br=null;
		
		}
		return buf.toString();
	}
	
	private void doPost(String urlString, String command, String data) throws Exception{
	    URL                 url;
	    URLConnection   urlConn;
	    DataOutputStream    printout;
	    DataInputStream     input;
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
	    String content =
	    "token=" + URLEncoder.encode (yardsaleServlet.token) +
	    "&command="+command +
	    "&data=" + URLEncoder.encode (data);
	    printout.writeBytes (content);
	    printout.flush ();
	    printout.close ();
	    // Get response data.
	    input = new DataInputStream (urlConn.getInputStream ());
	    String str;
	    while (null != ((str = input.readLine())))
	    {
	    System.out.println (str);
	    }
	    input.close ();
	}
}
