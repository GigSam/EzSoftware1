package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;

public class ReturnTransactionClass extends BalanceOperationClass implements ReturnTransaction {

	// Map with the barcode and quantity of product to update in the sale transaction
	private final Map<ProductType, Integer> returnedProduct = new HashMap<>();
	private SaleTransaction saleTransaction;
	private ReturnStatus status;
	private final Map<String, Product> productRFID = new HashMap<>();

	public ReturnTransactionClass(int orderId, String description, double amount, LocalDate date, String type,
			Map<ProductType, Integer> returned, SaleTransaction saleT, ReturnStatus retstatus) {
		super(orderId, description, amount, date, type);
		if (amount < 0)
			throw new RuntimeException(new InvalidQuantityException());
		if (date == null)
			throw new RuntimeException(new Exception());
		if (type == null || type != "DEBIT")
			throw new RuntimeException(new Exception());
		if (orderId < 0)
			throw new RuntimeException(new InvalidTransactionIdException());
		if (description == null)
			throw new RuntimeException(new Exception());
		if (returned == null)
			throw new RuntimeException(new Exception());
		if (saleT == null)
			throw new RuntimeException(new Exception());
		if (retstatus == null)
			throw new RuntimeException(new Exception());
		this.returnedProduct.putAll(returned);
		this.saleTransaction = saleT;
		this.status = retstatus;
	}

	public ReturnTransactionClass(SaleTransaction saleT, ReturnStatus retstatus) {
		super(-1, "RETURN", 0.0, LocalDate.now(), "DEBIT");
		if (saleT == null)
			throw new RuntimeException(new Exception());
		this.saleTransaction = saleT;
		try {
			this.setDescription("RETURN");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.status = retstatus;
	}

	@Override
	public Integer getReturnId() {
		return super.getBalanceId();
	}

	@Override
	public void setReturnId(Integer balanceId) {
		if (balanceId == null || balanceId < 0)
			throw new RuntimeException(new Exception());
		super.setBalanceId(balanceId);
	}

	@Override
	public Map<ProductType, Integer> getReturnedProduct() {
		return returnedProduct;
	}

	@Override
	public void setReturnedProduct(Map<ProductType, Integer> returnedProduct) {
		if (returnedProduct == null)
			throw new RuntimeException(new Exception());
		this.returnedProduct.clear();
		this.returnedProduct.putAll(returnedProduct);
	}

	@Override
	public SaleTransaction getSaleTransaction() {
		return saleTransaction;
	}

	@Override
	public void setSaleTransaction(SaleTransaction saleTransaction) {
		if (saleTransaction == null)
			throw new RuntimeException(new Exception());
		this.saleTransaction = saleTransaction;
	}

	@Override
	public String getStatus() {
		return status.name();
	}

	@Override
	public void setStatus(String status) {
		if (status == null)
			throw new RuntimeException(new Exception());
		try {
			this.status = ReturnStatus.valueOf(status);
		} catch (Exception e) {
			throw new RuntimeException(new Exception());
		}
	}

	@Override
	public double getMoney() {
		return super.getMoney() * (1.0 - saleTransaction.getDiscountRate());
	}

	@Override
	public void setMoney(double money) {
		if (money < 0)
			throw new RuntimeException(new Exception());
		super.setMoney(money);
	}

	public int addReturnProduct(ProductType product, int quantity) {
		if (product == null)
			throw new RuntimeException(new Exception());
		if (quantity <= 0)
			throw new RuntimeException(new InvalidQuantityException());
		SaleTransactionClass st = (SaleTransactionClass) this.saleTransaction;
		int amount = st.getProductsEntries().get(product.getBarCode()).getAmount();
		if (amount < quantity)
			return -1;
		this.returnedProduct.put(product, quantity);
		setMoney(getMoney() + product.getPricePerUnit() * quantity
				* (1 - st.getProductsEntries().get(product.getBarCode()).getDiscountRate()));
		return 1;
	}
	
	boolean addProductRFID(Product p) {
		if (p == null)
			return false;

		String RFID=p.getRFID();
		ProductTypeClass ptc = p.getProductType();

		if(RFID==null || !RFID.matches("\\d{12}") || ptc==null)
			return false;

		//Product insert on the RFID MAP
		SaleTransactionClass st = (SaleTransactionClass) this.saleTransaction;
		//It would be better if the research was done in the saleClass
		if (!st.getProductRFID().containsKey(RFID) || productRFID.containsKey(RFID))
			return false;
		else {
			//Insert on ReturnMap
			productRFID.put(RFID, p);
		}

		return true;
	}
	
	boolean deleteProductRFID(String RFID) {
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

		return true;
	}
	public Map<String, Product> getReturnedRFID(){
		return productRFID;
	}
}
