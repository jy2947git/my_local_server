package com.focaplo.mylocal.sale.client;

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

import org.apache.log4j.Logger;
import org.junit.Test;

import com.focaplo.mylocal.sale.model.Sale;
import com.focaplo.mylocal.sale.server.yardsaleServlet;
import com.focaplo.mylocal.sale.service.SaleService;
import com.focaplo.mylocal.utils.NetUtility;

public class AdminClient{
	//String server="http://localhost:8082/yardsale?token=" + yardsaleServlet.token;
	String server="http://senselocal.appspot.com/yardsale";
	protected final Logger log = Logger.getLogger(this.getClass());
	private Sale createSaleItem(int i) throws ParseException{
		Sale item = new Sale();
		item.setUserUniqueId("seller"+i);
		item.setAddress1("2200 Yale Cir");
		item.setCity("Hoffman Estates");
		item.setState("IL");
		item.setZipcode("60192");
		item.setCountryCode("US");
		item.setDetail("this is a test"+i);
//		double mylati=37.331689;
//		double mylagi=-122.030731;
//		double mylati=42.06577;
//		double mylagi=-88.21957;
		double mylati=41.881111;
		double mylagi=-87.632371;
		double deltaLati=0.01; //0.0001=0.01km,0.01-1km
		//mylagi+=0.01; //almost 1 KM
		item.setLatitude(Double.valueOf(mylati+i*deltaLati));
		item.setLongitude(Double.valueOf(mylagi));
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, 13);
		item.setStartDateDate(beginDate.getTime());
		item.setEndDateDate(endDate.getTime());
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
			Sale si = this.createSaleItem(i);
			StringBuffer req=new StringBuffer(server);
			mylati+=0.01; //0.0001=0.01km,0.01-1km
			//mylagi+=0.01; //almost 1 KM
			
		
			String jsonAddItem = NetUtility.doPost(server, new String[]{"token", "command","data"}, new String[]{yardsaleServlet.token,"upload", service.toJson(si)});
			//now upload the image
			
		}
	}
	
	public void clearOldData() throws UnsupportedEncodingException, URISyntaxException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String request="&command=purge&beforeDate=" + URLEncoder.encode("2009-08-01 10:00", "UTF-8");
		
		String req = server +  request;
		System.out.println(NetUtility.doGet(req));
	}
	@Test
	public void browseAllData() throws URISyntaxException{
		String req = server +  "?token="+yardsaleServlet.token+"&command=browse&start=1&end=100";
		String allData = NetUtility.doGet(req);
		System.out.println(allData);
		
	}
	
	
//	private String doPost(String urlString, String command, String data) throws Exception{
//	    URL                 url;
//	    URLConnection   urlConn;
//	    DataOutputStream    printout;
//	    DataInputStream     input;
//	    // URL of CGI-Bin script.
//	    url = new URL (urlString);
//	    // URL connection channel.
//	    urlConn = url.openConnection();
//	    // Let the run-time system (RTS) know that we want input.
//	    urlConn.setDoInput (true);
//	    // Let the RTS know that we want to do output.
//	    urlConn.setDoOutput (true);
//	    // No caching, we want the real thing.
//	    urlConn.setUseCaches (false);
//	    // Specify the content type.
//	    urlConn.setRequestProperty
//	    ("Content-Type", "application/x-www-form-urlencoded");
//	    // Send POST output.
//	    printout = new DataOutputStream (urlConn.getOutputStream ());
//	    String content =
//	    "token=" + URLEncoder.encode (yardsaleServlet.token) +
//	    "&command="+command +
//	    "&data=" + URLEncoder.encode (data);
//	    printout.writeBytes (content);
//	    printout.flush ();
//	    printout.close ();
//	    // Get response data.
//	    input = new DataInputStream (urlConn.getInputStream ());
//	    StringBuffer res = new StringBuffer();
//	    String str;
//	    while (null != ((str = input.readLine())))
//	    {
//	    res.append(str);
//	    }
//	    input.close ();
//	    return res.toString();
//	}
}
