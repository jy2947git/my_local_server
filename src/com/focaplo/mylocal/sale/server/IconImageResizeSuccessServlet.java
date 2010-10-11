package com.focaplo.mylocal.sale.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.focaplo.mylocal.sale.service.SaleService;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * call-back servlet after the icon image uploaded to BlobStore
 *
 */
public class IconImageResizeSuccessServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	protected final Logger log = Logger.getLogger(this.getClass());  
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serve(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serve(request, response);
	}

	private void serve(HttpServletRequest req, HttpServletResponse res) throws IOException {
		log.debug("Sale " + req.getParameter("saleId") + " image " + req.getParameter("imageId") + " original file key " + req.getParameter("blobKey") + " isUsedAsIcon:" + req.getParameter("isUsedAsSaleIcon"));
	    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	    BlobKey blobKey = blobs.get("myFile");
	    log.debug("the new file blob key:" + blobKey.getKeyString());
	    //save the item-image
		SaleService saleService = new SaleService();
		String jsonResOfImageInfo = saleService.saveSaleIconImage(new Long(req.getParameter("imageId")), blobKey.getKeyString(), "1".equalsIgnoreCase(req.getParameter("isUsedAsSaleIcon")));
		res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");
        res.getWriter().println(jsonResOfImageInfo);
        res.getWriter().flush();
	}
}
