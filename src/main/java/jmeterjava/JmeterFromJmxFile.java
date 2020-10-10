package jmeterjava;

import java.awt.Desktop;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class JmeterFromJmxFile {

	public static void main(String[] args) throws Exception {
		//Set JMeter home for the JMeter utils to load
        File jmeterHome = new File("/Users/techops/Documents/Applications/apache-jmeter-5.3");
        String slash = System.getProperty("file.separator");
        
        // JMeter .jmx file
        File testPlan = new File("report/lifeCharger.jmx");
        
        if (jmeterHome.exists()) {
        	if (testPlan.exists()) {
        		File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
                if (jmeterProperties.exists()) {
                	
                	// JMeter Engine
                	StandardJMeterEngine jmeter = new StandardJMeterEngine();
                	
                	// Initialize Properties , locale , etc.
                	JMeterUtils.setJMeterHome(jmeterHome.getPath());
                	JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                	JMeterUtils.initLocale();
                	
                	// Set directory for HTML report
                	String repDir = "HTMLreport";
                	JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir", repDir);
                	
                	
                	// Initialize JMeter SaveService
					SaveService.loadProperties();
					// Load existing .jmx Test Plan
					HashTree testPlanTree = SaveService.loadTree(testPlan);
                	
                	// Add Summarizer to get test progress
                	Summariser summary = null;
                	String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
                	if (summariserName.length() > 0) {
                		summary = new Summariser(summariserName);
                	}
                	
                	// Store execution results .jtl or csv
                	File logFile = new File("report/report.jtl");
                	
                	// Delete log file if exist 
                	if (logFile.exists()) {
                		boolean delete = logFile.delete();
                		System.out.println("Jtl file cleaned up: " + delete);
                	}
                	
                	ResultCollector logger = new ResultCollector(summary);
                	//creating ReportGenerator for creating HTML report                
                	ReportGenerator reportgenerator = new ReportGenerator(logFile.getPath(), logger);
                	logger.setFilename(logFile.getPath());
                	testPlanTree.add(testPlanTree.getArray()[0], logger);
                	
                	// Run JMeter test
                	jmeter.configure(testPlanTree);
                	jmeter.run();
                	
                	// Report generator 
					FileUtils.deleteDirectory(new File(repDir));
					File reportOutput = new File("report-output");
					if (reportOutput.exists()) {
						FileUtils.deleteDirectory(new File("report-output"));
					}
					reportgenerator.generate();
				
                	
                	System.out.println("Test completed. See report/report.jtl file for results");
                	
                	// Open HTML report in default browser
                	File htmlFile = new File(repDir + "/index.html");
                	Desktop.getDesktop().browse(htmlFile.toURI());
                	System.exit(0);
                }
        	}
        	System.err.println("testPlan.location property is not set or pointing to incorrect location");
            System.exit(1);
        }
        System.err.println("jmeter.home property is not set or pointing to incorrect location");
        System.exit(1);

	}

}
