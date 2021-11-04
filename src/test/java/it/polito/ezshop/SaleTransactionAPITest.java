package it.polito.ezshop;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.data.Connect;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.RoleEnum;
import it.polito.ezshop.data.SaleStatus;
import it.polito.ezshop.data.SaleTransactionClass;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.data.TicketEntryClass;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class SaleTransactionAPITest {

	private final EZShop ezshop = new EZShop();
	private String username = "testSaleTransactionApiEZShop";
	private String password = "password";
	private int createdUserId = -1;
	private int createdCashier = -1;
	private String usernameC = "testSaleTransactionApiUserCashier";
	private ProductType pt1 = null;
	private int newProdId1 = -1;
	private int newProdId2 = -1;
	private ProductType pt2 = null;
	private int id = -1;

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
		// create test products and update their quantity
		ezshop.login(username, password);

		if ((pt1 = ezshop.getProductTypeByBarCode("4006381333900")) == null) {

			newProdId1 = ezshop.createProductType("testSaleTransactionProduct", "4006381333900", 3.5, null);
		}

		ezshop.updatePosition(ezshop.getProductTypeByBarCode("4006381333900").getId(), "3-ctest-3");
		ezshop.updateQuantity(newProdId1 > 0 ? newProdId1 : pt1.getId(), 5);

		if ((pt2 = ezshop.getProductTypeByBarCode("4006381333931")) == null) {

			newProdId2 = ezshop.createProductType("testSaleTransactionProduct", "4006381333931", 7.0, null);
		}
		ezshop.updatePosition(ezshop.getProductTypeByBarCode("4006381333931").getId(), "3-ctest-4");
		ezshop.updateQuantity(newProdId2 > 0 ? newProdId2 : pt2.getId(), 10);

		ezshop.recordBalanceUpdate(100);
		int orderId = ezshop.payOrderFor("4006381333900", 10, 1);
		ezshop.recordOrderArrivalRFID(orderId, "000000001000");
		
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

			// delete user

			ezshop.deleteUser(createdUserId);
			if (createdCashier > 0)
				ezshop.deleteUser(createdCashier);
		}
		// remove sale transaction
		if (id > 0) {
			ezshop.getAccountBook().removeSaleTransaction(id);
		}
		ezshop.reset();
	}

	@Test
	public void testStartSaleTransaction() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.startSaleTransaction();
		});
		// login Admin
		ezshop.login(username, password);
		id = ezshop.startSaleTransaction();
		// test new sale transaction id
		assertTrue(id > 0);
		// test correct upload of the transaction in the account book
		assertTrue(ezshop.getAccountBook().getSaleTransaction(id) != null);
		ezshop.getAccountBook().removeSaleTransaction(id);
		id = -1;
	}

	@Test
	public void testAddProductToSale() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.addProductToSale(1, "4006381333900", 2);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.addProductToSale(null, "4006381333900", 2);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.addProductToSale(-2, "4006381333900", 2);
		});
		// not present transactionId
		assertFalse(ezshop.addProductToSale(Integer.MAX_VALUE, "4006381333900", 2));
		// null productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.addProductToSale(id, null, 2);
		});
		// invalid productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.addProductToSale(id, "", 2);
		});
		// not present product code
		assertFalse(ezshop.addProductToSale(id, "400638133390", 2));
		// invalid quantity
		assertThrows(InvalidQuantityException.class, () -> {
			ezshop.addProductToSale(id, "4006381333900", -2);
		});

		int q = ezshop.getProductTypeByBarCode("4006381333931").getQuantity();
		// valid
		assertTrue(ezshop.addProductToSale(id, "4006381333931", 2));
		// test correctly updated quantity
		assertTrue(q == ezshop.getProductTypeByBarCode("4006381333931").getQuantity() + 2);

		ezshop.deleteProductFromSale(id, "4006381333931", 2);

		ezshop.logout();

	}

	@Test
	public void testAddProductToSaleRFID() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.addProductToSaleRFID(1, "000000001000");
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.addProductToSaleRFID(null, "000000001000");
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.addProductToSaleRFID(-1, "000000001000");
		});
		// not present transactionId
		assertFalse(ezshop.addProductToSaleRFID(Integer.MAX_VALUE, "000000001000"));
		// null RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.addProductToSaleRFID(id, null);
		});
		// invalid RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.addProductToSaleRFID(id, "1234");
		});

		// valid
		assertTrue(ezshop.addProductToSaleRFID(id, "000000001000"));

		ezshop.deleteProductFromSaleRFID(id, "000000001000");

		ezshop.logout();

	}

	@Test
	public void testDeleteProductFromSale() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.deleteProductFromSale(1, "4006381333900", 2);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		assertTrue(ezshop.addProductToSale(id, "4006381333900", 2));

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteProductFromSale(null, "4006381333900", 2);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteProductFromSale(-2, "4006381333900", 2);
		});
		// not present transactionId
		assertFalse(ezshop.deleteProductFromSale(Integer.MAX_VALUE, "4006381333900", 2));
		// null productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.deleteProductFromSale(id, null, 2);
		});
		// invalid productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.deleteProductFromSale(id, "", 2);
		});
		// not present product code
		assertFalse(ezshop.deleteProductFromSale(id, "400638133390", 2));
		// invalid quantity
		assertThrows(InvalidQuantityException.class, () -> {
			ezshop.deleteProductFromSale(id, "4006381333900", -2);
		});

		int q = ezshop.getProductTypeByBarCode("4006381333900").getQuantity();
		// valid
		assertTrue(ezshop.deleteProductFromSale(id, "4006381333900", 2));
		// test correctly updated quantity
		assertTrue(q + 2 == ezshop.getProductTypeByBarCode("4006381333900").getQuantity());

		ezshop.deleteProductFromSale(id, "4006381333900", 2);
		ezshop.logout();
	}

	@Test
	public void testDeleteProductFromSaleRFID() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.deleteProductFromSaleRFID(1, "000000001000");
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		assertTrue(ezshop.addProductToSale(id, "4006381333900", 2));

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteProductFromSaleRFID(null, "000000001000");
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteProductFromSaleRFID(-2, "000000001000");
		});
		// not present transactionId
		assertFalse(ezshop.deleteProductFromSaleRFID(Integer.MAX_VALUE, "000000001000"));
		// null RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.deleteProductFromSaleRFID(id, null);
		});
		// invalid RFID
		assertThrows(InvalidRFIDException.class, () -> {
			ezshop.deleteProductFromSaleRFID(id, "123");
		});
		// not present RFID
		assertFalse(ezshop.deleteProductFromSaleRFID(id, "123456789000"));

		int q = ezshop.getProductTypeByBarCode("4006381333900").getQuantity();
		// valid
		assertTrue(ezshop.addProductToSaleRFID(id, "000000001000"));
		assertTrue(ezshop.deleteProductFromSaleRFID(id, "000000001000"));
		// test correctly updated quantity          TODO
		assertTrue(q == ezshop.getProductTypeByBarCode("4006381333900").getQuantity());

		ezshop.logout();
	}

	@Test
	public void testGetSaleTransaction() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.getSaleTransaction(1);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// close sale transaction
		SaleTransactionClass stc = (SaleTransactionClass) ezshop.getAccountBook().getSaleTransaction(id);
		stc.setStatus(SaleStatus.CLOSED);

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.getSaleTransaction(null);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.getSaleTransaction(-1);
		});
		// valid
		SaleTransactionClass st = (SaleTransactionClass) ezshop.getSaleTransaction(id);
		assertTrue(st.getStatus() == SaleStatus.CLOSED);

		ezshop.logout();
	}

	@Test
	public void testApplyDiscountRateToProduct() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.applyDiscountRateToProduct(1, "4006381333900", 0.2);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		ezshop.addProductToSale(id, "4006381333900", 2);

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.applyDiscountRateToProduct(null, "4006381333900", 0.2);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.applyDiscountRateToProduct(-1, "4006381333900", 0.2);
		});
		// not present transactionId
		assertFalse(ezshop.applyDiscountRateToProduct(Integer.MAX_VALUE, "4006381333900", 0.5));
		// null productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.applyDiscountRateToProduct(id, null, 0.2);
		});
		// invalid productCode
		assertThrows(InvalidProductCodeException.class, () -> {
			ezshop.applyDiscountRateToProduct(id, "", 0.2);
		});
		// not present product code
		assertFalse(ezshop.applyDiscountRateToProduct(id, "400638133390", 0.2));
		// invalid discountRate
		assertThrows(InvalidDiscountRateException.class, () -> {
			ezshop.applyDiscountRateToProduct(id, "4006381333900", -0.3);
		});
		// invalid discountRate
		assertThrows(InvalidDiscountRateException.class, () -> {
			ezshop.applyDiscountRateToProduct(id, "4006381333900", 1.3);
		});

		// valid
		assertTrue(ezshop.applyDiscountRateToProduct(id, "4006381333900", 0.2));

		ezshop.endSaleTransaction(id);

		// check if product discount is updated
		List<TicketEntry> tec = ezshop.getSaleTransaction(id).getEntries();
		double disc = 0.0;
		for (TicketEntry t : tec) {
			if (t.getBarCode().equals("4006381333900")) {
				disc = t.getDiscountRate();
				break;
			}
		}
		assertEquals(disc, 0.2, 0.0001);

		ezshop.deleteProductFromSale(id, "4006381333900", 2);
		ezshop.logout();
	}

	@Test
	public void testApplyDiscountRateToSale() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.applyDiscountRateToSale(1, 0.2);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		ezshop.addProductToSale(id, "4006381333900", 2);
		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.applyDiscountRateToSale(null, 0.2);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.applyDiscountRateToSale(-1, 0.2);
		});
		// invalid discountRate
		assertThrows(InvalidDiscountRateException.class, () -> {
			ezshop.applyDiscountRateToSale(id, -0.3);
		});
		// invalid discountRate
		assertThrows(InvalidDiscountRateException.class, () -> {
			ezshop.applyDiscountRateToSale(id, 1.3);
		});

		// valid
		ezshop.applyDiscountRateToSale(id, 0.2);
		// check if discount for the sale is updated
		assertEquals(ezshop.getAccountBook().getSaleTransaction(id).getDiscountRate(), 0.2, 0.0001);

		ezshop.deleteProductFromSale(id, "4006381333900", 2);
		ezshop.logout();
	}

	@Test
	public void testComputePointsForSale() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.computePointsForSale(1);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		assertTrue(ezshop.addProductToSale(id, "4006381333900", 3));

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.computePointsForSale(null);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.computePointsForSale(-1);
		});

		// valid
		int p = ezshop.computePointsForSale(id);
		assertEquals(1, p, 0.0001);

		assertTrue(ezshop.addProductToSale(id, "4006381333931", 3));

		p = ezshop.computePointsForSale(id);
		assertEquals(3, p, 0.0001);

		ezshop.deleteProductFromSale(id, "4006381333900", 2);
		ezshop.logout();
	}

	@Test
	public void testEndSaleTransaction() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.endSaleTransaction(1);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		ezshop.addProductToSale(id, "4006381333900", 2);

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.endSaleTransaction(null);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.endSaleTransaction(-1);
		});

		// valid
		assertTrue(ezshop.endSaleTransaction(id));

		ezshop.getAccountBook().removeSaleTransaction(id);
		id = -1;
		ezshop.logout();
	}

	@Test
	public void testDeleteSaleTransaction() throws Exception {
		// before login
		assertThrows(UnauthorizedException.class, () -> {
			ezshop.deleteSaleTransaction(1);
			ezshop.deleteSaleTransaction(-1);
		});
		// login Admin
		ezshop.login(username, password);
		// create new transaction
		id = ezshop.startSaleTransaction();
		// add product to the new sale transaction
		ezshop.addProductToSale(id, "4006381333900", 2);
		ezshop.endSaleTransaction(id);

		// null transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteSaleTransaction(null);
		});
		// invalid transactionId
		assertThrows(InvalidTransactionIdException.class, () -> {
			ezshop.deleteSaleTransaction(-1);
		});
		// valid
		assertTrue(ezshop.deleteSaleTransaction(id));
		id = -1;
		ezshop.logout();
	}

}