package com.ibm.clientengineering;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

/**
 * Populate the outgoing message with all system properties.
 */
public class CpuIntensiveComputeNode extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {

		MbOutputTerminal out = getOutputTerminal("out");

		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);

		try {

			MbElement rootElement = outMessage.getRootElement();

			MbElement outJsonRoot = rootElement.createElementAsLastChild(MbJSON.PARSER_NAME);

			MbElement outJsonData = outJsonRoot.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.DATA_ELEMENT_NAME,
					null);

			addJsonChildToElement(outJsonData, "Date", new Date().toString());

			Map<String, String> environment = System.getenv();

			addJsonChildToElement(outJsonData, "COMPNAME", environment.get("COMPNAME"));
			addJsonChildToElement(outJsonData, "HOSTNAME", environment.get("HOSTNAME"));
			addJsonChildToElement(outJsonData, "Nov22", "Nov22");

			addJsonChildToElement(outJsonData, "OPENSHIFT_BUILD_NAMESPACE",
					environment.get("OPENSHIFT_BUILD_NAMESPACE"));
			addJsonChildToElement(outJsonData, "SERVICE_NAME", environment.get("SERVICE_NAME"));
			addJsonChildToElement(outJsonData, "ACE_SERVER_NAME", environment.get("ACE_SERVER_NAME"));
			addJsonChildToElement(outJsonData, "GIT_COMMIT", environment.get("GIT_COMMIT"));
			addJsonChildToElement(outJsonData, "SOME_OUTPUT", environment.get("GIT_COMMIT"));
			
			addJsonChildToElement(outJsonData, "NOV23", "NOV23");
					
			int primeCount = 5000;
			
			try {
				primeCount = Integer.parseInt(environment.get("PRIME_COUNT"));
				System.out.println("Using prime count: " + primeCount);
			} catch (Exception e) {
				System.out.println("No prime count specified in environment variable PRIME_COUNT, using default value: " + primeCount);
			}
			
			
			
			List<Integer> primeNumbers = calculatePrimenumbers(primeCount);
			System.out.println(primeNumbers.size() + " primenumbers from " + primeCount);
			
			addJsonChildToElement(outJsonData, "PRIME_COUNT", primeNumbers.size() + "");

		} catch (MbException e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
		}
		out.propagate(outAssembly);
	}
	
	private List<Integer> calculatePrimenumbers(int from) {

		int i = 0;
		int num = 0;
		List<Integer> primeNumbers = new ArrayList<Integer>();
		
		for (i = 1; i <= from; i++) {
			
			int counter = 0;
			
			for (num = i; num >= 1; num--) {
				if (i % num == 0) {
					counter = counter + 1;
				}
			}
			if (counter == 2) {
				primeNumbers.add(i);
			}
		}
		return primeNumbers;
	}

	private void addJsonChildToElement(MbElement element, String name, String value) throws MbException {
		MbElement child = element.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
		child.setName(name);
		child.setValue(value);
	}
}
