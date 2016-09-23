import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


public class DriveCommon {
	/** Application name. */
    private static final String APPLICATION_NAME = "ManageFile";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/drive-java-quickstart.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, 
        		DriveScopes.DRIVE_FILE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws Exception 
     */
    public static Credential authorize() throws Exception {
        // Load client secrets.
        InputStream in =
            DriveCommon.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws Exception 
     */
    public static Drive getDriveService() throws Exception {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static String CreateFolder(Drive service, String folderName) throws Exception{
    	File fileMetadata = new File();
    	fileMetadata.setName(folderName);
    	fileMetadata.setMimeType("application/vnd.google-apps.folder");

    	File file = service.files().create(fileMetadata)
    	        .setFields("id")
    	        .execute();
    	    	
    	return file.getId();
    }
    
    public static String InsertFile(Drive service, String name, String folderId, String mimeType, java.io.InputStream fileUpload) throws IOException {
    	
    	File fileMetaData = new File();
    	fileMetaData.setName(name);
    	fileMetaData.setParents(Collections.singletonList(folderId));
    	//FileContent mediaContent = new FileContent(mimeType, fileUpload);
    	
    	File file = service.files().create(fileMetaData, new InputStreamContent(mimeType, fileUpload))
    	        .setFields("id, parents")
    	        .execute();
    	
    	return file.getId();
    }
    
    public static String CheckExistingFolder(Drive service, String folderName) throws IOException {
    	
    	FileList result = service.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder'")
                .setQ("name='"+folderName+"'")
                .setSpaces("drive")
				.setFields("nextPageToken, files(id, name)").execute();       
    	
		if (result != null) {
			 List<File> files = result.getFiles();
			 
			if (files != null && files.size() > 0) {
				return result.getFiles().get(0).getId();
			}
			 
			 return null;
		}
    	
    	return null;
    }
    
    public static java.io.ByteArrayOutputStream DownloadFile(Drive service, String fileId) throws IOException{
    	java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
    	service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
    	
    	return outputStream;
    }
}
