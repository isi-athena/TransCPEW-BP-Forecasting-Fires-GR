package gr.isi.iolaos;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.value.FileValue;

public class SendMapToESKE implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
//		Map<String, Object> variables=new HashMap<String, Object>();
//		
//		variables.put("MapSentFromDSAEE", execution.getVariable("map"));
//		
//		execution.getProcessEngineServices().getRuntimeService()		
//		.createMessageCorrelation("MapSentFromDSAEE")
//		.processDefinitionId("ESKE")
//		.setVariable("map", variables)
//		
//		.processInstanceId(execution.getProcessInstanceId())
//		.processInstanceVariableEquals(null, variables)
//		.correlateAll();
//		
//		ProcessInstance startedProcessInstance = runtimeService
//				  .createMessageCorrelation("messageName")
//				  .processInstanceBusinessKey("businessKey")
//				  .setVariable("name", "value")
//				  .correlateStartMessage();
		
		ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService=processEngine.getRuntimeService();
		
		FileValue retrievedTypedFileValue=runtimeService.getVariableTyped(execution.getId(),"map");
		
		execution.getProcessEngineServices().getRuntimeService()
		.createMessageCorrelation("MapSentFromDSAEE")
		.processInstanceId("process-map")
		.setVariable("map",retrievedTypedFileValue)
		.correlateStartMessage();

	}

}
