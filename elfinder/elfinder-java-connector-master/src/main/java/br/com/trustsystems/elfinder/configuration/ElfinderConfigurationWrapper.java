/*
 * #%L
 * %%
 * Copyright (C) 2015 Trustsystems Desenvolvimento de Sistemas, LTDA.
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Trustsystems Desenvolvimento de Sistemas, LTDA. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package br.com.trustsystems.elfinder.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.trustsystems.elfinder.configuration.jaxb.ElfinderConfiguration;

/**
 * Elfinder Configuration Wrapper class.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class ElfinderConfigurationWrapper {

    private static final Logger log = LoggerFactory.getLogger(ElfinderConfigurationWrapper.class);
    private ElfinderConfiguration elfinderConfiguration;

    public ElfinderConfigurationWrapper() {
        this("elfinder-configuration.xml");
    }
    
    public ElfinderConfigurationWrapper(String path) {
        loadElfinderConfiguration(path);
    }

	private InputStream getResourceConfigurationStream(String path) throws IOException {
		boolean isUseBuiltIn = false;
		if (path == null) {
			log.warn("path == null");
			isUseBuiltIn = true;
		}
		if (path != null && Files.exists((Paths.get(ElfinderConfigurationUtils.toURI(path)))) == false) {
			log.warn("path not found:" + path);
			isUseBuiltIn = true;
		}
		
		if (isUseBuiltIn == false) {
			return Files.newInputStream(Paths.get(ElfinderConfigurationUtils.toURI(path)));
		}
		return this.getClass().getClassLoader().getResourceAsStream("elfinder-configuration.xml");
                
	}
	private void loadElfinderConfiguration(String path) {
		
		try (InputStream resourceAsStream = getResourceConfigurationStream(path)) {
            this.elfinderConfiguration = ElfinderConfigurationJaxb.unmarshal(resourceAsStream);
            if (log.isDebugEnabled()) {
                log.debug("Thumbnail Width: " + getThumbnailWidth());
                for (ElfinderConfiguration.Volume volume : getVolumes()) {
                    log.debug("Source  : " + volume.getSource());
                    log.debug("Alias   : " + volume.getAlias());
                    log.debug("Path    :\t " + volume.getPath());
                    log.debug("Default :\t " + volume.isDefault());
                    log.debug("Locale  :\t " + volume.getLocale());
                    log.debug("Locked  :\t\t " + volume.getConstraint().isLocked());
                    log.debug("Readable:\t\t " + volume.getConstraint().isReadable());
                    log.debug("Writable:\t\t " + volume.getConstraint().isWritable());
                }
            }

        } catch (IOException e) {
            log.warn("Could not close elfinder configuration input stream and releases any system resources associated with the stream", e);
//          throw new ElfinderConfigurationException("Could not close elfinder configuration input stream and releases any system resources associated with the stream", e);
        }
	}

    public Integer getThumbnailWidth() {
        int thumbnailWidth = 80; //default value
        if (elfinderConfiguration.getThumbnail() != null) {
            thumbnailWidth = elfinderConfiguration.getThumbnail().getWidth().intValue();
        }
        return thumbnailWidth;
    }

    public List<ElfinderConfiguration.Volume> getVolumes() {
        return Collections.unmodifiableList(elfinderConfiguration.getVolume());
    }
}
