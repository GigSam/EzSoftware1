package it.polito.ezshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.Test;


import it.polito.ezshop.data.RoleEnum;
import it.polito.ezshop.data.UserClass;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;

public class UserClassTest {
	/* if(username==null || username.isEmpty()) throw new InvalidUsernameException();
	 if(password==null || password.isEmpty()) throw new InvalidPasswordException();
	 if(role==null || role.isEmpty() ) throw new InvalidRoleException();*/
	 
	@Test
	public void testUserClassConstructor() {
		//invalid id
		assertThrows(Exception.class, ()->{
			UserClass u = new UserClass(0,"username","password",RoleEnum.Administrator);});
		// invalid username
		assertThrows(Exception.class, ()->{
			UserClass u = new UserClass(1,null,"password",RoleEnum.Administrator);});
		assertThrows(Exception.class, ()->{
			UserClass u = new UserClass(1,"","password",RoleEnum.Administrator);});
		// invalid password
		assertThrows(Exception.class, ()->{
			UserClass u = new UserClass(1,"username",null,RoleEnum.Administrator);});
		assertThrows(Exception.class, ()->{
			UserClass u = new UserClass(1,"username","",RoleEnum.Administrator);});
	
		// invalid role
		assertThrows(Exception.class, ()->{UserClass u = new UserClass(1,"username","password",null);});
		// valid
				try {
					UserClass u = new UserClass(1, "username", "password", RoleEnum.Administrator );					
					assertEquals(new Integer(1), u.getId());
					assertEquals("username", u.getUsername());
					assertEquals("password", u.getPassword());
					assertEquals("Administrator",u.getRole());
				}catch(Exception e) {fail();}
	
	}
	
	@Test
	public void testSetUserId() {
		final UserClass u = new UserClass(1, "username", "password", RoleEnum.Administrator );					
		assertEquals(new Integer(1), u.getId());
				// null
				assertThrows(Exception.class, ()->{u.setId(null);});
				assertEquals(new Integer(1), u.getId());
				// invalid
				assertThrows(Exception.class, ()->{u.setId(0);});
				assertEquals(new Integer(1), u.getId());
				assertThrows(Exception.class, ()->{u.setId(-1);});
				assertEquals(new Integer(1), u.getId());
				// valid
				try {
					u.setId(2);
				}catch(Exception e) {
					fail();
				}		
			}
	
	@Test
	public void testSetUsername() {
		final UserClass u = new UserClass(1, "username", "password", RoleEnum.Administrator );					
		assertEquals("username", u.getUsername());
				// null
				assertThrows(Exception.class, ()->{u.setUsername(null);});
				assertEquals("username", u.getUsername());
				// empty
				assertThrows(Exception.class, ()->{u.setUsername("");});
				assertEquals("username", u.getUsername());
				// valid
				try {
					u.setUsername("username");
				}catch(Exception e) {
					fail();
				}
	}
		
		@Test
		public void testSetPassword() {
			final UserClass u = new UserClass(1, "username", "password", RoleEnum.Administrator );					
			assertEquals("password", u.getPassword());
					// null
					assertThrows(Exception.class, ()->{u.setPassword(null);});
					assertEquals("password", u.getPassword());
					// empty
					assertThrows(Exception.class, ()->{u.setPassword("");});
					assertEquals("password", u.getPassword());
					// valid
					try {
						u.setPassword("password");
					}catch(Exception e) {
						fail();
					}
		}
		
		@Test
		public void testSetRole() {
			final UserClass u = new UserClass(1, "username", "password", RoleEnum.Administrator );					
			assertEquals("Administrator", u.getRole());
					// null
					assertThrows(Exception.class, ()->{u.setRole(null);});
					assertEquals("Administrator", u.getRoleEnum().toString());
					// empty
					assertThrows(Exception.class, ()->{u.setRole("");});
					assertEquals("Administrator", u.getRole());
					// valid
					try {
						u.setPassword("Administrator");
					}catch(Exception e) {
						fail();
					}
		}
		//WB testing
		@Test 
		public void testWhiteBox() throws InvalidUserIdException, InvalidUsernameException,InvalidPasswordException,UnauthorizedException
		{final UserClass u = new UserClass(1, "username", "password", RoleEnum.Administrator );					
		try {
			//setId
		assertThrows(RuntimeException.class, () -> {u.setId(null);});
		assertThrows(RuntimeException.class, () -> {u.setId(0);});
			u.setId(2);
			assertEquals(new Integer(2), u.getId());
		} catch(Exception e) {
			fail();
		}
			//setUsername
			assertThrows(RuntimeException.class, () -> {u.setUsername(null);});
			assertThrows(RuntimeException.class, () -> {u.setUsername("");});
			try {
				u.setUsername("username");
				assertEquals("username", u.getUsername());
			} catch(Exception e) {
				fail();
			}
			//setPassword
			assertThrows(RuntimeException.class, () -> {u.setPassword(null);});
			assertThrows(RuntimeException.class, () -> {u.setPassword("");});
			try {
				u.setPassword("password");
				assertEquals("password", u.getPassword());
			} catch(Exception e) {
				fail();
			}
			//setRole
			assertThrows(RuntimeException.class, () -> {u.setRole(null);});
			assertThrows(RuntimeException.class, () -> {u.setRole("Cashiero");});
			try {
				u.setRole("Cashier");
				assertEquals("Cashier", u.getRoleEnum().toString());
			} catch(Exception e) {
				fail();
			}
		}
		
}
