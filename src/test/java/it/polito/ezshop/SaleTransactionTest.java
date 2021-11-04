package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.ezshop.data.*;
import org.junit.*;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;

public class SaleTransactionTest {
	// double price, String paymentType, Time time, SaleStatus status, LoyaltyCard
	// loyaltyCard,Integer ticketNumber, Map<String, TicketEntryClass>
	// ticketEntries, double discountRate
	@Test
	public void testSaleTransactionConstructor() {
		// invalid loyalty card
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CREDIT_CARD",
					new Time(System.currentTimeMillis()), SaleStatus.STARTED, null, 2, new HashMap<>(), 0.1);
		});
		// invalid discount rate (<0)
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CASH", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), -1);
		});
		// invalid discount rate (>1)
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CASH", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 2);
		});
		// invalid payment type
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, " ", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
		});
		// invalid price
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(-1, "CASH", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
		});
		// invalid time
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CASH", null, SaleStatus.STARTED,
					new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
		});
		// invalid status
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CASH", new Time(System.currentTimeMillis()),
					null, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
		});
		// invalid ticketEntries
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CASH", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, null, 0.1);
		});
		// invalid ticketNumber
		assertThrows(Exception.class, () -> {
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CASH", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), -1, new HashMap<>(), 0.1);
		});
		// valid
		try {
			Time t = new Time(System.currentTimeMillis());
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CREDIT_CARD", t, SaleStatus.STARTED,
					new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
			assertEquals(10.0, stc.getMoney(), 0.001);
			assertEquals("CREDIT_CARD", stc.getPaymentType());
			assertEquals(t, stc.getTime());
			assertEquals(SaleStatus.STARTED, stc.getStatus());
			assertEquals("1234567890", stc.getLoyaltyCard().getCardCode());
			assertEquals(2, stc.getBalanceId());
			assertTrue(new HashMap<>().equals(stc.getProductsEntries()));
			assertEquals(0.1, stc.getDiscountRate(), 0.0001);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testAddProductToSale() throws InvalidQuantityException, Exception {
		SaleTransactionClass stc = new SaleTransactionClass(10.0, "CREDIT_CARD", new Time(System.currentTimeMillis()),
				SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 0, new HashMap<>(), 0.1);
		// initially, the hash map shouldn't have any element
		assertTrue(stc.getProductsEntries().equals(new HashMap<>()));
		// try adding a void product
		assertFalse(stc.addProduct(null, 3));
		// try adding a product with <0 quantity
		try {
			assertFalse(stc.addProduct(new ProductTypeClass(3, "null", "400638133390", 303.0, "notes"), -1));
		} catch (InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException e) {
			e.printStackTrace();
		}
		// valid
		try {
			assertTrue(stc.addProduct(new ProductTypeClass(3, "null", "400638133390", 303.0, "notes"), 10));
			assertTrue(stc.addProduct(new ProductTypeClass(3, "null", "400638133390", 303.0, "notes"), 10));			
		} catch (InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTime() throws Exception {

		Time t = new Time(System.currentTimeMillis());
		SaleTransactionClass stc = new SaleTransactionClass(10.0, "CREDIT_CARD", t, SaleStatus.STARTED,
				new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
		assertThrows(Exception.class, () -> {
			stc.setTime(null);
		});

		stc.setTime(t);
		assertEquals(t, stc.getTime());
	}

	@Test
	public void testStatus() throws Exception {
		SaleTransactionClass stc = new SaleTransactionClass(10.0, "CREDIT_CARD", new Time(System.currentTimeMillis()),
				SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
		SaleStatus st = SaleStatus.STARTED;
		// test getter
		assertTrue(st == stc.getStatus());
		// test setter
		// invalid
		assertThrows(Exception.class, () -> {
			stc.setStatus(null);
		});
		// valid
		stc.setStatus(SaleStatus.CLOSED);
		SaleStatus st2 = SaleStatus.CLOSED;
		assertTrue(st2 == stc.getStatus());
	}

	@Test
	public void testDiscountRate() throws Exception {
		SaleTransactionClass stc;
		try {
			stc = new SaleTransactionClass(10.0, "CREDIT_CARD", new Time(System.currentTimeMillis()),
					SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1);
			// test getter
			assertTrue(0.1 == stc.getDiscountRate());
			// test setter
			// invalid (<0)
			assertThrows(Exception.class, () -> {
				stc.setDiscountRate(-1);
			});
			// invalid (>1)
			assertThrows(Exception.class, () -> {
				stc.setDiscountRate(1.1);
			});
			// valid
			stc.setDiscountRate(0.2);
			assertTrue(0.2 == stc.getDiscountRate());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testTicketNumber() throws InvalidTransactionIdException { // balanceId = transactionId = ticketId
		try { // cosa succede se c'è già una transazione con quell'id?
			SaleTransactionClass stc = new SaleTransactionClass(10.0, "CREDIT_CARD",
					new Time(System.currentTimeMillis()), SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2,
					new HashMap<>(), 0.1);
			Integer tNumber = 2;
			// test getter
			assertTrue(tNumber == stc.getTicketNumber());
			// invalid setter
			assertThrows(Exception.class, () -> {
				stc.setTicketNumber(null);
			});
			// invalid setter
			assertThrows(Exception.class, () -> {
				stc.setTicketNumber(-1);
			});
			// valid setter
			stc.setTicketNumber(1);
			assertTrue(stc.getTicketNumber() == 1);
		} catch (Exception e) {
			fail();
		}

	}
									///////////////////////////////////////////////////////////////////////
															/* Integration tests */
									///////////////////////////////////////////////////////////////////////
	@Test
	public void testSetLoyaltyCard() throws InvalidTransactionIdException {
		try {
			SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()),
					SaleStatus.STARTED);
			LoyaltyCard lc = new LoyaltyCardClass("1234567890", 1);
			// invalid loyalty card
			assertThrows(Exception.class, () -> {
				stc.setLoyaltyCard(null);
			});
			// valid
			stc.setLoyaltyCard(lc);
			assertTrue(lc.getCardCode() == stc.getLoyaltyCard().getCardCode());
		} catch (Exception e) {
			fail();
		}
	}

	
	@Test
	public void testSetAndGetEntries() throws Exception {
		SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()), SaleStatus.STARTED);
		TicketEntryClass te4 = new TicketEntryClass(
				new ProductTypeClass(1, "testSaleTransactionProduct", "4006381333900", 7.0, null), 5);
		TicketEntryClass te5 = new TicketEntryClass(
				new ProductTypeClass(2, "testSaleTransactionProduct", "4006381333931", 7.0, null), 5);
		List<TicketEntry> teL = new ArrayList<>();
		teL.add(te4);
		teL.add(te5);
		// void (but valid) getters
		assertTrue(stc.getProductsEntries().size() == 0);
		assertTrue(stc.getEntries().size() == 0);
		// invalid argument for setters
		assertThrows(Exception.class, () -> {
			stc.setEntries(null);
		});
		// valid
		stc.setEntries(teL);
		assertTrue(stc.getEntries().size() == 2);
		assertTrue(stc.getProductsEntries().size() == 2);
	}

	@Test
	public void testCheckout() throws Exception {
		SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()), SaleStatus.STARTED);
		TicketEntryClass te4 = new TicketEntryClass(
				new ProductTypeClass(1, "testSaleTransactionProduct", "4006381333900", 7.0, null), 5);
		TicketEntryClass te5 = new TicketEntryClass(
				new ProductTypeClass(2, "testSaleTransactionProduct", "4006381333931", 7.0, null), 5);
		List<TicketEntry> teL = new ArrayList<>();
		teL.add(te4);
		teL.add(te5);
		// try checkout for a transaction that has no entries
		stc.checkout();
		assertEquals(stc.getPrice(), 0.0, 0.0001);
		assertEquals(stc.getStatus(), SaleStatus.CLOSED);
		// try method for a transaction with some entries
		stc.setEntries(teL);
		stc.checkout();
		// test correctness of the price
		double shouldBeThePrice = 0.0;
		for (TicketEntry te : teL) {
			shouldBeThePrice = shouldBeThePrice + te.getAmount() * te.getPricePerUnit() * (1 - te.getDiscountRate());
		}
		assertEquals(stc.getPrice(), shouldBeThePrice, 0.0001);
		assertTrue(stc.getStatus() == SaleStatus.CLOSED);
	}

	@Test
	public void testAddProduct() throws Exception {
		SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()), SaleStatus.STARTED);
		//null productType
		assertFalse(stc.addProduct(null, 10));
		//invalid quantity
		assertFalse(stc.addProduct(new ProductTypeClass(2, "testSaleTransactionProduct", "4006381333931", 7.0, null), -1));
		//valid
		assertTrue(stc.addProduct(new ProductTypeClass(2, "testSaleTransactionProduct", "4006381333931", 7.0, null), 2));
		assertTrue(stc.getEntries().size()==1);
		assertTrue(stc.getPrice()==14);
	}
	
	@Test
	public void testDeleteProduct() throws Exception {
		SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()), SaleStatus.STARTED);
		ProductTypeClass ptc=new ProductTypeClass(1, "testSaleTransactionProduct", "4006381333900", 5.0, null);
		ProductTypeClass ptc2=new ProductTypeClass(2, "testSaleTransactionProduct", "4006381333931", 7.0, null);
		stc.addProduct(ptc, 2);
		
		//invalid quantity
		assertThrows(Exception.class, ()->{
			stc.deleteProduct(ptc, -1);
		});
		//try to delete a product that isn't in the transaction
		assertFalse(stc.deleteProduct(ptc2, 1));
		
		//try to delete one of the two units of the product initially inserted in the transaction
		stc.deleteProduct(ptc, 1);
		assertEquals(stc.getPrice(), 5.0, 0.0001);
		//try to delete the other unit
		stc.deleteProduct(ptc, 1);
		assertEquals(stc.getPrice(), 0.0, 0.0001);
		assertTrue(stc.getEntries().size()==0);
	}

	@Test
	public void testAddProductRFID() throws Exception {
		SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()), SaleStatus.STARTED);
		//Product is null
		assertFalse(stc.addProductRFID(null));

		//RFID of product is null
		//ProductTypeClass pT = new ProductTypeClass(1,"Banana","400638133390",1.0,null);
		//Product p = new Product(null,pT);
		//assertFalse(stc.addProductRFID(p));

		//Product Type of Product is null
		//Product p2 = new Product("400638133390",null);
		//assertFalse(stc.addProductRFID(p2));

		//RFID is already in the map
		Map<String, Product> pRFID = new HashMap<>();
		ProductTypeClass pT = new ProductTypeClass(1,"Banana","400638133390",1.0,null);
		Product p2 = new Product("111111111111",pT);
		pRFID.put("111111111111",p2);
		stc.setProductRFID(pRFID);
		assertFalse(stc.addProductRFID(p2));

		//All right
		pRFID.clear();
		stc.setProductRFID(pRFID);
		assertTrue(stc.addProductRFID(p2));
		assertEquals(1.0,stc.getMoney(),0.00001);

	}

	@Test
	public void testDeleteProductRFID() throws Exception {

		SaleTransactionClass stc = new SaleTransactionClass(new Time(System.currentTimeMillis()), SaleStatus.STARTED);
		//Product is null
		assertFalse(stc.deleteProductRFID(null));

		//RFID of product is null
		//ProductTypeClass pT = new ProductTypeClass(1,"Banana","400638133390",1.0,null);
		//Product p = new Product(null,pT);
		//assertFalse(stc.addProductRFID(p));

		//Product Type of Product is null
		//Product p2 = new Product("400638133390",null);
		//assertFalse(stc.addProductRFID(p2));

		//RFID is not in the map
		Map<String, Product> pRFID = new HashMap<>();
		ProductTypeClass pT = new ProductTypeClass(1,"Banana","400638133390",1.0,null);
		Product p2 = new Product("111111111111",pT);
		assertFalse(stc.deleteProductRFID("111111111111"));

		//All right
		stc.addProductRFID(p2);
		assertTrue(stc.deleteProductRFID("111111111111"));
		assertEquals(0.0,stc.getMoney(),0.00001);
	}
}
