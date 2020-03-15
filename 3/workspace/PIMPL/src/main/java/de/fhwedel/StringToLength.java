package de.fhwedel;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class StringToLength implements WorkItemHandler {

	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		arg1.abortWorkItem(arg0.getId());
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		String instr = (String)arg0.getParameter("instr");
		Map<String, Object> results = new HashMap<>();
		results.put("length", instr.length());
		arg1.completeWorkItem(arg0.getId(), results);
	}

}
