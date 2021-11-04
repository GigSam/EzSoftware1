package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import it.polito.ezshop.data.*;
import it.polito.ezshop.data.EZShop;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class ReturnTransactionAPITest {
	private final EZShop ezshop = new EZShop();
	private String username = "testReturnTransactionApiEZShop";
	private String password = "password";
	private int createdUserId = -1;
	private int createdCashier = -1;
	private String usernameC = "testSaleTransactionApiUserCashier";
	private ProductType pt1 = null;
	private int newProdId1 = -1;
	private int newProdId2 = -1;
	private ProductType pt2 = null;
	private int id = -1;
	private int retId=-1;
    private int retId2=-1;
    
	@Before
	public void init() throws Exception {
		ezshop.reset();
		User u = null;
		if ((u = ezshop.login(username, password)) == null) {
			createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
		} else if (u.getRole().equals(RoleEnum.Cashier.name())) {
			do {
				username += "1234";
				createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
			} while (createdUserId < 0);
		}

		while (true) {
			if ((u = ezshop.login(usernameC, password)) == null) {
				createdCashier = ezshop.createUser(usernameC, password, RoleEnum.Cashier.name());
				if (createdCashier > 0)
					break;
				else
					usernameC += "1234";
			} else if (!u.getRole().equals(RoleEnum.Cashier.name())) {
				ezshop.logout();
				usernameC += "1234";
				createdCashier = ezshop.createUser(usernameC, password, RoleEnum.Cashier.name());
				if (createdCashier > 0)
					break;
			} else
				break;
		}
		// create test products changing some digits and updating their quantity
		ezshop.login(username, password);
		if ((pt1 = ezshop.getProductTypeByBarCode("4006381333900")) == null) {

			newProdId1 = ezshop.createProductType("testSaleTransactionProduct", "4006381333900", 3.5, null);
			pt1=ezshop.getProductTypeByBarCode("4006381333900");
		}

		ezshop.updatePosition(ezshop.getProductTypeByBarCode("4006381333900").getId(), "3-ctest-3");
		ezshop.updateQuantity(newProdId1 > 0 ? newProdId1 : pt1.getId(), 5);
		// to see the quantity of the updated product
		System.out.println(ezshop.getProductTypeByBarCode("4006381333900").getQuantity());

		if ((pt2 = ezshop.getProductTypeByBarCode("4006381333931")) == null) {

			newProdId2 = ezshop.createProductType("testSaleTransactionProduct", "4006381333931", 7.0, null);
			pt2=ezshop.getProductTypeByBarCode("4006381333931");
		}
		ezshop.updatePosition(ezshop.getProductTypeByBarCode("4006381333931").getId(), "3-ctest-4");
		ezshop.updateQuantity(newProdId2 > 0 ? newProdId2 : pt2.getId(), 10);
		// to see the quantity of the updated product
		System.out.println(ezshop.getProductTypeByBarCode("4006381333931").getQuantity());
		// start and add some products to a new sale transaction
		id = ezshop.startSaleTransaction();
		ezshop.addProductToSale(id, "4006381333900", 4);
		ezshop.addProductToSale(id, "4006381333931", 9);
		SaleTransactionClass stc = (SaleTransactionClass) ezshop.getAccountBook().getSaleTransaction(id);
		ezshop.recordBalanceUpdate(100);
		int orderId = ezshop.payOrderFor("4006381333900", 10, 1);
		ezshop.recordOrderArrivalRFID(orderId, "000000001000");
		ezshop.addProductToSaleRFID(stc.getTicketNumber(), "000000001000");
		stc.setStatus(SaleStatus.CLOSED);
		
		
		ezshop.logout();
	}

	@After
	public void after() throws Exception {
		if (createdUserId > 0) {
			ezshop.login(username, password);
			// delete created product
			if (newProdId1 < 0) {
				ezshop.updateProduct(pt1.getId(), pt1.getProductDescription(), pt1.getBarCode(), pt1.getPricePerUnit(),
						pt1.getNote());
			} else
				ezshop.deleteProductType(newProdId1);
			if (newProdId2 < 0) {
				ezshop.updateProduct(pt2.getId(), pt2.getProductDescription(), pt2.getBarCode(), pt2.getPricePerUnit(),
						pt2.getNote());
			} else
				ezshop.deleteProductType(newProdId2);

			ezshop.deleteUser(createdUserId);
			if (createdCashier > 0)
				ezshop.deleteUser(createdCashier);
		}
		if (id > 0) {
			ezshop.getAccountBook().removeSaleTransaction(id);
			id=-1;
			
		}
		if(retId2>0) {
			ezshop.getAccountBook().removeReturnTransaction(retId2);
			retId2=-1;
		}
		ezshop.reset();
	}

	@Test
	public void testStartReturnTransaction() throws Exception {
		EZShop ezShop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.startReturnTransaction(2);
		});
		// login Admin
		ezshop.login(username, password);
		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.startReturnTransaction(null);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.startReturnTransaction(-1);
		});
		// wrong transactionId
		AccountBookClass aB = ezshop.getAccountBook();
		try {
			aB.getReturnTransaction(10);
		}catch(Exception e){
			assertEquals(-1, ezshop.startReturnTransaction(10), 0.0001);
		}
		// try to create a return transaction on a sale that has not been payed
		SaleTransactionClass st = (SaleTransactionClass) ezshop.getSaleTransaction(id);
		st.setStatus(SaleStatus.STARTED);
		assertEquals(-1, ezshop.startReturnTransaction(st.getTicketNumber()), 0.0001);
		// change the status to payed
		st.setStatus(SaleStatus.PAYED);
		// valid
		assertTrue(ezshop.startReturnTransaction(id) != -1);
		
	}

	@Test
	public void testReturnProduct() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.returnProduct(35, "4006381333900", 1);
		});
		// login Admin
		ezshop.login(username, password);
		// start a new return transaction
		int retId = ezshop.startReturnTransaction(id);
		ezshop.logout();
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.returnProduct(id, "4006381333900", 1);
		});
		ezshop.login(username, password);
		// null returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.returnProduct(null, "4006381333900", 1);
		});
		// invalid returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.returnProduct(-1, "4006381333900", 1);
		});
		// wrong returnId
		assertEquals(false, ezshop.returnProduct(35, "4006381333900", 1));
		// null productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.returnProduct(retId, null, 1);
		});
		// invalid productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.returnProduct(retId, "", 1);
		});
		// invalid productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.returnProduct(retId, "111111111111111111", 1);
		});
		// invalid amount
		assertThrows(InvalidQuantityException.class, () -> {
			ezshop.returnProduct(retId, pt1.getBarCode(), -1);
		});
		// try to return a product that wasn't in the transaction
		assertEquals(false, ezshop.returnProduct(retId, "400638133390", 1));

		// valid case
		// first, see how many products are there for the type we want to return, both
		// in the shop and in the relative sale transaction
		int q1 = pt1.getQuantity();
		SaleTransactionClass stc = (SaleTransactionClass) ezshop.getAccountBook().getReturnTransaction(retId)
				.getSaleTransaction();
		int q2 = stc.getProductsEntries().get(pt1.getBarCode()).getAmount();
		// return product
		assertTrue(ezshop.returnProduct(retId, pt1.getBarCode(), 1));
		// check that the quantity has been updated in the sale transaction
		assertEquals(q2 , stc.getProductsEntries().get(pt1.getBarCode()).getAmount(), 0.0001);
		// too much return
		assertFalse(ezshop.returnProduct(retId, pt1.getBarCode(), q2+1));
		assertTrue(ezshop.returnProduct(retId, pt1.getBarCode(), q2-1));
		// undo the operation
		ezshop.addProductToSale(stc.getTicketNumber(), pt1.getBarCode(), 1);
	}
	
	
	@Test
	public void testReturnProductRFID() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.returnProductRFID(35, "000000001000");
		});
		// login Admin
		ezshop.login(username, password);
		// start a new return transaction
		int retId = ezshop.startReturnTransaction(id);
		ezshop.logout();
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.returnProductRFID(35, "000000001000");
		});
		ezshop.login(username, password);
		// null returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.returnProductRFID(null, "000000001000");
		});
		// invalid returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.returnProductRFID(-1, "000000001000");
		});
		// wrong returnId
		assertFalse(ezshop.returnProductRFID(35, "000000001000"));
		// null RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.returnProductRFID(retId, null);
		});
		// invalid RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.returnProductRFID(retId, "");
		});
		// invalid RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.returnProductRFID(retId, "111111111111111111");
		});

		// valid case
		// first, see how many products are there for the type we want to return, both
		// in the shop and in the relative sale transaction
		int q1 = pt1.getQuantity();
		SaleTransactionClass stc = (SaleTransactionClass) ezshop.getAccountBook().getReturnTransaction(retId)
				.getSaleTransaction();
		// return product
		assertTrue(ezshop.returnProductRFID(retId, "000000001000"));
		// check that the product has been inserted in the return transaction map of products
		assertTrue(((ReturnTransactionClass)ezshop.getAccountBook().getReturnTransaction(retId)).getReturnedRFID().containsKey("000000001000"));
	}
	

	@Test
	public void testEndReturnTransaction() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.endReturnTransaction(2, true);
			ezshop.endReturnTransaction(null, true);
		});
		// login Admin
		ezshop.login(username, password);
		// start a new return transaction
		retId = ezshop.startReturnTransaction(id);
		ezshop.returnProduct(retId, pt1.getBarCode(), 1);
		ezshop.returnProduct(retId, pt2.getBarCode(), 1);
		// null returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.endReturnTransaction(null, true);
		});
		// invalid returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.endReturnTransaction(-1, true);
		});
		// wrong returnId
		assertEquals(false, ezshop.endReturnTransaction(Integer.MAX_VALUE, true));

		int q1 = ezshop.getProductTypeByBarCode(pt1.getBarCode()).getQuantity();
		int q2 = ezshop.getProductTypeByBarCode(pt2.getBarCode()).getQuantity();

		// valid-->commit==false

		assertTrue(ezshop.endReturnTransaction(retId, false));
		assertEquals(Integer.valueOf(q1), ezshop.getProductTypeByBarCode(pt1.getBarCode()).getQuantity());
		assertEquals(Integer.valueOf(q2), ezshop.getProductTypeByBarCode(pt2.getBarCode()).getQuantity());
		assertThrows(Exception.class, () -> {
			ezshop.getAccountBook().getReturnTransaction(retId);
		});
		retId=-1;
		
	    retId2 = ezshop.startReturnTransaction(id);
		ezshop.returnProduct(retId2, pt1.getBarCode(), 1);
		ezshop.returnProduct(retId2, pt2.getBarCode(), 1);
		
		// valid-->commit==true
		assertTrue(ezshop.endReturnTransaction(retId2, true));
		assertEquals(Integer.valueOf(q1+1), ezshop.getProductTypeByBarCode(pt1.getBarCode()).getQuantity());
		assertEquals(Integer.valueOf(q2+1), ezshop.getProductTypeByBarCode(pt2.getBarCode()).getQuantity());
		assertFalse(ezshop.endReturnTransaction(retId2, false));
		
		
	}

	@Test
	public void testDeleteReturnTransaction() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.deleteReturnTransaction(2);
			ezshop.deleteReturnTransaction(null);
		});
		// login Admin
		ezshop.login(username, password);
		// start a new return transaction
		int retId = ezshop.startReturnTransaction(id);
		ezshop.returnProduct(retId, pt1.getBarCode(), 1);
		ezshop.returnProduct(retId, pt2.getBarCode(), 1);
		ezshop.endReturnTransaction(retId, true);
		// null returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteReturnTransaction(null);
		});
		// invalid returnId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteReturnTransaction(-1);
		});
		// wrong returnId
		assertEquals(false, ezshop.deleteReturnTransaction(35));
		// valid
		int q1 = ezshop.getProductTypeByBarCode(pt1.getBarCode()).getQuantity();
		int q2 = ezshop.getProductTypeByBarCode(pt2.getBarCode()).getQuantity();
		SaleTransactionClass stc=(SaleTransactionClass) ezshop.getAccountBook().getReturnTransaction(retId).getSaleTransaction();
		int sq1=stc.getProductsEntries().get(pt1.getBarCode()).getAmount();
		int sq2=stc.getProductsEntries().get(pt2.getBarCode()).getAmount();
		assertEquals(true, ezshop.deleteReturnTransaction(retId));
		
		assertEquals(sq1+1,stc.getProductsEntries().get(pt1.getBarCode()).getAmount());
		assertEquals(sq2+1,stc.getProductsEntries().get(pt2.getBarCode()).getAmount());
		
		assertEquals(Integer.valueOf(q1-1), ezshop.getProductTypeByBarCode(pt1.getBarCode()).getQuantity());
		assertEquals(Integer.valueOf(q2-1), ezshop.getProductTypeByBarCode(pt2.getBarCode()).getQuantity());
		
		//try to delete twice the same return transaction
		assertFalse(ezshop.deleteReturnTransaction(retId));

	}
}