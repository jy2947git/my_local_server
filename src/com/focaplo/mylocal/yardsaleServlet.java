package com.focaplo.mylocal;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class yardsaleServlet extends HttpServlet {
	public static String token="29073429202020128953";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println(req.getQueryString());
		String token = req.getParameter("token");
		if(token==null || !token.equalsIgnoreCase(token)){
			return;
		}
		try {
			serve(req, resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String token = req.getParameter("token");
		if(token==null || !token.equalsIgnoreCase(token)){
			return;
		}
//		log.warning(req.getQueryString());
		try {
			serve(req, resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void serve(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
		String command=req.getParameter("command");
		if(command==null){
			return;
		}
		//
		if(command.equalsIgnoreCase("upload")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.upload(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else if(command.equalsIgnoreCase("browse")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.browse(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else if(command.equalsIgnoreCase("download")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.download(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else if(command.equalsIgnoreCase("delete")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.delete(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else if(command.equalsIgnoreCase("purge")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.purge(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else{
		}
	}
	
	private String purge(HttpServletRequest req, HttpServletResponse resp) throws ParseException {
		String beforeDate = req.getParameter("beforeDate");
		SaleService service = new SaleService();
		service.deleteOldRecordsBeforeDate(sdf.parse(beforeDate));
		return "SUCCESS";
	}
	private String delete(HttpServletRequest req, HttpServletResponse resp) {
		String id = req.getParameter("id");
		SaleService service = new SaleService();
		if(id!=null){
			service.deleteItem(Integer.parseInt(id));
			return "SUCCESS";
		}else{
			return "";
		}
	}
	private String download(HttpServletRequest req, HttpServletResponse resp)  throws ParseException{
		String id = req.getParameter("id");
		SaleService service = new SaleService();
		SaleItem item = service.getItem(Integer.parseInt(id));

		return "SUCCESS" + "^" +item.getItemId() + "^" + item.getUserUniqueId() + "^" + (item.getAddress()==null?"":item.getAddress()) + "^" + (item.getDescription()==null?"":item.getDescription()) + "^" + sdf.format(item.getStartDate()) + "^" + sdf.format(item.getEndDate()) + "^" + item.getLatitude() + "^" + item.getLongitude();
	}
	private String browse(HttpServletRequest req, HttpServletResponse resp) throws ParseException {
		String latitude = req.getParameter("latitude");
		String longitude = req.getParameter("longitude");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		SaleService service = new SaleService();
		List<SaleItem> results = null;
		if(latitude!=null && longitude!=null){
			results = service.searchItemByLocationAndDateWithPage(Integer.parseInt(start), Integer.parseInt(end), Double.parseDouble(latitude), Double.parseDouble(longitude));
		}else{
			results = service.browseWithPage(Integer.parseInt(start), Integer.parseInt(end));
		}
		StringBuffer buf = new StringBuffer("SUCCESS");
		for(int i=0;i<results.size();i++){
			//id^address^desecription^startDate^endDate|id2...
			SaleItem item = (SaleItem)results.get(i);
			buf.append("|");
			buf.append(item.getItemId() + "^" + item.getUserUniqueId() + "^" + (item.getAddress()==null?"":item.getAddress()) + "^" + (item.getDescription()==null?"":item.getDescription()) + "^" + sdf.format(item.getStartDate()) + "^" + sdf.format(item.getEndDate()) + "^" + item.getLatitude() + "^" + item.getLongitude());

		}
		return buf.toString();
	}
	private String upload(HttpServletRequest req, HttpServletResponse resp) throws ParseException {
		String itemId=req.getParameter("id");
		String userId=req.getParameter("userId");
		String address=req.getParameter("address");
		String description=req.getParameter("description");
		String latitude = req.getParameter("latitude");
		String longitude = req.getParameter("longitude");
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		
		SaleService service = new SaleService();

		String id = service.saveItem(itemId,userId,address,description,latitude,longitude,startDate,endDate);
		return id + "|" + "SUCCESS";
	}
}
