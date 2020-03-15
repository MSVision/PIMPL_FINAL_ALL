package de.fhwedel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

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
			CustomerService.props.load(new FileInputStream("..\\pimpl.conf"));
		} catch (IOException e) {
			System.err.println("Failed to load configuration file (" + new File("..\\pimpl.conf").getAbsolutePath()
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
	
	public Customer getCustomerByName(String name) throws UnknownCustomer {
		try {
			System.out.println("searching for name " + name);			
			return (Customer)CustomerService.em
				.createNamedQuery("getCustomerByName")
				.setParameter("name", name)
				.getSingleResult()
				;
		} catch(Exception e) {
			throw new UnknownCustomer();			
		}
	}
	
	public void updateCustomer(Customer c) throws UpdateFailed {
		try {
			System.out.println("updating " + c);			
			CustomerService.em.getTransaction().begin();
			CustomerService.em.merge(c);
			CustomerService.em.getTransaction().commit();		
		} catch(Exception e) {
			CustomerService.em.getTransaction().rollback();
			throw new UpdateFailed();
		}
	}
	
	public Customer getCustomerReputation(Customer c) {
		System.out.println("returning " + (200 + Math.abs(c.getName().hashCode() % 50)) + " for " + c);
		c.setReputation(200 + Math.abs(c.getName().hashCode() % 50));
		return c;
	} 
	
	public void log(String s) {
		System.out.println("CustomerService.log(): " + s);		
	} 
	
	// TODO: THINK ABOUT THIS -> Ab hier Shipping Service! 
	
	public Customer getCustomerByCustomerNumber(Integer customerNumber) throws UnknownCustomer {
		try {
			System.out.println("searching for customer number " + customerNumber);			
			return (Customer)CustomerService.em
				.createNamedQuery("getShippingCustomerByCustomerNumber")
				.setParameter("customerNumber", customerNumber)
				.getSingleResult();
		} catch(Exception e) {
			throw new UnknownCustomer();			
		}
	}
	
//	If () {
//		
//	}
	
	public boolean checkSign(ShippingCustomer shippingCustomer, String transportRequestCustomerIdentifier) {
		If () {
			
		}
		
		Customer c = null;
		try {
			c = selectCustomer(customerNumber);
		} catch (UnknownCustomer e) {
			System.err.println("Kundennummer " + customerNumber + " wurde noch nicht vergeben!");
		}
	
		return c.getSign().equals(customerSign);
	}
	
	public Product selectProduct(String productCode) throws InvalidProduct {
//		try {
//			System.out.println("searching for customer number " + customerNumber);			
//			return (Customer)CustomerService.em
//				.createNamedQuery("getCustomerByCustomerNumber")
//				.setParameter("customerNumber", customerNumber)
//				.getSingleResult()
//				;
//		} catch(Exception e) {
//			throw new UnknownCustomer();			
//		}
		
		@SuppressWarnings("unchecked")
		List<Product> allProducts = em.createQuery("SELECT p FROM Product p").getResultList();

		for (Product p : allProducts) {
			if (p.getProductCode().equals(productCode)) {
				return p;
			}
		}
		throw new InvalidProduct();
	}
	
	public Contract createContract(Integer identifier) throws UnkownCustomerID {
//		private Integer id;
//		private Integer contractNumber;
//		private Integer weight;
//		// Relations
//		private Customer customer;
//		private Product product;
//		private Adress fromAdress;
//		private Adress toAdress;
		return null;
	}
	
}
