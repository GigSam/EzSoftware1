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
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidLocationException;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;
import it.polito.ezshop.exceptions.InvalidProductIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class ProductAPITest {
	private final EZShop ezshop = new EZShop();
	private String username = "testUserProductApiEZShop";
	private String password = "password";
	private int createdUserId = -1;
	
	@Before
	public void init() throws Exception {
		User u = null;
		if((u=ezshop.login(username, password))==null) {
			createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
		}else if(u.getRole().equals(RoleEnum.Cashier.name())) {
			do {
			username+="123";
			createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
			}while(createdUserId<0);
		}
		// delete test products
		ezshop.login(username, password);
		ProductType pt = null;
		if((pt = ezshop.getProductTypeByBarCode("400638133390"))!=null){
			ezshop.deleteProductType(pt.getId());
		}
		if((pt = ezshop.getProductTypeByBarCode("4006381333931"))!=null){
			ezshop.deleteProductType(pt.getId());
		}
		ezshop.logout();
	}
	@After
	public void after() throws Exception {
		if(createdUserId > 0) {
			ezshop.login(username, password);
			ezshop.deleteUser(createdUserId);
		}
	}
	@Test
	public void testCreateProductType() throws Exception{
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.createProductType("apple", "400638133390", 10.0, null);});		
		// login
		ezshop.login(username, password);
		// description
		// null
		assertThrows(InvalidProductDescriptionException.class, ()->{ezshop.createProductType(null, "400638133390", 10.0, null);});
		// empty
		assertThrows(InvalidProductDescriptionException.class, ()->{ezshop.createProductType("", "400638133390", 10.0, null);});
		// barcode
		// null
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.createProductType("apple", null, 10.0, null);});
		// invalid
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.createProductType("apple", "", 10.0, null);});
		// unitPrice
		// invalid
		assertThrows(InvalidPricePerUnitException.class, ()->{ezshop.createProductType("apple", "400638133390", -10.0, null);});
		// db error
		Connect.closeConnection();
		assertEquals(Integer.valueOf(-1), ezshop.createProductType("apple", "400638133390", 10.0, "apple"));
		Connect.openConnection();
		// valid
		int id =-1;
		try{
			if((id=ezshop.createProductType("apple", "400638133390", 10.0, "apple"))<=0)
				fail();
		}catch(Exception e) {
			fail();
		}
		// double add
		assertEquals(new Integer(-1), ezshop.createProductType("apple", "400638133390", 10.0, "apple"));
		
		// remove
		ezshop.deleteProductType(id);
	}
	
	@Test
	public void testDeleteProductType() throws Exception {
		// final EZShop ezshop = new EZShop();
		// login
		ezshop.login(username, password);
		int id[] = {-1};
		try{
			if((id[0]=ezshop.createProductType("apple", "400638133390", 10.0, "apple"))<=0)
				fail();
		}catch(Exception e) {
			fail();
		}
		ezshop.logout();
		
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.deleteProductType(id[0]);});	
		// login
		ezshop.login(username, password);
		// invalid id
		assertThrows(InvalidProductIdException.class, ()->{ezshop.deleteProductType(0);});	// db error
		Connect.closeConnection();
		assertFalse(ezshop.deleteProductType(id[0]));
		Connect.openConnection();
		// valid
		assertEquals(true, ezshop.deleteProductType(id[0]));	
		// double remove
		assertFalse(ezshop.deleteProductType(id[0]));	
	}
	@Test
	public void testGetAllProductTypes() throws InvalidUsernameException, InvalidPasswordException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException, InvalidProductIdException {
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.getAllProductTypes();});	
		// login
		ezshop.login(username, password);
		// valid
		int num = -1;
		try {
			List<ProductType> prods = ezshop.getAllProductTypes();
			num = prods.size();
		}catch(Exception e) {fail();}
		// add and see if the prod is added
		final int id = ezshop.createProductType("apple", "400638133390", 10.0, "apple");
		boolean present=false;
		try {
			List<ProductType> prods = ezshop.getAllProductTypes();
			assertEquals(num+1, prods.size());
			// check if contains
			for(ProductType pt:prods) {
				if(pt.getBarCode().equals("400638133390"))
					present = true;
			}
			if(!present)
				fail();
		}catch(Exception e) {fail();}
		// remove and see if really removed
		ezshop.deleteProductType(id);
		present = false;
		try {
			List<ProductType> prods = ezshop.getAllProductTypes();
			assertEquals(num, prods.size());
			// check if contains
			for(ProductType pt:prods) {
				if(pt.getBarCode().equals("400638133390"))
					present = true;
			}
			if(present)
				fail();
		}catch(Exception e) {fail();}
	}
	@Test
	public void testGetProductTypeByBarCode() throws InvalidUsernameException, InvalidPasswordException, InvalidProductCodeException, UnauthorizedException, InvalidProductIdException {
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.getProductTypeByBarCode("4006381333900");});	
		// login
		ezshop.login(username, password);
		// invalid barcode
		// null
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.getProductTypeByBarCode(null);});	
		// empty
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.getProductTypeByBarCode("");});	
		// not valid
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.getProductTypeByBarCode("4006381333903");});
		// valid but not present
		assertNull(ezshop.getProductTypeByBarCode("400638133390"));
		// present
		int id=-1;
		try {
			id = ezshop.createProductType("apple", "400638133390", 10.0, "apple");
			ProductType pt = ezshop.getProductTypeByBarCode("400638133390");
			// cehck if same prod inserted
			assertEquals(new Integer(id), pt.getId());
			assertEquals("apple", pt.getProductDescription());
			assertEquals("400638133390", pt.getBarCode());
			assertEquals(10, pt.getPricePerUnit(), 1e-6);
		}catch(Exception e) {fail();}
		// remove product
		ezshop.deleteProductType(id);
	}
	@Test
	public void testGetProductTypesByDescription() throws InvalidUsernameException, InvalidPasswordException {
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.getProductTypesByDescription("banana");});	
		// login
		ezshop.login(username, password);
		try {
			int id = ezshop.createProductType("apple", "400638133390", 10.0, "apple");
			// list for comparison
			List<ProductType> allProd = ezshop.getAllProductTypes();
			// null string
			List<ProductType> selProd = ezshop.getProductTypesByDescription(null);
			if(!allProd.containsAll(selProd))
				fail();
			// empty string
			selProd = ezshop.getProductTypesByDescription("");
			if(!allProd.containsAll(selProd))
				fail();
			// not present
			selProd = ezshop.getProductTypesByDescription("cdfg");
			if(selProd == null || selProd.size()>=1)
				fail();
			// only one
			selProd = ezshop.getProductTypesByDescription("pl");	// should return apple only
			if(selProd.size() != 1 || !selProd.get(0).getBarCode().equals("400638133390"))
				fail();
			// clean
			ezshop.deleteProductType(id);
		}catch(Exception e) {fail();}
	}
	@Test
	public void testUpdateProduct() throws Exception {
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.updateProduct(1, "banana", "4006381333900", 10.0, null);});	
		// login
		ezshop.login(username, password);
		// create test prod
		final int id = ezshop.createProductType("apple", "400638133390", 10.0, "apple");
		ProductType pt = ezshop.getProductTypeByBarCode("400638133390");
		ezshop.updatePosition(id, "1-test-1");
		ezshop.updateQuantity(id, 10);
		// invalid id
		assertThrows(InvalidProductIdException.class, ()->{ezshop.updateProduct(0, "apple", "400638133390", 10.0, "apple");});	
		// not present id
		assertFalse(ezshop.updateProduct(Integer.MAX_VALUE, "apple", "400638133390", 10.0, "apple"));	
		// invalid description
		// null
		assertThrows(InvalidProductDescriptionException.class, ()->{ezshop.updateProduct(id, null, "400638133390", 10.0, "apple");});	
		// empty
		assertThrows(InvalidProductDescriptionException.class, ()->{ezshop.updateProduct(id, "", "400638133390", 10.0, "apple");});	
		// invalid barcode
		// null
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.updateProduct(id, "apple", null, 10.0, "apple");});	
		//empty
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.updateProduct(id, "apple", "", 10.0, "apple");});	
		//invalid
		assertThrows(InvalidProductCodeException.class, ()->{ezshop.updateProduct(id, "apple", "400638133399", 10.0, "apple");});	
		//already present
		boolean created = false;
		int id2 = -1;
		if(ezshop.getProductTypeByBarCode("4006381333900") == null) {
			id2 = ezshop.createProductType("test", "4006381333900", 1.0, "");
			created = true;
		}
		assertFalse(ezshop.updateProduct(id, "apple", "4006381333900", 10.0, "apple"));	
		if(created)
			ezshop.deleteProductType(id2);
		// invalid price
		assertThrows(InvalidPricePerUnitException.class, ()->{ezshop.updateProduct(id, "apple", "400638133390", 0.0, "apple");});	
		// db error
		Connect.closeConnection();
		assertFalse( ezshop.updateProduct(id, "apple2", "400638133390", 20.0, "apple2"));
		Connect.openConnection();
		// valid
		try {
			if(!ezshop.updateProduct(id, "apple2", "400638133390", 20.0, "apple2"))
				fail();
			pt = ezshop.getProductTypeByBarCode("400638133390");
			assertEquals(new Integer(id), pt.getId());
			assertEquals("apple2", pt.getProductDescription());
			assertEquals(20.0, pt.getPricePerUnit(), 1e-6);
			assertEquals("apple2", pt.getNote());
			assertEquals(new Integer(10), pt.getQuantity());
			assertEquals("1-test-1", pt.getLocation());
			// clean
			ezshop.deleteProductType(id);
		}catch(Exception e) {fail();}
		
	}
	
	@Test
	public void testUpdateQuantity() throws Exception {
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.updateQuantity(1, 10);});	
		// login
		ezshop.login(username, password);
		// create test products
		final int id = ezshop.createProductType("apple", "400638133390", 10.0, "apple");
		ProductType pt = ezshop.getProductTypeByBarCode("400638133390");
		// invalid id
		assertThrows(InvalidProductIdException.class, ()->{ezshop.updateQuantity(0, 100);});	// invalid
		assertFalse(ezshop.updateQuantity(Integer.MAX_VALUE, 1));	// not present
		// invalid location
		assertFalse(ezshop.updateQuantity(id, 1));
		// set position
		ezshop.updatePosition(id, "1-test-1");
		pt = ezshop.getProductTypeByBarCode("400638133390");
		assertEquals("1-test-1", pt.getLocation());
		// invalid quantity
		assertFalse(ezshop.updateQuantity(id, -11));	
		// db error
		Connect.closeConnection();
		assertFalse( ezshop.updateQuantity(id, 10));
		Connect.openConnection();
		// valid
		assertTrue(ezshop.updateQuantity(id, 10));
		pt = ezshop.getProductTypeByBarCode("400638133390");
		assertEquals(new Integer(10), pt.getQuantity());
		assertTrue(ezshop.updateQuantity(id, -10));
		pt = ezshop.getProductTypeByBarCode("400638133390");
		assertEquals(new Integer(0), pt.getQuantity());
		
		// clean
		ezshop.deleteProductType(id);
	}
	
	@Test
	public void testUpdateLocation() throws Exception {
		// final EZShop ezshop = new EZShop();
		// before login
		assertThrows(UnauthorizedException.class, ()->{ezshop.updatePosition(1, "1-test-1");});	
		// login
		ezshop.login(username, password);
		// create test products
		final int id = ezshop.createProductType("apple", "400638133390", 10.0, "apple");
		ProductType pt = ezshop.getProductTypeByBarCode("400638133390");
		pt.setQuantity(10);
		String startPos = pt.getLocation();
		final int id2 = ezshop.createProductType("ananas", "4006381333931", 10.0, "ananas");
		ProductType pt2 = ezshop.getProductTypeByBarCode("4006381333931");
		pt.setQuantity(10);
		// invalid id
		assertThrows(InvalidProductIdException.class, ()->{ezshop.updatePosition(0, "1-atest-1");});	// invalid
		assertFalse(ezshop.updatePosition(Integer.MAX_VALUE, "1-atest-1"));	// not present
		// invalid location
		assertThrows(InvalidLocationException.class, ()->{ezshop.updatePosition(id, "1-1");});
		pt = ezshop.getProductTypeByBarCode("400638133390");
		assertEquals(startPos, pt.getLocation());
		// already present
		ezshop.updatePosition(id2, "2-btest-2");
		pt2 = ezshop.getProductTypeByBarCode("4006381333931");
		assertEquals("2-btest-2", pt2.getLocation());
		assertFalse(ezshop.updatePosition(id, "2-btest-2"));
		pt = ezshop.getProductTypeByBarCode("400638133390");
		assertEquals(startPos, pt.getLocation());		
		// db error
		Connect.closeConnection();
		assertFalse( ezshop.updatePosition(id, "3-ctest-3"));
		Connect.openConnection();
		// valid
		try {
			ezshop.updatePosition(id, "3-ctest-3");
			pt = ezshop.getProductTypeByBarCode("400638133390");
			assertEquals("3-ctest-3", pt.getLocation());
			// reset with null
			ezshop.updatePosition(id, null);
			pt = ezshop.getProductTypeByBarCode("400638133390");
			assertEquals("", pt.getLocation());
			// reset with empty
			ezshop.updatePosition(id, "3-ctest-3");
			pt = ezshop.getProductTypeByBarCode("400638133390");
			assertEquals("3-ctest-3", pt.getLocation());
			ezshop.updatePosition(id, "");
			pt = ezshop.getProductTypeByBarCode("400638133390");
			assertEquals("", pt.getLocation());
		}catch(Exception e) {fail();}
		// clean
		ezshop.deleteProductType(id);
		ezshop.deleteProductType(id2);
	}
}
