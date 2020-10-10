package jmeterjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class jmeterfromjava {

	public static void main(String[] args) {

		//Set jmeter home for the jmeter utils to load
        File jmeterHome = new File("/Users/techops/Documents/Applications/apache-jmeter-5.3");
        String slash = System.getProperty("file.separator");
        
        if (jmeterHome.exists()) {
        	File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
        	if (jmeterProperties.exists()) {
        		StandardJMeterEngine jmeter = new StandardJMeterEngine();
        		
        		JMeterUtils.setJMeterHome(jmeterHome.getPath());
        		JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        		JMeterUtils.initLocale();
        		
        		HashTree testPlanTree = new HashTree();
        		
        		// HTTP sampler
        		HTTPSamplerProxy sampler = new HTTPSamplerProxy();
        		
        		sampler.setProtocol("https");
        		sampler.setDomain("demoblaze.com");
        		sampler.setPort(443);
        		sampler.setPath("/cart.html");
        		sampler.setMethod("GET");
        		sampler.setName("Open demoblaze from jmeter");
        		sampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        		sampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());      		
        		
        		// Loop Controller
        		LoopController loopController = new LoopController();
        		loopController.setLoops(5);
        		loopController.setFirst(true);
        		loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        		loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        		loopController.initialize();
        		
        		// Thread Group
        		ThreadGroup threadGroup = new ThreadGroup();
        		threadGroup.setName("sample thread group");
        		threadGroup.setNumThreads(1);
        		threadGroup.setRampUp(1);
        		threadGroup.setSamplerController(loopController);
        		threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        		threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        		
        		// Test Plan
        		TestPlan testPlan = new TestPlan("Create Jmeter Script from Java Code");
        		
        		testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        		testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        		testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
        		
        		// construct Test plan from previously initialized elements
        		testPlanTree.add(testPlan);
        		HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup).add(sampler);      		
        		
        		// Save generated test plan to Jmeter's.jmx file format
        		try {
					SaveService.saveTree(testPlanTree, new FileOutputStream("report/jmeter_api_sample.jmx"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        		//add Summarizer output to get progress
        		Summariser summary = null;
        		String summarizerName = JMeterUtils.getPropDefault("summarizer.name", "summary");
        		if (summarizerName.length() > 0 ) {
        			summary = new Summariser(summarizerName);
        		}
        		
        		// Store execution result into .jtl file, or csv
        		String reportFile = "report/report.jtl";
        		String csvFile = "report/report.csv";
        		
        		ResultCollector logger = new ResultCollector(summary);
        		logger.setFilename(reportFile);
        		
        		ResultCollector csvLogger = new ResultCollector(summary);
        		csvLogger.setFilename(csvFile);
        		
        		testPlanTree.add(testPlanTree.getArray()[0], logger);
        		testPlanTree.add(testPlanTree.getArray()[0], csvLogger);
        		      		
        		//Run Test Plan
        		jmeter.configure(testPlanTree);
        		jmeter.run();
        		
        		System.out.println("Test completed. See " + jmeterHome + slash + "report.jtl file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
                System.exit(0);
        		
        	}
        	
        	 System.err.println("jmeterHome property is not set or pointing to incorrect location");
             System.exit(1);
        }
	}

}

