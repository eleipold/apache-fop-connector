package org.mule.modules.apachefop.config;

import java.io.File;
import java.io.IOException;

import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

    private FopFactory fopFactory;
    private FopFactoryBuilder builder;

    /**
     * FOP Configuration
     */
    @Configurable
    @Path
    @Default("fop.xconf")
    private String fopConfiguration;
    
    @Configurable
    @Default("72.0")
    private float targetResolution;
    
    public String getFopConfiguration() {
        return fopConfiguration;
    }

    public void setFopConfiguration(String fopConfiguration) throws SAXException, IOException {
        this.fopConfiguration = fopConfiguration;
        File configFile = ResourceUtils.getFile(this.fopConfiguration);
        FopConfParser parser = new FopConfParser(configFile);
        this.builder = parser.getFopFactoryBuilder();
    }

    public FopFactory getFopFactory() {
        if (fopFactory == null) {
            builder.setTargetResolution(getTargetResolution());
            setFopFactory(builder.build());
        }
        return fopFactory;
    }

    public void setFopFactory(FopFactory fopFactory) {
        this.fopFactory = fopFactory;
    }

    public float getTargetResolution() {
        return targetResolution;
    }

    public void setTargetResolution(float targetResolution) {
        this.targetResolution = targetResolution;
    }
    
}