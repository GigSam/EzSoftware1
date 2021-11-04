package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.Test;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.ProductTypeClass;
import it.polito.ezshop.data.TicketEntryClass;

//private ProductType productType;
//private int amount;
//private double discountRate;
public class TicketEntryTest {
	@Test
	public void testTicketEntryConstructor() {
		//invalid productType
		assertThrows(Exception.class, ()->{
			TicketEntryClass tec=new TicketEntryClass(null, 2, 0.0);
		});
		//invalid amount
		assertThrows(Exception.class, ()->{
			TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), -1, 0.0);
		});
		//invalid discountRate (<0)
		assertThrows(Exception.class, ()->{
			TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), 3, -2);
		});
		//invalid discountRate (>1)
		assertThrows(Exception.class, ()->{
			TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), 3, 1.2);
		});
		
		//valid
		try {
			ProductTypeClass p=new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes");
			TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), 5, 0.2);
			assertEquals(tec.getAmount(), 5);
			assertEquals(tec.getDiscountRate(), 0.2, 0.0001);
			assertEquals(tec.getProductType(), p);
		} catch (Exception e) {
			fail();
		}
		
	}
	@Test
	public void testSetAmount() {
		try {
			TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), 5, 0.2);
			//invalid
			assertThrows(Exception.class, ()->{
				tec.setAmount(-1);
			});
			//invalid
			assertThrows(Exception.class, ()->{
				tec.setAmount(0);	// la ticket entry può avere amount = 0?
			});
			//valid
			tec.setAmount(1);
			assertEquals(1, tec.getAmount());
		} catch (Exception e) {
			fail();
		}
		
	}
	@Test
	public void testSetDiscountRate() {
		try {
			TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), 5, 0.2);
			//invalid
			assertThrows(Exception.class, ()->{
				tec.setDiscountRate(-1);
			});
			//invalid
			assertThrows(Exception.class, ()->{
				tec.setDiscountRate(1.1);
			});
			//valid
			tec.setDiscountRate(0.3);
			assertEquals(tec.getDiscountRate(), 0.3, 0.0001);
		} catch (Exception e) {
			fail();
		}
	}
	
	/////////////////// INTEGRATION ///////////////////////
	@Test
	public void testSetBarcode() throws Exception{
		TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "null", "4006381333900", 2.0, "notes"), 5, 0.2);
		// invalid barcode
		assertThrows(Exception.class, ()->tec.setBarCode("1234"));
		assertEquals("4006381333900", tec.getBarCode());
		// valid
		tec.setBarCode("400638133390");
		// check
		assertEquals("400638133390", tec.getBarCode());
	}
	
	@Test
	public void testSetProductDescription() throws Exception{
		TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "desc", "4006381333900", 2.0, "notes"), 5, 0.2);
		// invalid barcode
		assertThrows(Exception.class, ()->tec.setProductDescription(null));
		assertEquals("desc", tec.getProductDescription());
		// valid
		tec.setProductDescription("desc2");
		assertEquals("desc2", tec.getProductDescription());
	}
	
	@Test
	public void testSetPricePerUnit() throws Exception{
		TicketEntryClass tec=new TicketEntryClass(new ProductTypeClass(1, "desc", "4006381333900", 2.0, "notes"), 5, 0.2);
		// invalid barcode
		assertThrows(Exception.class, ()->tec.setPricePerUnit(-1));
		assertEquals(2.0, tec.getPricePerUnit(), 1e-6);
		// valid
		tec.setPricePerUnit(5.0);
		assertEquals(5.0, tec.getPricePerUnit(), 1e-6);
	}
}
