package it.polito.ezshop;

import static org.junit.Assert.*;

import org.junit.Test;

import it.polito.ezshop.data.Position;
import it.polito.ezshop.data.ProductTypeClass;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;
import it.polito.ezshop.exceptions.InvalidProductIdException;

public class ProductTypeTest {
	@Test
	public void testValidateBarCode() {
		// ivalid 13
		String str = "4006381333939";
		assertFalse(ProductTypeClass.validateBarCode(str));
		// valid 13
		str = "4006381333931";
		assertTrue(ProductTypeClass.validateBarCode(str));
		// null
		assertFalse(ProductTypeClass.validateBarCode(null));
		// empty
		assertFalse(ProductTypeClass.validateBarCode(""));
		// not digit
		assertFalse(ProductTypeClass.validateBarCode("asdasdasdsdas"));
		// valid 12
		str = "400638133390";
		assertTrue(ProductTypeClass.validateBarCode(str));
		// invalid 12
		str = "400638133396";
		assertFalse(ProductTypeClass.validateBarCode(str));
		// valid 14
		str = "4006381333900";
		assertTrue(ProductTypeClass.validateBarCode(str));
		// invalid 14
		str = "4006381333905";
		assertFalse(ProductTypeClass.validateBarCode(str));
		// with spaces
		str = " 400 638 133 3900 ";
		assertTrue(ProductTypeClass.validateBarCode(str));
		str = " 400 638 133 3901 ";
		assertFalse(ProductTypeClass.validateBarCode(str));
		assertFalse(ProductTypeClass.validateBarCode("             "));
		// invalid 11
		str = "4006381333";
		assertFalse(ProductTypeClass.validateBarCode(str));
		// invalid 15
		str = "40063813339054";
		assertFalse(ProductTypeClass.validateBarCode(str));
	}
	@Test
	public void testProductTypeConstructor() {
		// invalid description
		assertThrows(InvalidProductDescriptionException.class, ()->{
			ProductTypeClass pt = new ProductTypeClass(1, null, "4006381333900", 1.0, null);});
		// invalid id
		assertThrows(Exception.class, ()->{
			ProductTypeClass pt = new ProductTypeClass(0, "null", "4006381333900", 1.0, null);});
		// invalid product code
		assertThrows(InvalidProductCodeException.class, ()->{
			ProductTypeClass pt = new ProductTypeClass(1, "null", "40063813339", 1.0, null);});
		
		// invalid price
		assertThrows(InvalidPricePerUnitException.class, ()->{
			ProductTypeClass pt = new ProductTypeClass(1, "null", "4006381333900", -0.0, null);});
		// valid
		try {
			ProductTypeClass pt = new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
			assertEquals("4006381333900", pt.getBarCode());
			assertEquals(new Integer(1), pt.getId());
			assertEquals("notes", pt.getNote());
			assertEquals("null", pt.getProductDescription());
			assertEquals(2.0, pt.getPricePerUnit(), 0.0001);
			// test on set note call
			pt.setNote("prova");
		}catch(Exception e) {fail();}
	}
	
	@Test
	public void testUpdateQuantity() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
		ProductTypeClass pt = new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
		// initial qty should be 0
		assertEquals(new Integer(0), pt.getQuantity());
		// try update with negative
		assertFalse(pt.updateQuantity(-1));
		assertEquals(new Integer(0), pt.getQuantity());
		assertTrue(pt.updateQuantity(2));
		assertTrue(pt.updateQuantity(0));
		assertEquals(new Integer(2), pt.getQuantity());
	}
	
	@Test
	public void testDescription() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
		final ProductTypeClass pt = new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
		
		assertEquals("null", pt.getProductDescription());
		// invalid new description
		assertThrows(Exception.class, ()->{
			pt.setProductDescription("");});
		
		assertEquals("null", pt.getProductDescription());
		assertThrows(Exception.class, ()->{
			pt.setProductDescription(null);});
		assertEquals("null", pt.getProductDescription());
		// valid description
		try {
			pt.setProductDescription("prova");			
		}catch(Exception e) {fail();}
		assertEquals("prova", pt.getProductDescription());		
	}
	
	@Test
	public void testProductId() {
		ProductTypeClass pt=null;
		try {
			pt = new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
		} catch (Exception e1) {fail();}
		// neagtive 
		try {
			pt.setId(-1);
			fail();
		}catch(Exception e) {}
		// null
		try {
			pt.setId(null);
			fail();
		}catch(Exception e) {}
		
		// valid
		try {
			pt.setId(1231);
			assertEquals(new Integer(1231), pt.getId());
		}catch(Exception e) {fail();}
	}
	
	@Test
	public void testSetPosition() {
		ProductTypeClass pt=null;
		try {
			pt = new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
		} catch (Exception e1) {fail();}

		pt.setLocation("");
		assertEquals("", pt.getLocation());

		pt.setLocation((String)null);
		assertEquals("", pt.getLocation());
		//valid
		pt.setLocation("1-a-1");
		assertEquals("1-a-1", pt.getLocation());
		
		// with object position
		pt.setLocation((Position)null);
		assertEquals("", pt.getLocation());
		Position p = new Position("1-a-2");
		pt.setLocation(p);
		assertEquals("1-a-2", pt.getLocation());
	}
	
	@Test
	public void testWhiteBox() throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
		final ProductTypeClass pt=new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
		
		// setQuantity
		assertThrows(RuntimeException.class, ()->{pt.setQuantity(null);});
		assertThrows(RuntimeException.class, ()->{pt.setQuantity(-1);});
		try {
			pt.setQuantity(2);
			assertEquals(new Integer(2), pt.getQuantity());
		}catch(Exception e) {
			fail();
		}
		// setBarCode
		assertThrows(RuntimeException.class, ()->{pt.setBarCode(null);});
		assertThrows(RuntimeException.class, ()->{pt.setBarCode("  dfs");});
		try {
			pt.setBarCode("400638133390");
			assertEquals("400638133390", pt.getBarCode());
		}catch(Exception e) {
			fail();
		}
		// setPricePerUnit
		assertThrows(RuntimeException.class, ()->{pt.setPricePerUnit(null);});
		assertThrows(RuntimeException.class, ()->{pt.setPricePerUnit(-0.0);});
		try {
			pt.setPricePerUnit(2.0);
			assertEquals(2.0, pt.getPricePerUnit(), 1e-6);
		}catch(Exception e) {
			fail();
		}
		
		// getPosition
		Position p = new Position("3-c-3");
		pt.setLocation(p);
		assertEquals(p, pt.getPosition());
		//equals
		assertFalse(pt.equals(null));
		assertFalse(pt.equals("4006381333900"));
		assertFalse(pt.equals(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes")));
		assertFalse(pt.equals(new ProductTypeClass(2, "null", "4006381333900", 2.0, "notes")));
		assertTrue(pt.equals(pt));
		// copy
		try {
		ProductTypeClass pt2 = new ProductTypeClass(pt);
		assertTrue(pt.equals(pt2));
		}catch(Exception e) {fail();}
		
	}
}
