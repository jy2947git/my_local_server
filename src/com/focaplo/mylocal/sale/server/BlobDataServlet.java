package com.focaplo.mylocal.sale.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import com.focaplo.mylocal.sale.model.ImageInfo;
import com.focaplo.mylocal.sale.service.RequestResult;
import com.focaplo.mylocal.sale.service.SaleService;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

/**
 * 
 */
public class BlobDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected final Logger log = Logger.getLogger(this.getClass());
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * This method is called by GAE after data is uploaded. It is a call-back.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		log.info("this is after GAE saved the uploaded blob to blobstore");
		String saleId = req.getParameter("saleId");
		log.info("item=" + saleId);
		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("myFile");

		log.info("blob key:" + blobKey.getKeyString());
		//save the item-image
		SaleService saleService = new SaleService();
		String jsonResOfImageInfo = saleService.saveSaleImage(new Long(saleId), blobKey.getKeyString());
		//find the new image-id, need it to save the icon-image later
		Gson gson = new Gson();
		Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
		RequestResult<ImageInfo> rr = gson.fromJson(jsonResOfImageInfo, parameterizedType);
		if("good".equalsIgnoreCase(rr.getStatus())){
			ImageInfo image = rr.getData().get(0);
			Long imageId = image.getImageId();
			// start a backend task to trim the small
			// size image
			this.enqueueIconImageResizing(saleId, imageId, blobKey.getKeyString());
		}
		
		// redirct to blob list page for testing purpose
		//res.sendRedirect("/BlobDataServlet");
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");
        res.getWriter().println(jsonResOfImageInfo);
        res.getWriter().flush();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * Get method to list all the existing blob data from the store or serve the particular blob if blobKey is provided.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		if(req.getParameter("blobKey")!=null){
			this.serve(req, res);
		}else{
			//this.list(req, res);
			this.createUploadUrl(req, res);
		}
	}

	private void createUploadUrl(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException{
		String uploadUrl =  blobstoreService.createUploadUrl("http://senselocal.appspot.com/BlobDataServlet");
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");
        res.getWriter().println(uploadUrl);
        res.getWriter().flush();
		
	}
	private void list(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		log.info("listing all the existing blobs");
		BlobInfoFactory bif = new BlobInfoFactory();
		Iterator<BlobInfo> iterator = bif.queryBlobInfos();
		while (iterator.hasNext()) {
			BlobInfo bi = iterator.next();
			log.info(bi.getFilename());
			log.info(bi.getSize());
			log.info(bi.getContentType());
			log.info(bi.getCreation());
			log.info(bi.getBlobKey());
		}
		// forward to the JSP
		req.getRequestDispatcher("upload.jsp").forward(req, res);
		
	}
	
	private void serve(HttpServletRequest req, HttpServletResponse res) throws IOException {
		BlobKey blobKey = new BlobKey(req.getParameter("blobKey"));
        blobstoreService.serve(blobKey, res);
	}
	
	/**
	 * Create a re-size task and put in the queue, which will be processed by the IconImageResizeServlet
	 * @param imageBlobKey
	 */
	private void enqueueIconImageResizing(String saleId, Long imageId, String imageBlobKey) {
		log.info("enqueuing " + imageBlobKey);
		BlobInfoFactory bif = new BlobInfoFactory();
		BlobInfo bi = bif.loadBlobInfo(new BlobKey(imageBlobKey));
		String newName = "icon-" + bi.getFilename();
		Queue queue = QueueFactory.getQueue("icon-image-resize-processing");

		queue.add(url("/IconImageResizeServlet")
				.param("saleId", saleId)
				.param("imageId", imageId.toString())
				.param("blobKey", imageBlobKey).param("newName", newName)
				.param("width", "20").param("height", "25"));
	}

}
