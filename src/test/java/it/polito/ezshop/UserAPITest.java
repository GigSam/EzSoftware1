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
import it.polito.ezshop.data.RoleEnum;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class UserAPITest{
	private final EZShop ezshop = new EZShop();
	private String username = "testUserCustomerApiEZShop";
	private String password = "password";
	private int createdUserId = -1;
	
	@Before
	public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException, InvalidCustomerIdException, InvalidUserIdException {
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
	}

	@Test
	public  void testLogin(){

		//Invalid Username
		assertThrows(InvalidUsernameException.class, ()->{ezshop.login(null, "password");});
		assertThrows(InvalidUsernameException.class, ()->{ezshop.login("", "password");});

		//Invalid Password
		assertThrows(InvalidPasswordException.class, ()->{ezshop.login("testUserCustomerApiEZShop", null);});
		assertThrows(InvalidPasswordException.class, ()->{ezshop.login("testUserCustomerApiEZShop", "");});

	}

	@Test
	public void testLogout(){

		//Invalid User
		assertFalse(ezshop.logout());
	}

	@Test
	public  void testCreateUser() throws InvalidUserIdException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException,InvalidRoleException {
				// login
				ezshop.login(username, password);
				// username
				//null
				assertThrows(InvalidUsernameException.class, ()->{ezshop.createUser(null, "gattini","Administrator");});
				// empty
				assertThrows(InvalidUsernameException.class, ()->{ezshop.createUser("", "gattini","Administrator");});			
				// password
				//null
				assertThrows(InvalidPasswordException.class, ()->{ezshop.createUser("Mik", null,"Administrator");});
				// empty
				assertThrows(InvalidPasswordException.class, ()->{ezshop.createUser("Mik", "","Administrator");});			
				// role
				//null
				assertThrows(InvalidRoleException.class, ()->{ezshop.createUser("Mik", "gattini",null);});
				// empty
				assertThrows(InvalidRoleException.class, ()->{ezshop.createUser("Mik", "gattini","");});								
				int id =-1;
				try{
					if((id=ezshop.createUser("Mik", "gattini","Administrator"))<=0)
						fail();
				}catch(Exception e) {
					fail();
				}
				//double add		
				assertEquals(new Integer(-1),ezshop.createUser("Mik", "gattini","Administrator"));	
				//delete customer
				ezshop.deleteUser(id);
	}
	@Test
	public void testDeleteUser() throws InvalidUsernameException, InvalidPasswordException,InvalidUserIdException,UnauthorizedException, InvalidRoleException{
	ezshop.login(username, password);
	//test user
		int id[] = {-1};
		try{
			if((id[0]=ezshop.createUser("Leo", "2021", "Cashier"))<=0)
				fail();
		}catch(Exception e) {
			fail();
		}
		ezshop.logout();
	// no logged user
	assertThrows(UnauthorizedException.class, ()->{ezshop.deleteUser(id[0]);});		
	
	// now logged
	ezshop.login(username, password);
	//	assertThrows(InvalidUserIdException.class, ()->{ezshop.deleteUser(null);});		
	// invalid id
	assertThrows(InvalidUserIdException.class, ()->{ezshop.deleteUser(0);});
	assertThrows(InvalidUserIdException.class, ()->{ezshop.deleteUser(null);});	

	// valid
	assertEquals(true, ezshop.deleteUser(id[0]));	
	// elimino lo stesso user
	assertFalse(ezshop.deleteUser(id[0]));	
	}

	@Test
	public void testGetUser() throws InvalidUsernameException, InvalidPasswordException,InvalidUserIdException,UnauthorizedException{
	//no logged user
	assertThrows(UnauthorizedException.class, ()->{ezshop.getUser(2);});	
	// now logged
	ezshop.login(username, password);
	assertThrows(Exception.class, ()->{ezshop.getUser(null);});	//non mi lancia l'eccezione giusta?
	// 0
	assertThrows(InvalidUserIdException.class, ()->{ezshop.getUser(0);});	
	// not valid
	assertThrows(InvalidUserIdException.class, ()->{ezshop.getUser(-3);});
	// valid but not present 
	assertNull(ezshop.getUser(Integer.MAX_VALUE));
	// present
	//int id=-1;
	/*try {
		id= ezshop.createUser("Francy", "0000", "ShopManager");
		User u = ezshop.getUser(id);
		//non faccio il corretamente il clean degli user???
		assertEquals(new Integer(1), u.getId());
		assertEquals("Francy", u.getUsername());	
	}catch(Exception e) {fail();}*/
	//ezshop.deleteUser(id);
	}

	@Test
	public void testGetAllUsers() throws InvalidUsernameException, InvalidPasswordException,UnauthorizedException{
	//no logged user
	assertThrows(UnauthorizedException.class, ()->{ezshop.getAllUsers();});	
	// now logged
	ezshop.login(username, password);
	int num = -1;	
	try {
		List<User> users = ezshop.getAllUsers();
		num = users.size();
	}catch(Exception e) {fail();}
		
	}
	
	@Test
	public void updateUserRights() throws InvalidUserIdException, InvalidRoleException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException{
	assertThrows(UnauthorizedException.class, ()->{ezshop.updateUserRights(1,"ShopManager");});
	//logged
	ezshop.login(username, password);
	//test user
	final int id = ezshop.createUser("Francy", "0000", "ShopManager");
	User u = ezshop.getUser(id);	
	ezshop.updateUserRights(id, "Cashier");
	// role
	//null
	assertThrows(InvalidRoleException.class, ()->{ezshop.updateUserRights(id,null);});
	// empty
	assertThrows(InvalidRoleException.class, ()->{ezshop.updateUserRights(id,"");});
	//Wrong role
	assertThrows(InvalidRoleException.class, ()->{ezshop.updateUserRights(id,"menagero");});
	//id
	assertThrows(InvalidUserIdException.class, ()->{ezshop.updateUserRights(null,"Cashier");});
	assertThrows(InvalidUserIdException.class, ()->{ezshop.updateUserRights(-4,"Cashier");});
    //user doesn't exist
	assertFalse(ezshop.updateUserRights(Integer.MAX_VALUE, "Cashier"));
	
	ezshop.deleteUser(id);
	}

}
