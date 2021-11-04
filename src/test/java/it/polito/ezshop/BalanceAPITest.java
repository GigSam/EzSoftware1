package it.polito.ezshop;

import it.polito.ezshop.data.*;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class BalanceAPITest {
    private final it.polito.ezshop.data.EZShop ezshop = new EZShop();
    private String username="testOrderApiUser";
    private String password = "password";
    private String usernameC="testOrderApiUserCashier";
    private int createdUserId = -1;
    private int createdCashier = -1;

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException, InvalidProductCodeException, InvalidProductIdException {
        User u = null;
        if((u=ezshop.login(username, password))==null) {
            createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
        }else if(u.getRole().equals(RoleEnum.Cashier.name())) {
            username+="123456789101112";
            createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
        }
        while(true) {
            if((u=ezshop.login(usernameC, password))==null) {
                createdCashier = ezshop.createUser(usernameC, password, RoleEnum.Cashier.name());
                if(createdCashier>0)
                    break;
                else
                    usernameC += "1234";
            }else if(!u.getRole().equals(RoleEnum.Cashier.name())) {
                ezshop.logout();
                usernameC+="1234";
                createdCashier = ezshop.createUser(usernameC, password, RoleEnum.Cashier.name());
                if(createdCashier > 0)
                    break;
            }else
                break;
        }
        ezshop.logout();
    }
    @After
    public void after() throws InvalidUsernameException, InvalidPasswordException, InvalidUserIdException, UnauthorizedException {
        if(createdUserId > 0) {
            ezshop.login(username, password);
            ezshop.deleteUser(createdUserId);
        }
        if(createdCashier>0)
        	ezshop.deleteUser(createdCashier);
    }

    @Test
    public void testRecordBalanceUpdate() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {

        // before login
        assertThrows(UnauthorizedException.class, ()->{ezshop.recordBalanceUpdate(50);});
        // login cashier
        ezshop.login(usernameC, password);
        // cashier not auth
        assertThrows(UnauthorizedException.class, ()->{ezshop.recordBalanceUpdate(50);});
        // login Admin
        ezshop.logout();

        ezshop.login(username, password);
        //After login as Admin

        //Negative new balance
        AccountBook aB = ezshop.getAccountBook();
        aB.setBalance(100);
        assertFalse(ezshop.recordBalanceUpdate(-101));

        //Correct update
        assertTrue(ezshop.recordBalanceUpdate(99));
        // need to clean db
        ezshop.reset();
    }

    @Test
    public void testGetCreditsAndDebits() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {

        // before login
        assertThrows(UnauthorizedException.class, ()->{ezshop.getCreditsAndDebits(LocalDate.now().minusDays(3),LocalDate.now().plusDays(3));});
        // login cashier
        ezshop.login(usernameC, password);
        // cashier not auth
        assertThrows(UnauthorizedException.class, ()->{ezshop.getCreditsAndDebits(LocalDate.now().minusDays(3),LocalDate.now().plusDays(3));});
        // login Admin
        ezshop.logout();

        ezshop.login(username, password);
        //After login as Admin

        //Valid request
        EZShop ezShop = new EZShop();
        List<BalanceOperation> list = new ArrayList<>();
        AccountBookClass aB = new AccountBookClass(0);
        ezshop.setAccountBook(aB);
        aB.setBalanceOperationMap(new HashMap<>());
        BalanceOperation bo1 = new BalanceOperationClass(1,"SALE",20,LocalDate.now(),"CREDIT");
        BalanceOperation bo2 = new BalanceOperationClass(2,"SALE",20,LocalDate.now().plusDays(2),"CREDIT");
        BalanceOperation bo3 = new BalanceOperationClass(3,"SALE",20,LocalDate.now().minusDays(2),"CREDIT");
        aB.addBalanceOperation(bo1);
        aB.addBalanceOperation(bo2);
        aB.addBalanceOperation(bo3);
        list.add(bo1);
        list.add(bo2);
        list.add(bo3);
        assertEquals(list,ezshop.getCreditsAndDebits(LocalDate.now().minusDays(3),LocalDate.now().plusDays(3)));


        //Valid request with inverse date order
        assertEquals(list,ezshop.getCreditsAndDebits(LocalDate.now().plusDays(3),LocalDate.now().minusDays(3)));
        
        // db clean
        Connect.removeBalanceOperation(bo1.getBalanceId());
        Connect.removeBalanceOperation(bo2.getBalanceId());
        Connect.removeBalanceOperation(bo3.getBalanceId());
    }
}
