package it.polito.ezshop.data;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidPaymentException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;

public class SaleTransactionClass extends BalanceOperationClass implements SaleTransaction {

	public SaleTransactionClass(double price, String paymentType, Time time, SaleStatus status, LoyaltyCard loyaltyCard,
			Integer ticketNumber, Map<String, TicketEntryClass> ticketEntries, double discountRate) throws Exception {
		super(ticketNumber, "SALE", price, LocalDate.now(), "CREDIT");
		if (price < 0)
			throw new RuntimeException(new Exception());
		if (time == null)
			throw new RuntimeException(new Exception());
		if (status == null)
			throw new RuntimeException(new Exception());
		if (ticketNumber < 0)
			throw new RuntimeException(new InvalidTransactionIdException());
		if (ticketEntries == null)
			throw new RuntimeException(new Exception());
		if (discountRate < 0 || discountRate > 1)
			throw new InvalidDiscountRateException();
		if ((!paymentType.equals("CASH") && !paymentType.equals("CREDIT_CARD")) && !paymentType.isEmpty()
				|| paymentType == null)
			throw new RuntimeException(new InvalidPaymentException());
		if (loyaltyCard == null)
			throw new RuntimeException(new InvalidCustomerCardException());
		this.setPaymentType(paymentType);
		this.time = time;
		this.status = status;
		this.loyaltyCard = loyaltyCard;
		this.ticketEntries = ticketEntries;
		this.discountRate = discountRate;
	}

	public SaleTransactionClass(Time time, SaleStatus saleStatus) {
		this(-1, "SALE", 0.0, LocalDate.now(), "CREDIT", "", time, saleStatus, null, new HashMap<>(), 0.0);
	}

	public SaleTransactionClass(int transactionId, String description, double money, LocalDate date, String type,
			String paymentType, Time time, SaleStatus status, LoyaltyCard loyaltyCard,
			Map<String, TicketEntryClass> ticketEntries, double discountRate) {
		super(transactionId, description, money, date, type);
		this.setPaymentType(paymentType);
		this.time = time;
		this.status = status;
		this.loyaltyCard = loyaltyCard;
		this.ticketEntries = ticketEntries;
		this.discountRate = discountRate;
	}

	private Time time;
	private SaleStatus status;
	private LoyaltyCard loyaltyCard;
	// (barcode, TicketEntryClass)
	private Map<String, TicketEntryClass> ticketEntries;
	private double discountRate;
	private String paymentType = "";
	private Map<String, Product> productRFID = new HashMap<>();

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		if (time == null)
			throw new RuntimeException(new Exception());
		this.time = time;
	}

	public void setStatus(SaleStatus status) {
		if (status == null)
			throw new RuntimeException(new Exception());
		this.status = status;
	}

	public void setLoyaltyCard(LoyaltyCard l) {
		if (l == null)
			throw new RuntimeException(new InvalidCustomerCardException());
		this.loyaltyCard = l;
	}

	public LoyaltyCard getLoyaltyCard() {
		return this.loyaltyCard;
	}

	public Map<String, TicketEntryClass> getProductsEntries() {
		return this.ticketEntries;
	}

	@Override
	public Integer getTicketNumber() {
		return super.getBalanceId();
	}

	@Override
	public void setTicketNumber(Integer ticketNumber) {
		if (ticketNumber < 0 || ticketNumber == null)
			throw new RuntimeException(new InvalidTransactionIdException());
		super.setBalanceId(ticketNumber);
	}

	@Override
	public List<TicketEntry> getEntries() {
		List<TicketEntry> res = new ArrayList<TicketEntry>();
		ticketEntries.forEach((s, te) -> {
			try {
				res.add(new TicketEntryClass(te.getProductType(), te.getAmount(), te.getDiscountRate()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return res;
	}

	@Override
	public void setEntries(List<TicketEntry> entries) {
		if (entries == null)
			throw new RuntimeException(new Exception());
		this.ticketEntries = new HashMap<>();
		for (int i = 0; i < entries.size(); i++) {
			this.ticketEntries.put(entries.get(i).getBarCode(), (TicketEntryClass) entries.get(i));
		}

	}

	@Override
	public double getDiscountRate() {
		return this.discountRate;
	}

	@Override
	public void setDiscountRate(double discountRate) {
		if (discountRate < 0 || discountRate > 1)
			throw new RuntimeException(new InvalidDiscountRateException());
		this.discountRate = discountRate;
	}

	public boolean addProduct(ProductType product, int quantity) {
		if (product == null)
			return false;
		if (quantity <= 0)
			return false;
		if (ticketEntries.containsKey(product.getBarCode())) {
			TicketEntryClass t = ticketEntries.get(product.getBarCode());
			t.setAmount(t.getAmount() + quantity);
			ticketEntries.remove(product.getBarCode());
			ticketEntries.put(product.getBarCode(), t);
		} else {
			TicketEntryClass t;
			try {
				t = new TicketEntryClass(product, quantity);
				ticketEntries.put(product.getBarCode(), t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		double a = this.getPrice();
		a += product.getPricePerUnit() * quantity * (1 - this.discountRate);
		this.setPrice(a);
		return true;
	}

	public boolean deleteProduct(ProductType product, int quantity) {
		if (quantity <= 0)
			throw new RuntimeException(new InvalidQuantityException());
		if (product == null)
			throw new RuntimeException(new Exception());
		if (ticketEntries.containsKey(product.getBarCode())) {
			TicketEntryClass t = ticketEntries.get(product.getBarCode());
			int amount = t.getAmount();
			double discountOnProduct = t.getDiscountRate();
			double a = this.getMoney();
			if (amount - quantity < 0)
				return false;

			else if (amount == quantity) {
				ticketEntries.remove(t.getBarCode());
			
			} else {
				t.setAmount(t.getAmount() - quantity);
			}

			a -= product.getPricePerUnit() * quantity * (1 - discountOnProduct);
			this.setMoney(a);
			return true;
		}
		return false;
	}

	public SaleStatus getStatus() {
		return this.status;
	}

	public void checkout() {
		//Should the price already be set here?
		double a = 0.0;
		if (ticketEntries.size() == 0) {
			this.setPrice(a);
			this.status = SaleStatus.CLOSED;
			return;
		}
		for (TicketEntryClass te : ticketEntries.values()) {
			a += (te.getPricePerUnit() * te.getAmount()) * (1 - te.getDiscountRate());
		}
		for (Product p : productRFID.values()) {
			a += (p.getProductType().getPricePerUnit() * (1 -  ticketEntries.get(p.getProductType().getBarCode()).getDiscountRate()));
		}

		a = a * (1 - this.getDiscountRate());


		this.setPrice(a);
		this.status = SaleStatus.CLOSED;
	}

	public boolean addProductDiscount(ProductType product, double discount) {
		if (discount < 0 || discount > 1)
			throw new RuntimeException();
		if (ticketEntries.containsKey(product.getBarCode())) {
			ticketEntries.get(product.getBarCode()).setDiscountRate(discount);
			return true;
		}
		return false;
	}

	@Override
	public double getPrice() {
		return super.getMoney();
	}

	@Override
	public void setPrice(double price) {
		if (price < 0)
			throw new RuntimeException(new Exception());
		super.setMoney(price);
	}
	

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		if (!paymentType.equals("CASH") && !paymentType.equals("CREDIT_CARD") && !paymentType.isEmpty())
			throw new RuntimeException(new InvalidPaymentException(paymentType));
		this.paymentType = paymentType;
	}
	
	// this must also update the related ticket entry
	public boolean addProductRFID(Product p) {
		if (p == null)
			return false;

		String RFID=p.getRFID();
		ProductTypeClass ptc = p.getProductType();

		if(RFID==null || !RFID.matches("\\d{12}") || ptc==null)
			return false;

		//Product insert on the RFID MAP
		if (productRFID.containsKey(RFID))
			return false;
		else
			productRFID.put(RFID,p);

		//Product insert on the ticket entry
		TicketEntryClass t = null;
		if ((t = ticketEntries.get(ptc.getBarCode())) == null) {
			try {
				t = new TicketEntryClass(p.getProductType(), 0);
				ticketEntries.put(p.getProductType().getBarCode(), t);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		//New product -> New Price of the sale
		//I just added new product with his sale -> Is sale discount added at the checkout?
		double pPrice = p.getProductType().getPricePerUnit() * (1 - t.getDiscountRate());
		this.setPrice(this.getPrice() + pPrice);

		return true;
	}

	// this must also update the related ticket entry
	public boolean deleteProductRFID(String RFID) {
		if (RFID == null || !RFID.matches("\\d{12}") || !productRFID.containsKey(RFID) )
			return false;

		Product p = productRFID.get(RFID);
		if(p==null)
			return false;

		ProductTypeClass pTC = p.getProductType();
		if (p.getProductType()==null)
			return false;

		//Delete from Product Map
		productRFID.remove(RFID);

		//Delete from ticketEntries -> Random product?
		TicketEntryClass t = null;
		if ((t = ticketEntries.get(pTC.getBarCode())) == null)
			return false;

		//Set new Price -> Above problems
		double discount = t.getDiscountRate();
		double pPrice = pTC.getPricePerUnit() * (1 - discount);
		this.setPrice(this.getPrice()-pPrice);

		return true;
	}


	public Map<String, Product> getProductRFID(){
		return productRFID;
	}
	public void setProductRFID(Map<String, Product> rfids) {
		if(rfids == null)
			throw new RuntimeException();
		productRFID = rfids;
	}
}