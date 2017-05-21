package apiconnector;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.EvaluationRequest;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunList;
import org.openml.apiconnector.xml.RunTag;
import org.openml.apiconnector.xml.RunUntag;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xml.UploadRunAttach;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestRunFunctionality {
	private static final int probe = 67;
	private static final int probeChallenge = 13976;
	private static final String predictions_path = "data/predictions_task53.arff";
	private static final int FLOW_ID = 10;

	private static final String url_test = "https://test.openml.org/";
	private static final String url_live = "https://www.openml.org/";
	private static final OpenmlConnector client_write_test = new OpenmlConnector(url_test, "8baa83ecddfe44b561fd3d92442e3319");
	private static final OpenmlConnector client_read_live = new OpenmlConnector(url_live, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f");
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final String tag = "junittest";
	
	@Test
	public void testApiRunDownload() throws Exception {
		
		Run run = client_read_live.runGet(probe);
		
		File tempXml = Conversion.stringToTempFile(xstream.toXML(run), "run", "xml");
		File tempXsd = client_read_live.getXSD("openml.run.upload");
		
		System.out.println(xstream.toXML(run));
		
		assertTrue(Conversion.validateXML(tempXml, tempXsd));
		
		// very easy checks, should all pass
		assertTrue(run.getRun_id() == probe);
		
	}
	
	@Test
	public void testApiRunList() throws Exception {
		List<Integer> uploaderId = new ArrayList<Integer>();
		uploaderId.add(29);
		
		RunList rl = client_read_live.runList(null, null, null, uploaderId);
		
		for (org.openml.apiconnector.xml.RunList.Run r : rl.getRuns()) {
			assertTrue(uploaderId.contains(r.getUploader()));
		}
		
	}
	
	@Test
	public void testApiRunUpload() throws Exception {
		String[] tags = {"first_tag", "another_tag"};
		
		Run r = new Run(probe, null, FLOW_ID, null, null, tags);
		String runXML = xstream.toXML(r);
		
		File runFile = Conversion.stringToTempFile(runXML, "runtest",  "xml");
		File predictions = new File(predictions_path); 
		
		Map<String,File> output_files = new HashMap<String, File>();
		
		output_files.put("predictions", predictions);
		
		UploadRun ur = client_write_test.runUpload(runFile, output_files);
		
		Run newrun = client_write_test.runGet(ur.getRun_id());
		
		Set<String> uploadedTags = new HashSet<String>(Arrays.asList(newrun.getTag()));
		Set<String> providedTags = new HashSet<String>(Arrays.asList(tags));
		
		assertTrue(uploadedTags.equals(providedTags));
		
		RunTag rt = client_write_test.runTag(ur.getRun_id(), tag);
		assertTrue(Arrays.asList(rt.getTags()).contains(tag));
		RunUntag ru = client_write_test.runUntag(ur.getRun_id(), tag);
		assertTrue(Arrays.asList(ru.getTags()).contains(tag) == false);
		
		client_write_test.runDelete(ur.getRun_id());
	}
	
	// skip for now, add to test server later
	public void testApiUploadRunAttach() throws Exception {
		Run r = new Run(probeChallenge, null, FLOW_ID, null, null, null);
		String runXML = xstream.toXML(r);
		File runFile = Conversion.stringToTempFile(runXML, "runtest",  "xml");
		File predictions = new File(predictions_path); 
		UploadRun ur = client_write_test.runUpload(runFile, null);
		for (int i = 0; i < 5; i+=1) {
			UploadRunAttach ura = client_write_test.runUploadAttach(ur.getRun_id(), i , runFile, predictions);
			
			assertTrue(ura.getPredictionFiles().length == i+1);
		}
	}
	
	@Test
	public void testApiEvaluationRequest() throws Exception {
		EvaluationRequest er = client_write_test.evaluationRequest(2, "random", 1);
		assertTrue(er.getRuns().length == 1);
	}
}
