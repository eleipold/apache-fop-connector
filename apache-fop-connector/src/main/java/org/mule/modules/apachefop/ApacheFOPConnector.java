package org.mule.modules.apachefop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.param.Payload;
import org.mule.module.xml.transformer.XsltTransformer;
import org.mule.modules.apachefop.config.ConnectorConfig;

@Connector(name="apache-fop", friendlyName="ApacheFOP")
public class ApacheFOPConnector {

    @Config
    ConnectorConfig config;
    
    /**
     * Custom processor
     *
     * @param friend Name to be used to generate a greeting message.
     * @return A greeting message
     * @throws TransformerException 
     */
    @Processor
    public byte[] generate(String mimeType, @Path @FriendlyName("FO XSL") String foStylesheet, @Payload Object payload) throws TransformerException {
        XsltTransformer xslt = new XsltTransformer();
        xslt.setXslFile(foStylesheet);
        
        InputStream is = null;
        //source = "<test></test>";
        if (payload instanceof String) {
          is = new ByteArrayInputStream(((String)payload).getBytes());
        } else {
          is = new ByteArrayInputStream((byte [])payload);
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] resultBytes = null;
        try {
            Fop fop = config.getFopFactory().newFop(mimeType, out);
            Source source = new StreamSource(is);
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = (foStylesheet == null || foStylesheet.length() == 0)
                    ? factory.newTransformer() : factory.newTransformer(new StreamSource(foStylesheet));
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(source, result);
            resultBytes = out.toByteArray();
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new TransformerException(e.getMessage());
        }
        return resultBytes;
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}