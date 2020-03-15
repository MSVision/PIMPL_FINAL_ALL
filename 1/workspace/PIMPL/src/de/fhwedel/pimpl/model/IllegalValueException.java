package de.fhwedel.pimpl.model;

@SuppressWarnings("serial")
public class IllegalValueException extends Exception {
	
	private String disc;

	/**
     * Constructs an <code>IllegalValueException</code> with no
     * detail message.
     */
    public IllegalValueException() {
        super();
    }
    
    public IllegalValueException(String discription) {
    	super();
        this.disc = discription;
    }
    
    public String getDiscription() {
    	return this.disc;
    }
    
    @Override
    public String getMessage() {
    	return this.disc;
    }
	 
}
