package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;

public class LoyaltyCardClass implements LoyaltyCard {
	private int points;
	private String cardCode;
	
	public LoyaltyCardClass(String cardCode, int points)
	{		
		if(	cardCode == null|| (cardCode.length() !=10 && cardCode.length()!=0)) throw new RuntimeException(new InvalidCustomerCardException());
		this.points=points;
		this.cardCode=cardCode;		
	}
		
public static String createCardCode(int i) 
	    { 
	        String theAlphaNumericS;
	        StringBuilder builder;
	        
	        theAlphaNumericS = "0123456789"; 
	        //create the StringBuffer
	        builder = new StringBuilder(i); 
	    	
	        if(i!=10)
	    	return "";
	    	else{
	        for (int m = 0; m < i; m++) { 
	            // generate numeric
	            int myindex 
	                = (int)(theAlphaNumericS.length() 
	                        * Math.random()); 
	            // add the characters
	            builder.append(theAlphaNumericS 
	                        .charAt(myindex)); 
	        } 

	        return builder.toString(); 
	    	}
	    } 

	
	public static boolean checkCardCode(String newCustomerCard) {
		if(newCustomerCard==null || newCustomerCard.isEmpty()|| newCustomerCard.matches("\\d{10}")) return true;
	    return false;
	}
	
	public Integer getPoints() {
		return points;
	}

	public boolean setPoints(Integer points) {
		if(points<0) return false;
		this.points = points;
		return true;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		if(	cardCode == null|| (cardCode.length() !=10 && cardCode.length()!=0)) throw new RuntimeException(new InvalidCustomerCardException());
		this.cardCode = cardCode;
	}
	
	public boolean updatePoints(int toBeAdded) {
		if(points + toBeAdded < 0)
			return false;
		points += toBeAdded;
		return true;
	}

}

