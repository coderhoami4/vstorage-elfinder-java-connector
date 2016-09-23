package br.com.trustsystems.elfinder.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.com.trustsystems.elfinder.core.Target;
import br.com.trustsystems.elfinder.core.Volume;
import br.com.trustsystems.elfinder.core.impl.googledriver.GoogleDriveTarget;
import br.com.trustsystems.elfinder.core.impl.googledriver.GoogleDriveVolume;
public class GoogleDriveVolumeTest {
	
	private static Volume volume;

    @BeforeClass
    public static void setUp() {
    	volume = GoogleDriveVolume.builder("Google Driver", "hoamicoder1.json").build();
    }

    @AfterClass(enabled = false)
    public static void tearDown() {
    }

    @Test(enabled = true)
    public void fromPathTest() throws IOException {
    	Target target = volume.fromPath("root");
    	printTarget(target);
    }
    
    @Test(enabled = false)
    public void getRootTest() throws IOException {
    	Target rootTarget = volume.getRoot();
    	printTarget(rootTarget);
    }
    
    @Test(enabled = false)
    public void listChildrenTest() throws IOException {
        Target[] listChildren = volume.listChildren(volume.getRoot());
		int length = listChildren.length;
        System.out.println("Volume#listChildren(Target target): " + length);
        for (Target childTarget : listChildren) {
        	GoogleDriveTarget child = ((GoogleDriveTarget)childTarget);
        	printTarget(child);
		}
    }
    
    @Test(enabled = false)
    public void getParentTest() throws IOException {
    	Target target = volume.getRoot();
    	Target[] listChildren = volume.listChildren(target);
        for (Target childTarget : listChildren) {
        	printTarget(childTarget);
        	GoogleDriveTarget parent = (GoogleDriveTarget)volume.getParent(childTarget);
        	printTarget(parent);
		}
    }
    
    @Test(enabled = false)
    public void createFolderTest() throws IOException {
    	Target target = volume.getRoot();
    	volume.createFolder(target);
    }
    
    @Test(enabled = true)
    public void getSizeTest() throws IOException {
    	Target target = volume.fromPath("0B3AqhzC4YrigSmNDM0Z6bG8yZDg");// 0B3AqhzC4Yrigc3RhcnRlcl9maWxl
    	long size = volume.getSize(target);
    	System.out.println(size);
    }
    
    @Test(enabled = true)
    public void openInputStreamTest() throws IOException {
    	Target target = volume.fromPath("0B3AqhzC4YrigSmNDM0Z6bG8yZDg");
    	OutputStream out = System.out;
		try (InputStream is = volume.openInputStream(target)) {
            IOUtils.copy(is, out);
            out.flush();
            out.close();
        }
    	
    }
    
    private void printTarget(Target target) {
    	GoogleDriveTarget gtarget = (GoogleDriveTarget) target;
    	System.out.printf("%s (%s) %s \n", gtarget.getName(), gtarget.getId(), gtarget.getMimeType()) ;
    }

}
