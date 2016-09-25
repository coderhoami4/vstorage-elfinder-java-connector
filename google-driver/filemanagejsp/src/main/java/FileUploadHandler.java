import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.api.services.drive.Drive;

@MultipartConfig
@SuppressWarnings("serial")
public class FileUploadHandler extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				// Build a new authorized API client service.
				Drive service = DriveCommon.getDriveService();
				
				String folderId = DriveCommon.CheckExistingFolder(service, "MyTest");
		        
				if (folderId == null) {
					folderId = DriveCommon.CreateFolder(service, "MyTest");
				}
							    
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					
					if (!item.isFormField()) {
						InputStream stream = item.openStream();
						String fileId = DriveCommon.InsertFile(service, item.getName(), folderId, item.getContentType(), stream);
					    System.out.println("file Id: "+fileId);
					}
				}

				// File uploaded successfully
				request.setAttribute("message", "File Uploaded Successfully");

			} catch (Exception ex) {
				request.setAttribute("message", "File Upload Failed due to "
						+ ex);
			}
		} else {
			request.setAttribute("message",
                    "Sorry this Servlet only handles file upload request");
		}
		
		request.getRequestDispatcher("/result.jsp").forward(request, response);
	}

}