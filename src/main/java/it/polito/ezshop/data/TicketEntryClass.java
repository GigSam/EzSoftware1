package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidQuantityException;

public class TicketEntryClass implements TicketEntry {

	private ProductType productType;
	private int amount;
	private double discountRate;

	
	public TicketEntryClass(ProductType p, int amount, double discRate) throws Exception {
		if(p==null) throw new Exception();
		if(amount<0) throw new InvalidQuantityException();
		if(discRate<0 || discRate>1) throw new InvalidDiscountRateException();
		this.productType=p;
		this.discountRate=discRate;
		this.amount=amount;
	}
	
	public TicketEntryClass(ProductType p, int amount) throws Exception {
		this(p, amount, 0.0);
	}
	
	public ProductType getProductType() {
		return this.productType;
	}
	@Override
	public String getBarCode() {
		return this.productType.getBarCode();
	}

	@Override
	public void setBarCode(String barCode) {
		this.productType.setBarCode(barCode);
	}

	@Override
	public String getProductDescription() {
		return this.productType.getProductDescription();
	}

	@Override
	public void setProductDescription(String productDescription) {
		this.productType.setProductDescription(productDescription);
	}

	@Override
	public int getAmount() {
		return this.amount;
	}

	@Override
	public void setAmount(int amount) {
		if(amount<=0) throw new RuntimeException(new InvalidQuantityException());     //changed (<=)
		this.amount=amount;
	}

	@Override
	public double getPricePerUnit() {
		return this.productType.getPricePerUnit();
	}

	@Override
	public void setPricePerUnit(double pricePerUnit) {
		this.productType.setPricePerUnit(pricePerUnit);
	}

	@Override
	public double getDiscountRate() {
		return this.discountRate;
	}

	@Override
	public void setDiscountRate(double discountRate) {
		if(discountRate<0 || discountRate>1) throw new RuntimeException(new InvalidDiscountRateException());
		this.discountRate=discountRate;
	}

}
