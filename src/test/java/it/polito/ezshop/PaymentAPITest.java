package it.polito.ezshop;

import it.polito.ezshop.data.*;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class PaymentAPITest {
    private final it.polito.ezshop.data.EZShop ezshop = new EZShop();
    private String username = "testUserProductApiEZShop";
    private String password = "password";
    private int createdUserId = -1;

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, InvalidProductCodeException, UnauthorizedException, InvalidProductIdException {
        User u = null;
        if((u=ezshop.login(username, password))==null) {
            createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
        }else if(u.getRole().equals(RoleEnum.Cashier.name())) {
            username+="123456789101112";
            createdUserId = ezshop.createUser(username, password, RoleEnum.Administrator.name());
        }

        ezshop.logout();
    }
    @After
    public void after() throws InvalidUsernameException, InvalidPasswordException, InvalidUserIdException, UnauthorizedException {
        if(createdUserId > 0) {
            ezshop.login(username, password);
            ezshop.deleteUser(createdUserId);
        }
    }

    @Test
    public void testReceiveCashPayment() throws InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException {
        SaleTransaction saleTransaction = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        assertThrows(UnauthorizedException.class, ()->{ezshop.receiveCashPayment(1, 50);});
        // login
        ezshop.login(username, password);
        //After login
        //Invalid Transaction Id:
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.receiveCashPayment(saleTransaction.getTicketNumber(), 50);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.receiveCashPayment(-1, 50);});

        //Invalid Cash
        AccountBook aB = new AccountBookClass(100);
        Integer idSale = aB.addSaleTransaction(saleTransaction);
        assertThrows(InvalidPaymentException.class,()->{ezshop.receiveCashPayment(idSale, -50);});

        //Absent Sale Transaction
        try {
            aB.removeSaleTransaction(idSale);
        }catch (Exception e){
            fail();
        }
        assertEquals(-1,(int) ezshop.receiveCashPayment(idSale, 50));

        //change<0
        aB = ezshop.getAccountBook();
        SaleTransaction saleTransaction2 = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        Integer idSale2 = aB.addSaleTransaction(saleTransaction2);
        aB.getSaleTransaction(idSale2).setPrice(150);
        assertEquals(-1,(int) ezshop.receiveCashPayment(idSale2, 50));

        //Correct payment
        aB.getSaleTransaction(idSale2).setPrice(20);
        assertEquals((50-(int) aB.getSaleTransaction(idSale2).getPrice()),(int) ezshop.receiveCashPayment(idSale2, 50));

    }

    @Test
    public void testReceiveCreditCardPayment() throws InvalidPasswordException, InvalidUsernameException, InvalidCreditCardException, InvalidTransactionIdException, UnauthorizedException {
        SaleTransaction saleTransaction = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        assertThrows(UnauthorizedException.class, ()->{ezshop.receiveCreditCardPayment(1, "4485370086510891");});
        // login
        ezshop.login(username, password);
        //After login
        //Invalid Transaction Id:
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.receiveCreditCardPayment(saleTransaction.getTicketNumber(), "4485370086510891");});
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.receiveCreditCardPayment(-1, "4485370086510891");});


        //Absent Sale Transaction
        AccountBook aB = new AccountBookClass(100);
        Integer idSale = aB.addSaleTransaction(saleTransaction);

        try {
            aB.removeSaleTransaction(idSale);
        }catch (Exception e){
            fail();
        }
        assertFalse(ezshop.receiveCreditCardPayment(idSale,"4485370086510891" ));

        //change<0
        aB = ezshop.getAccountBook();
        SaleTransaction saleTransaction2 = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        Integer idSale2 = aB.addSaleTransaction(saleTransaction2);
        aB.getSaleTransaction(idSale2).setPrice(150);
        assertFalse(ezshop.receiveCreditCardPayment(idSale2, "5100293991053009"));

        //Correct payment
        aB.getSaleTransaction(idSale2).setPrice(20);
        assertTrue(ezshop.receiveCreditCardPayment(idSale2, "4485370086510891"));

    }

    @Test
    public void testReturnCashPayment() throws InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException {
        SaleTransaction saleTransaction = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        ReturnTransaction returnTransaction = new ReturnTransactionClass(saleTransaction,ReturnStatus.STARTED);
        assertThrows(UnauthorizedException.class, ()->{ezshop.returnCashPayment(1);});
        // login
        ezshop.login(username, password);
        //After login
        //Invalid Transaction Id:
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnCashPayment(returnTransaction.getReturnId());});
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnCashPayment(-1);});


        AccountBook aB = ezshop.getAccountBook();
        Integer idReturn = aB.addReturnTransaction(returnTransaction);
        //Absent Sale Transaction
        try {
            aB.removeReturnTransaction(idReturn);
        }catch (Exception e){
            fail();
        }
        assertEquals(-1,(int) ezshop.returnCashPayment(idReturn));

        //Not closed sale
        ReturnTransaction returnTransaction2 = new ReturnTransactionClass(saleTransaction,ReturnStatus.STARTED);
        Integer idReturn2 = aB.addReturnTransaction(returnTransaction2);
        assertEquals(-1,(int) ezshop.returnCashPayment(idReturn2));

        //Correct return
        ReturnTransaction returnTransaction3 = new ReturnTransactionClass(saleTransaction,ReturnStatus.CLOSED);
        Integer idReturn3 = aB.addReturnTransaction(returnTransaction3);
        // fallisce perchè la sale non è in accountBook
        assertEquals((int) ((ReturnTransactionClass)aB.getReturnTransaction(idReturn3)).getMoney(),(int) ezshop.returnCashPayment(idReturn3));

    }

    @Test
    public void testReturnCreditCardPayment() throws InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidCreditCardException {
        SaleTransaction saleTransaction = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        ReturnTransaction returnTransaction = new ReturnTransactionClass(saleTransaction,ReturnStatus.STARTED);
        assertThrows(UnauthorizedException.class, ()->{ezshop.returnCreditCardPayment(1,"4485370086510891");});
        // login
        ezshop.login(username, password);
        //After login
        //Invalid Transaction Id:
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnCreditCardPayment(returnTransaction.getReturnId(),"4485370086510891");});
        assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnCreditCardPayment(-1,"4485370086510891");});


        AccountBook aB = ezshop.getAccountBook();
        Integer idReturn = aB.addReturnTransaction(returnTransaction);
        //Absent Sale Transaction
        try {
            aB.removeReturnTransaction(idReturn);
        }catch (Exception e){
            fail();
        }
        assertEquals(-1,(int) ezshop.returnCreditCardPayment(idReturn,"4485370086510891"));

        //Not closed return
        ReturnTransaction returnTransaction2 = new ReturnTransactionClass(saleTransaction,ReturnStatus.STARTED);
        Integer idReturn2 = aB.addReturnTransaction(returnTransaction2);
        assertEquals(-1,(int) ezshop.returnCreditCardPayment(idReturn2,"4485370086510891"));

        //Correct return
        ReturnTransaction returnTransaction3 = new ReturnTransactionClass(saleTransaction,ReturnStatus.CLOSED);
        Integer idReturn3 = aB.addReturnTransaction(returnTransaction3);
        assertEquals((int) ((ReturnTransactionClass)aB.getReturnTransaction(idReturn3)).getMoney(), (int) ezshop.returnCreditCardPayment(idReturn3,"4485370086510891"));

    }

}
