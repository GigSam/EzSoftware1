
package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.RoleEnum;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class CustomerAPITest {
private final EZShop ezshop = new EZShop();
private String username = "testUserCustomerApiEZShop";
private String password = "password";
private int createdUserId = -1;


@Before
public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException, InvalidCustomerIdException {
	User u = null;
	if((u=ezshop.login(username, password))==null) {
	createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
	}
	//delete test customers?
	ezshop.login(username, password);
	ezshop.logout();
	}

@After
public void after() throws InvalidUsernameException, InvalidPasswordException, InvalidUserIdException, UnauthorizedException, InvalidRoleException {
if(createdUserId > 0) {
	ezshop.login(username, password);
	ezshop.deleteUser(createdUserId);
}
}

@Test
public void testDefineCustomer() throws InvalidUsernameException, InvalidPasswordException, UnauthorizedException,InvalidRoleException,InvalidCustomerNameException, InvalidCustomerIdException{		
	//before login lo possono fare tutti
	assertThrows(UnauthorizedException.class, ()->{ezshop.defineCustomer("alice");});		
	// login
	ezshop.login(username, password);
	//check sul current UserRole se è empty???	
	// customerName
	//null
	assertThrows(InvalidCustomerNameException.class, ()->{ezshop.defineCustomer(null);});
	// empty
	assertThrows(InvalidCustomerNameException.class, ()->{ezshop.defineCustomer("");});			
	int id =-1;
	try{
		if((id=ezshop.defineCustomer("alice"))<=0)
			fail();
	}catch(Exception e) {
		fail();
	}
	// se già esiste lo stesso username -> exception!		
	assertEquals(new Integer(-1), ezshop.defineCustomer("alice"));	
	//delete customer
	ezshop.deleteCustomer(id);
}

@Test
public void testDeleteCustomer() throws InvalidUsernameException, InvalidPasswordException,InvalidCustomerIdException,UnauthorizedException{
	ezshop.login(username, password);
	int id[] = {-1};
	try{
		if((id[0]=ezshop.defineCustomer("alice"))<=0)
			fail();
	}catch(Exception e) {
		fail();
	}
	ezshop.logout();	
	// no logged user
	assertThrows(UnauthorizedException.class, ()->{ezshop.deleteCustomer(id[0]);});	
	// now logged
	ezshop.login(username, password);
	// invalid id
	assertThrows(InvalidCustomerIdException.class, ()->{ezshop.deleteCustomer(0);});
	assertThrows(InvalidCustomerIdException.class, ()->{ezshop.deleteCustomer(null);});	
	// valid
	assertEquals(true, ezshop.deleteCustomer(id[0]));	
	// double remove
	assertFalse(ezshop.deleteCustomer(id[0]));
}

@Test
	public void testGetCustomer() throws InvalidUsernameException, InvalidPasswordException,InvalidCustomerIdException,UnauthorizedException{
	//no logged user
		assertThrows(UnauthorizedException.class, ()->{ezshop.getCustomer(2);});	
		// now logged
		ezshop.login(username, password);
		assertThrows(Exception.class, ()->{ezshop.getCustomer(null);});	//non mi lancia l'eccezione giusta?
		// 0
		assertThrows(InvalidCustomerIdException.class, ()->{ezshop.getCustomer(0);});	
		// not valid
		assertThrows(InvalidCustomerIdException.class, ()->{ezshop.getCustomer(-3);});
		// valid but not present 
		assertNull(ezshop.getCustomer(Integer.MAX_VALUE));
		// present
		int id=-1;
		try {
			id = ezshop.defineCustomer("customerName");
			Customer c = ezshop.getCustomer(id);
			assertEquals(new Integer(id), c.getId());
			assertEquals("customerName", c.getCustomerName());	
		}catch(Exception e) {fail();}		
	
		ezshop.deleteCustomer(id);
	}
	
	@Test
	public void testGetAllCustomers() throws InvalidUsernameException, InvalidPasswordException,UnauthorizedException{
		//no logged user
		assertThrows(UnauthorizedException.class, ()->{ezshop.getAllCustomers();});	
		// now logged
		ezshop.login(username, password);
		int num = -1;	
		try {
			List<Customer> customers = ezshop.getAllCustomers();
			num = customers.size();
		}catch(Exception e) {fail();}
	}
	
	@Test
	public void testModifyCustomer() throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException{
		assertThrows(UnauthorizedException.class, ()->{ezshop.modifyCustomer(1, "Amico", "1112223334");});	
		//logged
		ezshop.login(username, password);
		//test customer
		final int id = ezshop.defineCustomer("Amica");
		Customer c = ezshop.getCustomer(id);
		ezshop.modifyCustomer(id,"Amicaaaa", "1234567890");
		//invalid customer name
		//null
		assertThrows(InvalidCustomerNameException.class, ()->{ezshop.modifyCustomer(id,null, "1234567890");});
		//empty
		assertThrows(InvalidCustomerNameException.class, ()->{ezshop.modifyCustomer(id,"", "1234567890");});			
		//invalid card
		assertThrows(InvalidCustomerCardException.class, ()->{ezshop.modifyCustomer(id,"Amicaaa", "123");});     
		assertThrows(InvalidCustomerCardException.class, ()->{ezshop.modifyCustomer(id,"Amicaaa", "127843689723");});     

		assertEquals(true,ezshop.modifyCustomer(id,"Amicaaa",""));	
		
		// valid
				try {
					if(!ezshop.modifyCustomer(id,"Amicaaaa", "1234567890"))
						fail();
					c = ezshop.getCustomer(id);
					//checks
					assertEquals(new Integer(id), c.getId());
					assertEquals("Amicaaaa", c.getCustomerName());
					assertEquals("1234567890", c.getCustomerCard());
					// clean
					ezshop.deleteCustomer(id);
				}catch(Exception e) {fail();}
	}
	
}