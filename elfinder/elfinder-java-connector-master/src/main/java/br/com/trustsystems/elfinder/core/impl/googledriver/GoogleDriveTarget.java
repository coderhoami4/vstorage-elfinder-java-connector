package br.com.trustsystems.elfinder.core.impl.googledriver;

import org.json.JSONObject;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

import br.com.trustsystems.elfinder.core.Target;
import br.com.trustsystems.elfinder.core.Volume;

public class GoogleDriveTarget implements Target {
	
	private final Volume volume;
	private final File file;
	
	public GoogleDriveTarget(Volume volume) {
		this(volume, null);
    }
	
	public GoogleDriveTarget(Volume volume, File file) {
		this.volume = volume;
		this.file = file;
    }

	@Override
	public Volume getVolume() {
		return volume;
	}

	public String getName() {
		if (this.getFile() == null) {
			return null;
		}
		return this.getFile().getName();
	}

	public String getParentId() {
		if (this.getFile() == null || this.getFile().getParents()== null || this.getFile().getParents().size() == 0) {
			return null;
		}
		return this.getFile().getParents().get(0);
	}

	public String getId() {
		if (this.getFile() == null) {
			return "root";
		}
		return this.getFile().getId();
	}
	
	public String getMimeType() {
		if (this.getFile() == null) {
			return null;
		}
		String mimeType = this.getFile().getMimeType();
		if ("application/vnd.google-apps.folder".equals(mimeType)) {
			return "directory";
		}
		return mimeType;
	}

	public long getLastModified() {
		if (this.getFile() == null) {
			return 0;
		}
		DateTime modifiedTime = this.getFile().getModifiedTime();
		if (modifiedTime == null) {
			return 0;
		}
		return modifiedTime.getValue();
	}
	
	public boolean isFolder() {
		if (this.getFile() == null) {
			return false;
		}
		return "directory".equals(this.getMimeType());
	}

	public File getFile() {
		return file;
	}
	
	public long getSize() {
		if (this.getFile() == null) {
			return 0L;
		}
		Long s = file.getSize();
		if (s == null) {
			return 0;
		}
		return s.longValue();
	}
	
	@Override
	public String toString() {
		return JSONObject.valueToString(file);
	}

}
