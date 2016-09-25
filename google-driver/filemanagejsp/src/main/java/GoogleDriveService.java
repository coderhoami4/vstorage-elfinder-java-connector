import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveService {
	/** Global instance of the scopes required by this quickstart.
    *
    * If modifying these scopes, delete your previously saved credentials
    * at ~/.credentials/drive-java-quickstart.json
    */
	private static final List<String> SCOPES =
	        Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, 
	        		DriveScopes.DRIVE_FILE);
	
	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY =
	        JacksonFactory.getDefaultInstance();
	
	/** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
	
	private String applicationName;
	private FileDataStoreFactory dataStoreFactory;
	private Drive driver;
	private java.io.File dataStoreDir;
	
	static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
	
	public GoogleDriveService(String dataStorePath) {
		//this("Manage File", System.getProperty("user.home"), ".credentials/" + dataStorePath);
		this("Manage File", System.getProperty("user.home"), ".credentials/" + dataStorePath);
		
	}
	
	public GoogleDriveService(String applicationName, String parentDataStorePath, String dataStorePath) {
		this.applicationName = applicationName;
		this.dataStoreDir = new java.io.File(parentDataStorePath, dataStorePath);
		try {
			this.dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
		} catch (IOException e) {
			 e.printStackTrace();
	            System.exit(1);
		}
	}
	
	public void connect() {
		try {
			this.setDriver(new Drive.Builder(
					HTTP_TRANSPORT, JSON_FACTORY, authorize())
	                .setApplicationName(applicationName)
	                .build());
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
	}
	
	private Credential authorize() throws Exception {
        // Load client secrets.
        InputStream in =
            DriveQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + dataStoreDir.getAbsolutePath());
        return credential;
    }

	public Drive getDriver() {
		return driver;
	}

	public void setDriver(Drive driver) {
		this.driver = driver;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("user.dir"));
		testGoogleDriveService("hoamicoder1.json");
		testGoogleDriveService("vucandn.json");
    }
	
	private static void testGoogleDriveService(String credentialJsonPath) {
		GoogleDriveService service = new GoogleDriveService(credentialJsonPath);
		service.connect();
        // Build a new authorized API client service.
        Drive driver = service.getDriver();
        
        // Print the names and IDs for up to 10 files.
        FileList result = null;
		try {
			result = driver.files().list()
			     .setPageSize(10)
			     .setFields("nextPageToken, files(id, name)")
			     .execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s) \n", file.getName(), file.getId()) ;
            }
        }
	}
}
