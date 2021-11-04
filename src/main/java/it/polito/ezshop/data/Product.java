package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidRFIDException;

public class Product {
	private String RFID;
	private ProductTypeClass productType;
	
	
	public Product(String rFID, ProductTypeClass productType) {
		super();
		if(rFID == null || !rFID.matches("\\d{12}"))
			throw new RuntimeException();
		if(productType == null)
			throw new RuntimeException();
		RFID = rFID;
		this.productType = productType;
	}
	public String getRFID() {
		return RFID;
	}
	public void setRFID(String rFID) {
		if(rFID == null || !rFID.matches("\\d{12}"))
			throw new RuntimeException();
		RFID = rFID;
	}
	public ProductTypeClass getProductType() {
		return productType;
	}
	public void setProductType(ProductTypeClass productType) {
		if(productType == null)
			throw new RuntimeException();
		this.productType = productType;
	}
	
	public static String calculateRFID(String input, int step) throws InvalidRFIDException
    { 
		Long l = null;		
	try {			
			l = Long.parseLong(input);			
		} 
	catch (Exception e){
			throw new InvalidRFIDException();
		}		
		l += step;
		return String.format("%012d", l);
		
    }
}
