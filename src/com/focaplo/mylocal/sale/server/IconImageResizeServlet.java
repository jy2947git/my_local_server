package com.focaplo.mylocal.sale.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.focaplo.mylocal.utils.NetUtility;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

import org.apache.log4j.Logger;

/**
 * this servlet is triggered by the GAE Task Queue to resize the image from BlobStore.
 */
public class IconImageResizeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected final Logger log = Logger.getLogger(this.getClass());   
	ImagesService imagesService = ImagesServiceFactory.getImagesService();
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IconImageResizeServlet() {
        super();
    }

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

	private void serve(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String saleId = request.getParameter("saleId");
		String imageId = request.getParameter("imageId");
		String blobKey = request.getParameter("blobKey");
		String newName = request.getParameter("newName");
		String width=request.getParameter("width");
		String height=request.getParameter("height");
		byte[] newImageData = this.resizeImage(blobKey, Integer.parseInt(width), Integer.parseInt(height));
		//the call-back url after new image uploaded to BlobStore
		String uploadUrl = blobstoreService.createUploadUrl("/IconImageResizeSuccessServlet");
		//post image data to blob-store along with the original key
		String contentType="application/octet-stream";
		if(newName.toLowerCase().endsWith("jpg") || newName.toLowerCase().endsWith("jpeg")){
			contentType="image/jpeg";
		}else if(newName.toLowerCase().endsWith("png") ){
			contentType="image/png";
		}
		//upload the new image to BlobStore
		NetUtility.uploadFileContent(uploadUrl, new String[]{"saleId","imageId", "blobKey"}, new String[]{saleId,imageId, blobKey}, contentType, newName, newImageData);
	}

	private byte[] resizeImage(String blobKey, int width, int height){
		Image oldImage = ImagesServiceFactory.makeImageFromBlob(new BlobKey(blobKey));
		Transform transform = ImagesServiceFactory.makeResize(width, height);
		Image newImage = imagesService.applyTransform(transform, oldImage);
		byte[] newImageData = newImage.getImageData();
		
		return newImageData;
	}
}
