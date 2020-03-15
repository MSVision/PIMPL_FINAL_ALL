package de.fhwedel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CustomerService {
	
	private static Properties props = new Properties();
	private static EntityManager em;

	protected static EntityManager getEM() {
		if (CustomerService.em == null) {
			try {
				if (props.getProperty("regenerate", "1").equals("1")) {
					props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
					props.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
							PersistenceUnitProperties.DDL_DATABASE_GENERATION);
				}

				EntityManagerFactory emf = Persistence
						.createEntityManagerFactory(CustomerService.props.getProperty("persistence_unit", "pimpl"), props);
				if (emf != null) {
					CustomerService.em = emf.createEntityManager();
					CustomerService.em.setFlushMode(FlushModeType.COMMIT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CustomerService.em;
	}

	static {
		CustomerService.props = new Properties();
		try {
			CustomerService.props.load(new FileInputStream("..\\..\\pimpl.conf"));
		} catch (IOException e) {			
			System.err.println("Failed to load configuration file (" + new File("..\\..\\pimpl.conf").getAbsolutePath()
					+ "), continuing with defaults...");
		}

		if (CustomerService.props.getProperty("regenerate", "1").equals("1")) {
			EntityManager em = CustomerService.getEM();
			em.getTransaction().begin();
			em.persist( new Customer("C1", "Test", 100) );
			em.persist( new Customer("C2", "xyz", 500) );
			em.persist( new Customer("C3", "abc", 0) );
			em.persist( new Customer("C4", "Müller", null) );
			em.persist( new Customer("C5", "Meyer", null) );
			em.persist( new Customer("C6", "Hackmann", null) );

			// hier Platz für Beispieldaten/Stammdaten, die beim Neuerzeugen des
			// Schemas eingefügt werden sollen
			//
			// em.persist(...);
			em.getTransaction().commit();
		}
	}
	
	public static class GetCustomerByName implements WorkItemHandler {

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			String name = (String)wi.getParameter("name");
			Map<String, Object> results = new HashMap<>();
			try {
				System.out.println("searching for name " + name);			
				Customer c = (Customer)CustomerService.em
					.createNamedQuery("getCustomerByName")
					.setParameter("name", name)
					.getSingleResult()
					;
				results.put("customer", c);
			} catch(Exception e) {
				results.put("customer", null);		
			}				
			wim.completeWorkItem(wi.getId(), results);
		}				
		
	}
	
	public static class UpdateCustomer implements WorkItemHandler {

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			Customer c = (Customer)wi.getParameter("customer");
			Map<String, Object> results = new HashMap<>();			
			try {
				System.out.println("updating " + c);			
				CustomerService.em.getTransaction().begin();
				CustomerService.em.merge(c);
				CustomerService.em.getTransaction().commit();	
				results.put("result", c);
			} catch(Exception e) {
				CustomerService.em.getTransaction().rollback();
				results.put("result", null);;
			}
			wim.completeWorkItem(wi.getId(), results);
		}				
		
	}
		
}
