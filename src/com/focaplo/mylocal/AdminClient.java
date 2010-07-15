package com.focaplo.mylocal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;



public class AdminClient extends TestCase{
	//String server="http://localhost:8082/yardsale?token=" + yardsaleServlet.token;
	String server="http://senselocal.appspot.com/yardsale?token=" + yardsaleServlet.token;
	public void addTestingData(){
		double mylati=37.331689;
		double mylagi=-122.030731;
//		double mylati=42.06577;
//		double mylagi=-88.21957;
		for(int i=0; i<10;i++){
			StringBuffer req=new StringBuffer(server);
			mylati+=0.01; //0.0001=0.01km,0.01-1km
			//mylagi+=0.01; //almost 1 KM
			
			req.append("&command=upload&id=&userId=00000000-0000-1000-8000-001FF3457A22&latitude=" + (BigDecimal.valueOf(mylati)).setScale(6, BigDecimal.ROUND_UP).toString() + "&longitude=" + (BigDecimal.valueOf(mylagi)).setScale(6, BigDecimal.ROUND_UP).toString()+ "&address=test" + i + "&description=test" + i + "&startDate=2009-06-03%2008:44&endDate=2009-06-10%2008:44");
			System.out.println(this.doGet(req.toString()));
		}
	}
	
	public void clearOldData() throws UnsupportedEncodingException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String request="&command=purge&beforeDate=" + URLEncoder.encode("2009-08-01 10:00", "UTF-8");
		
		String req = server +  request;
		System.out.println(this.doGet(req));
	}
	
	public void browseAllData(){
		String req = server +  "&command=browse&start=1&end=100";
		String allData = this.doGet(req);
		System.out.println(allData);
		String[] dataArray = allData.split("\\|");
		for(int i=0;i<dataArray.length;i++){
			System.out.println(dataArray[i]);
		}
	}
	private String doGet(String urlString){
		StringBuffer buf = new StringBuffer();
		BufferedReader br = null;
		URL url = null;
		try {
			System.out.println("sending request:" + urlString);
			
			url = new URL(urlString);
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
}
