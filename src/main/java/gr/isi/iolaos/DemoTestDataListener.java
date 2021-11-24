package gr.isi.iolaos;

import java.util.ArrayList;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class DemoTestDataListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		java.util.ArrayList<String> fireServices=new ArrayList<String>();
		fireServices.add("chania");
		fireServices.add("lasithi");
		fireServices.add("heraklion");
		
		execution.setVariable("fireServices", fireServices);

	}

}
