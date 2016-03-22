/**
 * 
 */
package com.workhorseintegrations.apachefop.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.PageSequenceResults;
import org.xml.sax.SAXException;

/**
 * @author mbrigilin
 *
 */
public class ApacheFOPTransformerUtil
{
	// configure fopFactory as desired
	public final static FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
	
	/**
	 * 
	 * @param payload
	 * @param type
	 * @return byte[]
	 * @throws FileNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static byte[] transform( Object payload, String type ) throws FileNotFoundException, SAXException, IOException
	{		
		// Setup output stream
		OutputStream out = new ByteArrayOutputStream();
		
		String str = ( String )payload;
		
		// Setup input stream
		Source xmlSrc = new StreamSource( new ByteArrayInputStream( str.getBytes() ) );
		
		String mimeType = ( type == null || type == "" ) ? MimeConstants.MIME_PDF : type;
		byte[] resultArray = null;
		
		try
		{
			 // Construct fop with desired output format
			Fop fop = fopFactory.newFop( mimeType, out);
			
			// Setup JAXP using identity transformer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
			
			 // Resulting SAX events (the generated FO) must be piped through to FOP
			Result res = new SAXResult( fop.getDefaultHandler() );
			
			try
			{
				// Start XSLT transformation and FOP processing
				transformer.transform( xmlSrc, res);
			}
			catch (TransformerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultArray = ( ( ByteArrayOutputStream ) out).toByteArray();
			
			// Result processing
			FormattingResults foResults = fop.getResults();
			
			if( foResults != null )
			{
				@SuppressWarnings("rawtypes")
				List pageSequences = foResults.getPageSequences();
				
				if( pageSequences != null & !pageSequences.isEmpty() )
				{
					for ( @SuppressWarnings("rawtypes")Iterator it = pageSequences.iterator(); it.hasNext(); )
					{
						PageSequenceResults pageSequenceResults = ( PageSequenceResults )it.next();
						System.out.println("PageSequence " + (String.valueOf(pageSequenceResults.getID()).length() > 0 
								? pageSequenceResults.getID() : "<no id>") + " generated " + pageSequenceResults.getPageCount() + " pages.");
					}
					System.out.println("Generated " + foResults.getPageCount() + " pages in total.");	
				}
				else
					System.out.println("pageSequences is null or empty.");				
			}
		} 
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			// Clean-up
			out.close();
		}
		
		return resultArray;		
	}

}
