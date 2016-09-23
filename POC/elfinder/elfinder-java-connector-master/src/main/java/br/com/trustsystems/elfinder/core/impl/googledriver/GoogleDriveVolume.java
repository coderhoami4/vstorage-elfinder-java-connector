package br.com.trustsystems.elfinder.core.impl.googledriver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import br.com.trustsystems.elfinder.core.Target;
import br.com.trustsystems.elfinder.core.Volume;
import br.com.trustsystems.elfinder.core.VolumeBuilder;

public class GoogleDriveVolume implements Volume {
	
	private static final Logger logger = LoggerFactory.getLogger(GoogleDriveVolume.class);
	
	private final String alias;
	String credentialJsonPath = null;
	GoogleDriverService googleDriverService = null;
	private File rootDir = null;
	Target rootTarget = null;
	
	private GoogleDriveVolume(Builder builder) {
		logger.debug("Init instance of GoogleDriveVolume");
		this.alias = builder.alias;
		credentialJsonPath = builder.getCredentialJsonPath();
		googleDriverService = new GoogleDriverService(credentialJsonPath);
		googleDriverService.connect();
		createRootDir();
		printCurrentStackTrace();
    }
	
	private void createRootDir() {
		rootTarget = fromPath("root");
		GoogleDriveTarget gTarget = (GoogleDriveTarget)rootTarget;
		rootTarget = gTarget;
		rootDir = gTarget.getFile();
    }

	private Drive getDrive() {
		return googleDriverService.getDrive();
	}

	@Override
	public void createFile(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
	}

	@Override
	public void createFolder(Target target) throws IOException {
		logger.debug(target.toString());
	}

	@Override
	public void deleteFile(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
	}

	@Override
	public void deleteFolder(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
	}

	@Override
	public boolean exists(Target target) {
		logger.debug(target.toString());
		printCurrentStackTrace();
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		try {
			if (gTarget.getId() == null) {
				return false;
			}
			return getDrive().files().get(gTarget.getId()) != null;
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public Target fromPath(String fileId) {
		logger.debug(fileId);
		printCurrentStackTrace();
		try {
			if (fileId == null) {
				if (rootTarget != null) {
					return rootTarget;
				}
				fileId = "root";
			}
			File f = getDrive().files().get(fileId).setFields("id, name, parents, mimeType, size").execute();
			GoogleDriveTarget googleDriveTarget = new GoogleDriveTarget(this, f);
			if ("root".equals(fileId)) {
				rootTarget = googleDriveTarget;
			}
			return googleDriveTarget;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static File fromTarget(Target target) {
		logger.debug(target.toString());
        return ((GoogleDriveTarget) target).getFile();
    }

    public static Target fromPath(GoogleDriveVolume volume, File path) {
        return new GoogleDriveTarget(volume, path);
    }

	@Override
	public long getLastModified(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		return gTarget.getLastModified();
	}

	@Override
	public String getMimeType(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		return gTarget.getMimeType();
	}

	@Override
	public String getAlias() {
		logger.debug(alias);
		return alias;
	}

	@Override
	public String getName(Target target) {
		printCurrentStackTrace();
		logger.debug(target.toString());
		return ((GoogleDriveTarget)target).getName();
	}

	@Override
	public Target getParent(Target target) {
		logger.debug(target.toString());
		printCurrentStackTrace();
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		String parentId = gTarget.getParentId();
		return fromPath(parentId);
	}

	@Override
	public String getPath(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		return gTarget.getId();
	}

	@Override
	public Target getRoot() {
		logger.debug(rootTarget.toString());
		printCurrentStackTrace();
		return rootTarget;
	}

	@Override
	public long getSize(Target target) throws IOException {
		logger.debug(target.toString());
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		return gTarget.getSize();
	}

	@Override
	public boolean hasChildFolder(Target target) throws IOException {
		logger.debug(target.toString());
		printCurrentStackTrace();
		return false;
	}

	@Override
	public boolean isFolder(Target target) {
		logger.debug(target.toString());
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		boolean isFolder = gTarget.isFolder();
		logger.debug("isFolder:" + isFolder);
		printCurrentStackTrace();
		return isFolder;
	}

	@Override
	public boolean isRoot(Target target) throws IOException {
		logger.debug(target.toString());
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		String targetId = gTarget.getId();
		boolean isRoot = "root".equals(targetId) || (((GoogleDriveTarget)rootTarget).getId().equals(targetId));
		logger.debug("isRoot:" + isRoot);
		printCurrentStackTrace();
		return isRoot;
	}

	@Override
	public Target[] listChildren(Target target) throws IOException {
		printCurrentStackTrace();
        FileList list = null;
		try {
			String folderId = ((GoogleDriveTarget)target).getId();
			logger.debug("folderId=" + folderId);
			list = getDrive().files().list()
					.setQ("'" + folderId + "' in parents")
			     .setPageSize(10)
			     .setFields("nextPageToken, files(id, name, parents, mimeType, size)")
			     .execute();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
        List<File> files = list.getFiles();
        logger.debug("size==" + files.size());
        if (files == null || files.size() == 0) {
        	return null;
        } else {
        	Target[] result = new Target[files.size()];
        	for (int i = 0; i < files.size(); i++) {
        		File file = files.get(i);
        		GoogleDriveTarget gTarget = new GoogleDriveTarget(this, file);
        		logger.debug(target.toString());
        		result[i] = gTarget;
			}
        	return result;
        }
	}

	@Override
	public InputStream openInputStream(Target target) throws IOException {
		GoogleDriveTarget gTarget = (GoogleDriveTarget)target;
		String fileId = gTarget.getId();
		InputStream in = getDrive().files().get(fileId).executeMediaAsInputStream();
		return in;
	}

	@Override
	public OutputStream openOutputStream(Target target) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(Target origin, Target destination) throws IOException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public List<Target> search(String target) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	/**
     * Gets a Builder for creating a new NIO2FileSystemVolume instance.
     *
     * @return a new Builder for NIO2FileSystemVolume.
     */
    public static Builder builder(String alias, String credentialJsonPath) {
        return new GoogleDriveVolume.Builder(alias, credentialJsonPath);
    }
    
	public File getRootDir() {
		return rootDir;
	}

	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}

	/**
     * Builder GoogleDriverVolume Inner Class
     */
    public static class Builder implements VolumeBuilder<GoogleDriveVolume> {
        // required fields
        private final String alias;
        private final String credentialJsonPath;

        public Builder(String alias, String credentialJsonPath) {
            this.alias = alias;
            this.credentialJsonPath = credentialJsonPath;
        }

        @Override
        public GoogleDriveVolume build() {
            return new GoogleDriveVolume(this);
        }

		public String getCredentialJsonPath() {
			return credentialJsonPath;
		}

		public String getAlias() {
			return alias;
		}
    }
    static ThreadLocal latestStackTrace = new ThreadLocal(){
    	@Override
    	protected Object initialValue() {
    		return null;
    	}
    };
    public static void printCurrentStackTrace() {
    	if (false) {
    		StringBuilder sb = new StringBuilder();
    		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
    			if (ste != latestStackTrace.get()) {
    				sb.append("\n" + ste.toString());
    				latestStackTrace.set(ste);
    			}
    		}
    		logger.debug(sb.toString());
    	}
    }
}
