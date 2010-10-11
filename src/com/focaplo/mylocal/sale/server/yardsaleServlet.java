package com.focaplo.mylocal.sale.server;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.focaplo.mylocal.sale.model.Sale;
import com.focaplo.mylocal.sale.service.SaleService;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Builder;

@SuppressWarnings("serial")
public class yardsaleServlet extends HttpServlet {
	protected final Logger log = Logger.getLogger(this.getClass());
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
	
	private void enqueue(HttpServletRequest req, HttpServlet res){
		Queue queue = QueueFactory.getQueue("default");
		TaskOptions taskOptions = Builder.url("/yardsale");
		Enumeration<String> parameterNames = req.getParameterNames();
		while(parameterNames.hasMoreElements()){
			String parameterName = parameterNames.nextElement();
			String parameterValue = (String)req.getParameter(parameterName);
			taskOptions.param(parameterName, parameterValue);
		}
		queue.add(taskOptions);
		
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
		}else if(command.equalsIgnoreCase("images")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.getImagesOfSale(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else if(command.equalsIgnoreCase("deleteImage")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.deleteImageOfSale(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else if(command.equalsIgnoreCase("finalizeSale")){
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			String result = this.finalizeSale(req, resp);
	        resp.getWriter().println(result);
	        resp.getWriter().flush();
		}else{
		}
	}
	
	
	private String getImagesOfSale(HttpServletRequest req,
			HttpServletResponse resp) {
		SaleService service = new SaleService();
		String saleId = req.getParameter("id");
		if(saleId!=null&&!saleId.equalsIgnoreCase("")){
			return service.getJsonOfSaleImages(new Long(saleId));
		}
		return service.errorResultToJson(new IllegalArgumentException("id is required"));
	}
	
	private String deleteImageOfSale(HttpServletRequest req, HttpServletResponse res){
		SaleService service = new SaleService();
		String imageId = req.getParameter("imageId");
		if(imageId!=null && !imageId.equalsIgnoreCase("")){
			return service.deleteImageSoft(new Long(imageId));
		}
		return service.errorResultToJson(new IllegalArgumentException("id is required"));
	}
	private String purge(HttpServletRequest req, HttpServletResponse resp) throws ParseException {
		String beforeDate = req.getParameter("beforeDate");
		SaleService service = new SaleService();
		return service.deleteOldRecordsBeforeDate(sdf.parse(beforeDate));
		
	}
	private String delete(HttpServletRequest req, HttpServletResponse resp) {
		String id = req.getParameter("id");
		SaleService service = new SaleService();
		if(id!=null){
			return service.deleteSaleSoft(new Long(id));
		}else{
			return service.errorResultToJson(new IllegalArgumentException("id is required"));
		}
	}
	private String download(HttpServletRequest req, HttpServletResponse resp)  throws ParseException{
		String id = req.getParameter("id");
		SaleService service = new SaleService();
		return service.getSale(new Long(id));
		
	}
	private String browse(HttpServletRequest req, HttpServletResponse resp) throws ParseException {
		String latitude = req.getParameter("latitude");
		String longitude = req.getParameter("longitude");
		String start = req.getParameter("start");
		if(start==null || start.equalsIgnoreCase("")){
			start="0";
		}
		String end = req.getParameter("end");
		if(end==null || end.equalsIgnoreCase("")){
			end="20";
		}
		SaleService service = new SaleService();
		List<Sale> results = null;
		if(latitude!=null && longitude!=null){
			return service.searchSaleByLocationAndDateRange(Integer.parseInt(start), Integer.parseInt(end), Double.parseDouble(latitude), Double.parseDouble(longitude));
		}else{
			return service.browseWithPage(Integer.parseInt(start), Integer.parseInt(end));
		}

	}
	private String upload(HttpServletRequest req, HttpServletResponse resp) throws ParseException {
		String jsonOfSale = req.getParameter("data");
		//upload json string of Sale without any image data
		SaleService service = new SaleService();
		Sale item = service.fromJson(jsonOfSale);
		//note the initial status of the sale is "pending". It becomes "valid" after the user issue the "finalizeSale" request
		//the reason is, in our iphone app, once user click the "add sale" button, we will get his location and immediately
		//upload a sale to our server (in pending status). User can proceed to take picture and upload images (with the sale id)
		//and after he clicks the "done" button, the sale becomes valid
		//we might change this rule later in which case just comment below line
		item.setStatus("pending");
		return service.saveSale(item);

	}
	private String finalizeSale(HttpServletRequest req, HttpServletResponse resp) {
		SaleService service = new SaleService();
		String saleId = req.getParameter("id");
		if(saleId!=null&&!saleId.equalsIgnoreCase("")){
			return service.updateSaleStatus(new Long(saleId), "valid");
		}
		return service.errorResultToJson(new IllegalArgumentException("id is required"));
	}
}
