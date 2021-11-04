package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import it.polito.ezshop.data.CustomerClass;
import it.polito.ezshop.data.LoyaltyCard;
import it.polito.ezshop.data.LoyaltyCardClass;
import it.polito.ezshop.exceptions.InvalidCustomerCardException;

public class LoyaltyCardClassTest {
	@Test
	public void testLoyaltyCardClassConstructor() {
		//invalid cardCode
		assertThrows(Exception.class, ()->{
			final LoyaltyCardClass card = new LoyaltyCardClass("4789327489172", 0);});		
				try {
				final LoyaltyCardClass card = new LoyaltyCardClass("0123456789", 0);					
				assertEquals("0123456789", card.getCardCode());		
				}catch(Exception e) {				
					fail();
					}
	
	}
	  @Test
	    public void testSetPoints(){
	       LoyaltyCard card = new LoyaltyCardClass("0123456789", 0);
	        assertTrue(card.setPoints(10));
	        assertFalse(card.setPoints(-10));
	    }

		@Test
		public void testSetCustomerCard() {
			//valid 10 digits
			String code10 = "0123456789";
			assertTrue(LoyaltyCardClass.checkCardCode(code10));
			//invalid 11 
			String code11 = "01234567896";
			assertFalse(LoyaltyCardClass.checkCardCode(code11));
			//invalid 9 
			String code9 = "012345678";
			assertFalse(LoyaltyCardClass.checkCardCode(code9));
			//valid
			assertFalse(LoyaltyCardClass.checkCardCode("24324414352532"));
			assertFalse(LoyaltyCardClass.checkCardCode("22"));
			assertTrue(LoyaltyCardClass.checkCardCode("2223334445"));
			assertTrue(LoyaltyCardClass.checkCardCode(""));
			assertTrue(LoyaltyCardClass.checkCardCode(null));

		}
	  @Test
		public void testUpdatePoints() throws InvalidCustomerCardException {
			LoyaltyCardClass card = new LoyaltyCardClass("0123456789", 0);		
			// initial points should be 0
			assertEquals(new Integer(0), card.getPoints());
			// try update with negative
			assertFalse(card.updatePoints(-1));
			assertEquals(new Integer(0), card.getPoints());
			assertTrue(card.updatePoints(10));
			assertTrue(card.updatePoints(10));
			assertEquals(new Integer(20), card.getPoints());
		}
		
	  @Test
	  public void testCreateCardCode(){
		  int i=11;
			assertEquals("", LoyaltyCardClass.createCardCode(i));
			int i2=9;
			assertEquals("", LoyaltyCardClass.createCardCode(i2));
	  }
	  
	  @Test
		public void testWhiteBox() {
			final LoyaltyCardClass card = new LoyaltyCardClass("0123456789", 0);
			assertThrows(RuntimeException.class, () -> {card.setCardCode(null);});
			assertThrows(RuntimeException.class, () -> {card.setCardCode("12");});
			assertThrows(RuntimeException.class, () -> {card.setCardCode("1897386461946109");});			
			
			int i=11;
			assertEquals("", LoyaltyCardClass.createCardCode(i));
			int i2=9;
			assertEquals("", LoyaltyCardClass.createCardCode(i2));
			
			int i3=10;
			String str = LoyaltyCardClass.createCardCode(i3);		
			LoyaltyCardClass c =new LoyaltyCardClass(str, 0);
			c.setCardCode(str);
			assertEquals(str,c.getCardCode() );

	 
	  }

	
	

}
