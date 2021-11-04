package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;

public class UserClass implements User {
	
	private int id;
	private String username;
	private String password;
	private RoleEnum role;

	public UserClass(int id, String username, String password, RoleEnum role){
		 if(username==null || username.isEmpty()) throw new RuntimeException(new InvalidUsernameException(username));
		 if(password==null || password.isEmpty()) throw new RuntimeException(new InvalidPasswordException(password));
		 if(role==null )throw new RuntimeException(new InvalidRoleException());
		 if(id <= 0) throw new RuntimeException(new InvalidUserIdException(""+id));
		this.id=id;
		this.username=username;
		this.password=password;
		this.role=role;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		if(id == null || id <= 0)
			throw new RuntimeException(new InvalidUserIdException());
	
		this.id=id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
	   	 if(username==null || username.isEmpty()) throw new RuntimeException(new InvalidUsernameException());
		this.username=username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
	   	 if(password==null || password.isEmpty()) throw new RuntimeException(new InvalidPasswordException());
		this.password=password;
	}

	@Override
	public String getRole() {
		return role.toString();
	}

	@Override
	public void setRole(String role) {
	   	 if(role==null || role.isEmpty()) throw new RuntimeException(new InvalidRoleException(role));
	   this.role=RoleEnum.valueOf(role);
	}
	public RoleEnum getRoleEnum () {
		return role;
	}


}
