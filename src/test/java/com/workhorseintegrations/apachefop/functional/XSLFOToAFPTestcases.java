/**
 * 
 */
package com.workhorseintegrations.apachefop.functional;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

/**
 * @author mbrigilin
 *
 */
public class XSLFOToAFPTestcases extends FunctionalTestCase
{
	
	@Rule
	public Timeout globalTimeout = new Timeout(100000);
	
	@Override
	protected String getConfigResources()
	{
		return "apachefop-test-flows.xml";
	}
	
	@Test
	public void testContactsCreateData() throws Exception
	{
		MuleClient client = muleContext.getClient();
		String obj = IOUtils.toString( loadResource("fop.fo") );
		MuleMessage _msg = new DefaultMuleMessage(obj, muleContext);
		MuleMessage result = client.send("vm://test.xslfo.afp.in", _msg);
		
		assertNotNull( result );
		assertNotNull("Valid insighlty response :: ", result.getPayload() );
	}	
}
