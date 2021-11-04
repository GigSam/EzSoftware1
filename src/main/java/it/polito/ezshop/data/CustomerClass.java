package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;

public class CustomerClass implements Customer {

	private String customerName;
	private String customerCard;
	private Integer id;
	private Integer points;
	

	public CustomerClass(int id, String customerName, String customerCard, Integer points) {
		if(	customerCard == null|| (customerCard.length() !=10 && customerCard.length()!=0)) throw new RuntimeException(new InvalidCustomerCardException());
		if(id <= 0) throw new RuntimeException(new InvalidCustomerIdException());
		if(	customerName == null || customerName.isEmpty()) throw new RuntimeException(new InvalidCustomerNameException());
		this.id = id;
		this.customerName = customerName;
		this.customerCard = customerCard;
		this.points = points;
	}
	
	public int updateCustomerPoints(int toBeAdded) {
		if(points + toBeAdded < 0) return 0;
		points += toBeAdded;
		return points;
	}


	@Override
	public String getCustomerName() {		
		return customerName;
	}

	@Override
	public void setCustomerName(String customerName) {
		if(customerName==null || customerName.length() <= 0)
			throw new RuntimeException(new InvalidCustomerNameException());	
		this.customerName=customerName;		
	}

	@Override
	public String getCustomerCard() {
		return customerCard;
	}

	@Override
	public void setCustomerCard(String customerCard) {
	if(	customerCard == null|| (customerCard.length() !=10 && customerCard.length()!=0)) throw new RuntimeException(new InvalidCustomerCardException());
	this.customerCard=customerCard;		
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		if(id == null || id <= 0)
			throw new RuntimeException(new InvalidCustomerIdException());	
		this.id=id;		
	}

	@Override
	public Integer getPoints() {
		return points;
	}

	@Override
	public void setPoints(Integer points) {
		this.points=points;
		
	}
	

}
