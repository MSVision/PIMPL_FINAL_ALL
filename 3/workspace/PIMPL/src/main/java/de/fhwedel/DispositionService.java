package de.fhwedel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import de.fhwedel.Contract.DeliveryMethod;

public class DispositionService {
	
	private static Properties props = new Properties();
	private static EntityManager em;
	
	private static Random RANDOM =  new Random();

	protected static EntityManager getEM() {
		if (DispositionService.em == null) {
			try {
				if (props.getProperty("regenerate", "1").equals("1")) {
					props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
					props.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
							PersistenceUnitProperties.DDL_DATABASE_GENERATION);
				}

				EntityManagerFactory emf = Persistence
						.createEntityManagerFactory(DispositionService.props.getProperty("persistence_unit", "pimpl"), props);
				if (emf != null) {
					DispositionService.em = emf.createEntityManager();
					DispositionService.em.setFlushMode(FlushModeType.COMMIT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DispositionService.em;
	}

	static {
		DispositionService.props = new Properties();
		try {
			DispositionService.props.load(new FileInputStream("..\\..\\pimpl.conf"));
		} catch (IOException e) {
			System.err.println("Failed to load configuration file (" + new File("..\\..\\pimpl.conf").getAbsolutePath()
					+ "), continuing with defaults...");
		}

		if (DispositionService.props.getProperty("regenerate", "1").equals("1")) {
			EntityManager em = DispositionService.getEM();
			em.getTransaction().begin();

			Item item1 = new Item("PC-1233", "Banane", 1, 55);
			em.persist(item1);
			Item item2 = new Item("PC-1113", "Skyr", 2, 14);
			em.persist(item2);
			Item item3 = new Item("PC-1423", "Erdnussbutter", 5, 12);
			em.persist(item3);
			
			ContractPosition contractPosition1 = ContractPosition.initContractPosition(item1, 10, 0);
			em.persist(contractPosition1);
			ContractPosition contractPosition2 = ContractPosition.initContractPosition(item2, 12, 0);
			em.persist(contractPosition2);
			ContractPosition contractPosition3 = ContractPosition.initContractPosition(item3, 14, 0);
			em.persist(contractPosition3);

			List<ContractPosition> contractPositions = new ArrayList<>();
			contractPositions.add(contractPosition1);
			contractPositions.add(contractPosition2);
			contractPositions.add(contractPosition3);

			DispositionCustomer dispositionCustomer = new DispositionCustomer("Schumann", 15, "Liethberg", "24576", "Bad Bramstedt");
			em.persist(dispositionCustomer);
			
			Contract contract = Contract.initContract(1000, DeliveryMethod.COMPLETE, dispositionCustomer, contractPositions);
			em.persist(contract);
			
			em.getTransaction().commit();
		}
	}
	
	// FUNCTIONS HERE!
	
	public static class IdentifyContract implements WorkItemHandler {

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			EntityManager em = DispositionService.getEM();
			Map<String, Object> results = new HashMap<>();
			// Vereinfacht für das Beispiel... Erklärung in der Mail.
			Contract contract = (Contract) em.createNamedQuery("getAllContracts").getSingleResult();
			results.put("contract", contract);
			wim.completeWorkItem(wi.getId(), results);
		}

	}

	public static class IncreaseStock implements WorkItemHandler {

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			EntityManager em = DispositionService.getEM();
			Map<String, Object> results = new HashMap<>();
			@SuppressWarnings("unchecked")
			List<Item> allItems = em.createNamedQuery("getAllItems").getResultList();
//			List<Item> allItems = em.createQuery("SELECT i FROM Item i").getResultList();
			
			em.getTransaction().begin();
			for (Item item : allItems) {
				item.setStock(item.getStock() + RANDOM.nextInt(10));
				em.merge(item);
			}
			em.getTransaction().commit();
			
			results.put("result", allItems);
			wim.completeWorkItem(wi.getId(), results);
		}

	}

	public static class AssignInventoryToOrderItem implements WorkItemHandler {

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			ContractPosition contractPosition = (ContractPosition) wi.getParameter("contractPosition");
			EntityManager em = DispositionService.getEM();
			Map<String, Object> results = new HashMap<>();
			
			@SuppressWarnings("unchecked")
			List<Item> allItems = em.createNamedQuery("getAllItems").getResultList();
			
			// muss immer ein Element sein!
			Item item = allItems.stream()
					.filter(i -> i.getId().equals(contractPosition.getItem().getId()))
					.collect(Collectors.toList())
					.get(0);

			Integer minNumber = Math.min(item.getStock(), contractPosition.getTargetQuantity() - contractPosition.getActualQuantity());
			
			// 1.
			contractPosition.setActualQuantity(contractPosition.getActualQuantity() 
					+ minNumber);
			
			// 4. 
			if (item.getStock() > 0 && minNumber > 0) {
				// 2.
//				ShippingPosition shippingPosition = new ShippingPosition(minNumber, contractPosition);
				ShippingPosition shippingPosition = new ShippingPosition(minNumber);
				
				em.getTransaction().begin();
				em.persist(shippingPosition);
				em.getTransaction().commit();
				
				List<ShippingPosition> shippingPositions = contractPosition.getShippingPositions();
				shippingPositions.add(shippingPosition);
				contractPosition.setShippingPositions(shippingPositions);
				
				em.getTransaction().begin();
				em.merge(contractPosition);
				em.getTransaction().commit();
			} else {
				// normal fehlerbehandlung aber wegen Aufgabenstellung mal keine... :) 
			}
			
			// 3.
			item.setStock(item.getStock() - minNumber);
			em.getTransaction().begin();
			em.merge(item);
			em.getTransaction().commit();

			results.put("result", contractPosition);
			wim.completeWorkItem(wi.getId(), results);
		}

	}
	
	public static class CreateShippingOrder implements WorkItemHandler {
		
		private static int generateRandomIntIntRange(int min, int max) {
		    Random r = new Random();
		    return r.nextInt((max - min) + 1) + min;
		}
		
		private static String generateUuidOfIntWithLenght(int lenght) {
			return UUID.randomUUID().toString().substring(1, lenght).toUpperCase();
		}
		
		private static String generateTrackingNumber() {
			return "SNR-" + generateUuidOfIntWithLenght(8);
		}

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			Contract contract = (Contract) wi.getParameter("contract");
			Map<String, Object> results = new HashMap<>();
			EntityManager em = DispositionService.getEM();
			
			DispositionCustomer dispositionCustomer = contract.getDispositionCustomer();
			
			// Bei so hohen Zahlen kann man so relativ sicher eine Random Nummber vergeben. -> natürlich nicht so im echten Fall...
			// Für dieses Beisiel sind doppelungen aber so maximal unwahrscheinlich, dass ich diese lösung wähle. :)
			ShippingOrder shippingOrder = new ShippingOrder(generateRandomIntIntRange(0, 1000000000), generateTrackingNumber(), "DHL");
			
			em.getTransaction().begin();
			em.persist(shippingOrder);
			em.getTransaction().commit();
			
			List<ShippingOrder> shippingOrders = dispositionCustomer.getShippingOrders();
			
			shippingOrders.add(shippingOrder);
//			dispositionCustomer.setShippingOrders(shippingOrders);
			
			em.getTransaction().begin();
			em.merge(dispositionCustomer);
			em.getTransaction().commit();
			
			results.put("result", shippingOrder);
			wim.completeWorkItem(wi.getId(), results);
		}

	}
	
	public static class AssignOpenShippingItemsToShippingOrder implements WorkItemHandler {

		@Override
		public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
			wim.abortWorkItem(wi.getId());
		}

		@Override
		public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
			ContractPosition contractPosition = (ContractPosition) wi.getParameter("contractPosition");
			ShippingOrder shippingOrder = (ShippingOrder) wi.getParameter("shippingOrder");
			Map<String, Object> results = new HashMap<>();
			EntityManager em = DispositionService.getEM();
			
			List<ShippingPosition> shippingPositions = contractPosition.getShippingPositions();

			for (ShippingPosition shippingPosition : shippingPositions) {
				if (shippingPosition.getShippingOrder() == null) {
					shippingPosition.setShippingOrder(shippingOrder);
					em.getTransaction().begin();
					em.merge(shippingPosition);
					em.getTransaction().commit();
				}
			}
			
			em.getTransaction().begin();
			em.merge(contractPosition);
			em.getTransaction().commit();
			
			results.put("result", contractPosition);
			wim.completeWorkItem(wi.getId(), results);
		}

	}
	
}
