/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fhwedel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorManager;
import org.kie.test.util.db.PersistenceUtil;

import de.fhwedel.DispositionService.IncreaseStock;
import de.fhwedel.DispositionService.IdentifyContract;
import de.fhwedel.DispositionService.AssignInventoryToOrderItem;
import de.fhwedel.DispositionService.AssignOpenShippingItemsToShippingOrder;
import de.fhwedel.DispositionService.CreateShippingOrder;

/**
 * This is a sample file to launch a process.
 */
public class ProcessMain {

    private static final boolean usePersistence = false;
    
    public static final void main(String[] args) throws Exception {
        // load up the knowledge base
        KieBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = newStatefulKnowledgeSession(kbase);
        
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("contract", null); 
        variables.put("contractPosition", null); 
		variables.put("openContractPositions", new ArrayList<ContractPosition>());
		variables.put("finalContractPositions", new ArrayList<ContractPosition>()); 
		variables.put("allContractPositions", new ArrayList<ContractPosition>()); 
        variables.put("shippingOrder", null);
        variables.put("finalAllContractPositions", new ArrayList<ContractPosition>()); 
		
		ksession.getWorkItemManager().registerWorkItemHandler("IncreaseStock", new IncreaseStock());
		ksession.getWorkItemManager().registerWorkItemHandler("IdentifyContract", new IdentifyContract());
		ksession.getWorkItemManager().registerWorkItemHandler("AssignInventoryToOrderItem", new AssignInventoryToOrderItem());
		ksession.getWorkItemManager().registerWorkItemHandler("AssignOpenShippingItemsToShippingOrder", new AssignOpenShippingItemsToShippingOrder());
		ksession.getWorkItemManager().registerWorkItemHandler("CreateShippingOrder", new CreateShippingOrder());
		
		// start my new process instances
		System.out.println("Starting my processes...");
		ksession.startProcess("de.fhwedel.Disposition", variables);
		System.out.println("Completed my processes...");
        
        // OLD !!!!!!
        
//        List<String> names = new ArrayList<>();
//        names.add("Test");
//        names.add("Hackmann");        
//        variables.put("names", names);
//        variables.put("customers", new ArrayList<Customer>());
//        ksession.getWorkItemManager().registerWorkItemHandler("StringToLength", new StringToLength());
//		ksession.getWorkItemManager().registerWorkItemHandler("GetCustomerByName", new GetCustomerByName());
//		ksession.getWorkItemManager().registerWorkItemHandler("UpdateCustomer", new UpdateCustomer());

//        // start a new process instance
//		System.out.println("Starting process...");
//		ksession.startProcess("de.fhwedel.Example", variables);
//		System.out.println("Completed process...");
    }

    private static KieBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        kbuilder.add(ResourceFactory.newClassPathResource("de.fhwedel/Example.bpmn2"), ResourceType.BPMN2);
        
        // MyStuff
        kbuilder.add(ResourceFactory.newClassPathResource("de.fhwedel/Disposition.bpmn2"),
        		ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("de.fhwedel/Delivery.bpmn2"),
        		ResourceType.BPMN2);
        
        return kbuilder.newKieBase();
    }
    
    public static StatefulKnowledgeSession newStatefulKnowledgeSession(KieBase kbase) {
        RuntimeEnvironmentBuilder builder = null;
        if ( usePersistence ) {
            Properties properties = new Properties();
            properties.put("driverClassName", "org.h2.Driver");
            properties.put("className", "org.h2.jdbcx.JdbcDataSource");
            properties.put("user", "sa");
            properties.put("password", "");
            properties.put("url", "jdbc:h2:tcp://localhost/~/jbpm-db");
            properties.put("datasourceName", "jdbc/jbpm-ds");
            PersistenceUtil.setupPoolingDataSource(properties);
            Map<String, String> map = new HashMap<String, String>();
            map.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");                            
            builder = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf);
        } else {
            builder = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultInMemoryBuilder();
            DeploymentDescriptor descriptor = 
		new DeploymentDescriptorManager().getDefaultDescriptor().getBuilder().auditMode(AuditMode.NONE).get();	
            builder.addEnvironmentEntry("KieDeploymentDescriptor", descriptor);                
        }
        builder.knowledgeBase(kbase);
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(builder.get());
        return (StatefulKnowledgeSession) manager.getRuntimeEngine(EmptyContext.get()).getKieSession();
    }
}
