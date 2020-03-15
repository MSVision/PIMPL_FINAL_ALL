package de.fhwedel.pimpl.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.i18n.shared.DateTimeFormat;

@SuppressWarnings("serial")
public abstract class Helper implements Serializable {
	
	// Deutscher PLZ-Adressraum
	private static final Integer PLZ_MIN = 00000;
	private static final Integer PLZ_MAX = 99999;
	
	public static boolean validIntegerBaseValue(Integer value) {
        return value != null && value >= 0;
    } 
	
	// TODO: Unsure -> String.isEmpty() wirklich falsch -> laut Model NEIN
	public static boolean validStringBaseValue(String value) {
        return value != null && !value.isEmpty();
    }
	
	public static boolean validZip(Integer zip) throws IllegalValueException {
		if (!validIntegerBaseValue(zip)) {
			throw new IllegalValueException();
		}
		return zip >= PLZ_MIN && zip <= PLZ_MAX;
	}
	
	public static boolean isDateValid(String dateStr) {
		if (dateStr == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
    	DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
    	try {
    		dtf.parseStrict(dateStr);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
	
	public static String parseDateToStr(Date date) {
		if (date == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
		return dtf.format(date);
	}
	
	public static Date parseStrToDate(String dateStr) {
		if (dateStr == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		Date result;
    	DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
    	try {
    		result = dtf.parseStrict(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Der übergebe String empfricht nicht dem Datumsformat! -> (dd.mm.yyyy)");
        }
        return result;
    }
	
	
}
