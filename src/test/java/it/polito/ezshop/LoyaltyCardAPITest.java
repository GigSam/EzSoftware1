package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.data.AccountBookClass;
import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.CustomerClass;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.LoyaltyCard;
import it.polito.ezshop.data.LoyaltyCardClass;
import it.polito.ezshop.data.RoleEnum;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class LoyaltyCardAPITest {
	private final EZShop ezshop = new EZShop();
	private String username = "testUserCustomerApiEZShop";
	private String password = "password";
	private int createdUserId = -1;
	

@Before
public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException {
User u = null;
if((u=ezshop.login(username, password))==null) {
	createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
}

ezshop.login(username, password);
ezshop.logout();
}
@After
public void after() throws InvalidUsernameException, InvalidPasswordException, InvalidUserIdException, UnauthorizedException, InvalidRoleException {
if(createdUserId > 0) {
	ezshop.login(username, password);
	ezshop.deleteUser(createdUserId);
}
ezshop.reset();
}
@Test
public  void testCreateCard() throws InvalidUserIdException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException,InvalidRoleException {
			// login
			ezshop.logout();
	 		assertEquals(null,ezshop.login("notExistingUsername", "notExistingpPsw"));
			//role empty
	 		assertThrows(UnauthorizedException.class, ()->{ezshop.createCard();});								
			ezshop.login(username, password);
			String str = "";
			try{
				if((str.equals(ezshop.createCard())))
					fail();
			}catch(Exception e) {
				fail();
			}
			}

@Test 
public void testAttachCardToCustomer() throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException, InvalidCustomerNameException{
	ezshop.logout();
	//No login
	assertThrows(UnauthorizedException.class,() -> {ezshop.attachCardToCustomer("1234567890",0);});
	assertEquals(null,ezshop.login("notExistingUsername", "notExistingpPsw"));
	ezshop.login(username, password);

	int id=1;
	id = ezshop.defineCustomer("testCustomer");
	Customer c = ezshop.getCustomer(id);
	assertEquals(new Integer(id),c.getId());
	//valid
	String card = ezshop.createCard();
	ezshop.attachCardToCustomer(card,id);
	c.setCustomerCard(card);
	assertTrue(c.getCustomerCard().equals(card));
	ezshop.deleteCustomer(id);


	//invalid customer id
	assertThrows(InvalidCustomerIdException.class, ()->{ezshop.attachCardToCustomer("1234567890",0);});
	assertThrows(InvalidCustomerIdException.class, ()->{ezshop.attachCardToCustomer("1234567890",null);});

	//invalid customer
	assertFalse(ezshop.attachCardToCustomer("1234567890",2));


	//invalid card
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.attachCardToCustomer(null,1);});     
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.attachCardToCustomer("",1);});     
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.attachCardToCustomer("12345678910",1);});     
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.attachCardToCustomer("12345",1);});     
		
	// create test 
	/*final int id2 = ezshop.defineCustomer("testCustomer2");
	Customer c2 = ezshop.getCustomer(id2);
	c2.setId(null);
	assertFalse(ezshop.attachCardToCustomer("1234567890", c2.getId()));		
	ezshop.deleteCustomer(id2);*/

}
@Test 
public void testModifyPointsOnCard()throws InvalidCustomerCardException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException, InvalidCustomerIdException, InvalidCustomerNameException{
	assertThrows(UnauthorizedException.class, ()->{ezshop.modifyPointsOnCard("123456789", 10);});	
	// login
	ezshop.login(username, password);
    
	//invalid card
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.modifyPointsOnCard(null,0);});     
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.modifyPointsOnCard("",0);});     
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.modifyPointsOnCard("12345678910",0);});     
	assertThrows(InvalidCustomerCardException.class, ()->{ezshop.modifyPointsOnCard("12345",0);});

	//absent card
	assertFalse(ezshop.modifyPointsOnCard("1234567891", 10));
	
	// create test  1   
		final int id = ezshop.defineCustomer("testCustomer1");
		Customer c = ezshop.getCustomer(id);
		String card = ezshop.createCard();
		
		ezshop.attachCardToCustomer(card,id);
		c.setCustomerCard(card);
		System.out.println(c.getCustomerCard());
		
		assertEquals(true,ezshop.modifyPointsOnCard(c.getCustomerCard(),10));
		assertEquals(new Integer(10), c.getPoints());
	
		//invalid points
		assertFalse(ezshop.modifyPointsOnCard(card, -20));
		
		ezshop.deleteCustomer(id);
		
}


}