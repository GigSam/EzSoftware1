package it.polito.ezshop;

import it.polito.ezshop.data.*;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import org.junit.Test;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;

public class AccountBookTest {

    @Test
    public void testInvalidRemoveSaleTransaction() {
        AccountBook aB = new AccountBookClass(0);

        //Check Throws with negative id
        assertThrows(InvalidTransactionIdException.class, () -> {
            aB.removeSaleTransaction(-1);
        });
        //Check Throws with null
        assertThrows(InvalidTransactionIdException.class, () -> {
            aB.removeSaleTransaction(null);
        });
        //Check Throws with saleTransaction that doesn't exist
        assertThrows(InvalidTransactionIdException.class, () -> {
            aB.removeSaleTransaction(500);
        });
    }
    @Test
    public void testRemoveSaleTransaction() throws InvalidTransactionIdException{
        Connect.connect();
        AccountBook aB = new AccountBookClass(0);
        //Check remove if id exist in Sale Transaction (IMPLICIT TESTING)
        SaleTransactionClass sT = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.valueOf("STARTED"));
        Integer id = aB.addSaleTransaction(sT);
        aB.removeSaleTransaction(id);

    }

    @Test
    public void testSetBalance(){
        AccountBook aB = new AccountBookClass(0);
        assertTrue(aB.setBalance(500));
        assertFalse(aB.setBalance(-500));
    }

    @Test
    public void testInvalidGetSaleTransaction() {
        AccountBook aB = new AccountBookClass(0);

        //Check Throws with negative id
        assertThrows(Exception.class, () -> {
            aB.getSaleTransaction(-1);
        });
        //Check Throws with null
        assertThrows(Exception.class, () -> {
            aB.getSaleTransaction(null);
        });
        //Check Throws with saleTransaction that doesn't exist
        assertThrows(Exception.class, () -> {
            aB.getSaleTransaction(500);
        });
    }
    @Test
    public void testGetSaleTransaction() throws InvalidTransactionIdException{
        AccountBook aB = new AccountBookClass(0);
        Map<String,TicketEntryClass> tec = new HashMap<>();
        //Check add saleTransaction (IMPLICIT TESTING)
        SaleTransactionClass sT = new SaleTransactionClass(-1," String description", 20.0, LocalDate.now(), "CREDIT", "CASH", Time.valueOf(LocalTime.now()),
                SaleStatus.valueOf("STARTED"),  new LoyaltyCardClass("gdskj4679v",50), tec, 5.0);
        Integer id = aB.addSaleTransaction(sT);
        aB.getSaleTransaction(id);

    }

    @Test
    public void testWhiteBoxAccountBook() throws Exception{

        AccountBookClass ab = new AccountBookClass(-25);
        assertEquals(new Double(0),ab.getBalance());

        // setSaleTransaction
        Map<Integer, SaleTransaction > sales = new HashMap<>();
        sales.put(1, new SaleTransactionClass(10.0, "CREDIT_CARD",
					new Time(System.currentTimeMillis()), SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2,
					new HashMap<>(), 0.1));
        assertThrows(Exception.class, ()->{ab.setSaleTransactionMap(null);});
        // valid
        ab.setSaleTransactionMap(sales);
        assertEquals(1, ab.getSaleTransactionMap().size());
        

        // setReturnTransaction
        Map<Integer, ReturnTransaction > returns = new HashMap<>();
        returns.put(1, new ReturnTransactionClass(1, "description", 3.0, LocalDate.now(), "DEBIT",
				new HashMap<>(),
				(SaleTransaction) new SaleTransactionClass(10.0, "CREDIT_CARD", new Time(System.currentTimeMillis()),
						SaleStatus.STARTED, new LoyaltyCardClass("1234567890", 1), 2, new HashMap<>(), 0.1),
				ReturnStatus.STARTED));
        assertThrows(Exception.class, ()->{ab.setReturnTransactionMap(null);});
        // valid
        ab.setReturnTransactionMap(returns);
        assertEquals(1, ab.getReturnTransactionMap().size());
        
        // setOrderClass
        Map<Integer, Order> orders = new HashMap<>();
        orders.put(1, new OrderClass(1, LocalDate.now(), null, "4006381333931", 1, 10, OrderStatus.ISSUED));

        assertThrows(Exception.class, ()->{ab.setOrderMap(null);});
        // valid
        ab.setOrderMap(orders);
        assertEquals(1, ab.getOrderMap().size());
        
        // addOrder
        int id = ab.addOrder(new OrderClass( "4006381333931", 1, 10, OrderStatus.ISSUED));
        // remove order
        ab.removeOrder(id);
        
        // addSale
        id = ab.addSaleTransaction(new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED));
        // get sale
        assertThrows(Exception.class, ()->{ab.getSaleTransaction(null);});
        assertTrue(ab.getSaleTransaction(id)!=null);
        // remove sale
        ab.removeSaleTransaction(id);
        
        // add return
        id = ab.addReturnTransaction(new ReturnTransactionClass( new SaleTransactionClass( new Time(System.currentTimeMillis()), SaleStatus.STARTED),ReturnStatus.STARTED));
        //get return
        assertThrows(Exception.class, ()->{ab.getReturnTransaction(null);});
        assertTrue(ab.getReturnTransaction(id)!=null);        
        // remove return
        ab.removeReturnTransaction(id);
        
        // get balanceOperation
        assertTrue(ab.getBalanceOperationMap()!=null);
    }

    //Approach adopted: Button up
    //Step1(Unit Test): class BalanceOperationClass -> BalanceOperationClass(), getBalanceId(), setBalanceId(), getDate(), setDate()
    //                                                 getMoney(), setMoney(), getType(),setType(), getDescription(), setDescription()
    // step 2: class BalanceOperationClass + AccountBookClass -> addBalanceOperation(),updateBalanceOperation(), getBalanceOperationByDate()
    //                                                           getBalanceOperation()
    // step 3: class BalanceOperationClass + AccountBookClass + OrderClass -> addOrder(),getOrder()
    // step 4: class BalanceOperationClass + AccountBookClass + OrderClass + SaleTransactionClass -> addSaleTransaction(),getSaleTransaction()
    // step 5: class BalanceOperationClass + AccountBookClass + OrderClass + SaleTransactionClass + ReturnTransactionClass
    // -> addReturnTransaction(),getReturnTransaction()

    @Test
    public void testAccountBookIntegrationTest(){
        EZShop ez = new EZShop();
        SaleTransaction sT = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        BalanceOperation bO = new BalanceOperationClass(50,"CREDIT");
        final Order oT = new OrderClass("400638133390",10,5);
        ReturnTransaction rT = new ReturnTransactionClass(sT,ReturnStatus.STARTED);
        Map<Integer,SaleTransaction> sTMap = new HashMap<>();
        Map<Integer,BalanceOperation> bOMap = new HashMap<>();
        Map<Integer,Order> oTMap = new HashMap<>();
        Map<Integer,ReturnTransaction> rTMap = new HashMap<>();

        //AddBalanceOperation
        //BalanceOperationMap contains bO
        bO.setBalanceId(1);
        bOMap.put(1,bO);
        AccountBookClass aB = new AccountBookClass(0);
        aB.setBalanceOperationMap(bOMap);
        ez.setAccountBook(aB);
        //AccountBookClass aB = ez.getAccountBook();
        assertFalse(aB.addBalanceOperation(bO));

        //Invalid Balance
        BalanceOperation bO2 = new BalanceOperationClass(2,"ORDER",100,LocalDate.now(),"DEBIT");
        assertFalse(aB.addBalanceOperation(bO2));

        BalanceOperation bO3 = new BalanceOperationClass(3,"SALE",50,LocalDate.now(),"CREDIT");
        assertTrue(aB.addBalanceOperation(bO3));

        //getBalanceOperation()
        assertThrows(Exception.class, ()->{aB.getBalanceOperation(null);});
        assertNotNull(aB.getBalanceOperation(3));
        assertThrows(Exception.class, ()->{aB.getBalanceOperation(-5);});

        //getBalanceOperationByDate
        //None Balance Operation
        List<BalanceOperation> list = new ArrayList<>();
        Map<Integer,BalanceOperation> map = new HashMap<>();
        aB.setBalanceOperationMap(map);
        assertEquals(list,aB.getBalanceOperationByDate(null,null));

        BalanceOperation bo4 = new BalanceOperationClass(1,"SALE",20,LocalDate.now(),"CREDIT");
        BalanceOperation bo5 = new BalanceOperationClass(2,"SALE",20,LocalDate.now().plusDays(2),"CREDIT");
        BalanceOperation bo6 = new BalanceOperationClass(3,"SALE",20,LocalDate.now().minusDays(2),"CREDIT");
        aB.addBalanceOperation(bo4);
        aB.addBalanceOperation(bo5);
        aB.addBalanceOperation(bo6);
        list.add(bo4);
        list.add(bo5);
        list.add(bo6);

        //All balance operation up to LocalDateTo
        assertEquals(list,aB.getBalanceOperationByDate(null,LocalDate.now().plusDays(3)));
        //All balance operation starting LocalDateFrom
        assertEquals(list,aB.getBalanceOperationByDate(LocalDate.now().minusDays(3),null));
        //All balance operation starting LocalDateFrom and up to LocalDateTo
        assertEquals(list,aB.getBalanceOperationByDate(LocalDate.now().minusDays(3),LocalDate.now().plusDays(3)));
        //All balance operation
        assertEquals(list,aB.getBalanceOperationByDate(null,null));


        //addOrder
        //InvalidOrder
        oT.setOrderId(1);
        assertThrows(Exception.class, () -> {
            aB.addOrder(oT);
        });
        Order oT2 = null;
        assertThrows(Exception.class, () -> {
            aB.addOrder(oT2);
        });
        //Valid Order
        Order oT3 = new OrderClass("400638133390",10,5);
        Integer orderId = aB.addOrder(oT3);
        assertEquals(orderId,oT3.getOrderId());


        //getOrder
        assertThrows(Exception.class, ()->{aB.getOrder(null);});
        assertNotNull(aB.getOrder(orderId));
        assertThrows(Exception.class, ()->{aB.getOrder(-5);});


        //addSaleTransaction
        //Invalid Sale - Id is already set or Sale is null
        sT.setTicketNumber(1);
        assertThrows(Exception.class, () -> {
            aB.addSaleTransaction(sT);
        });
        SaleTransaction sT2 = null;
        assertThrows(Exception.class, () -> {
            aB.addSaleTransaction(sT2);
        });
        //Valid Sale
        SaleTransaction sT3 = new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED);
        Integer saleId = aB.addSaleTransaction(sT3);
        assertEquals(saleId,sT3.getTicketNumber());


        //getSaleTransaction
        assertThrows(Exception.class, ()->{aB.getSaleTransaction(null);});
        assertNotNull(aB.getSaleTransaction(saleId));
        assertThrows(Exception.class, ()->{aB.getSaleTransaction(-5);});


        //addReturnTransaction
        //Invalid Return - Id is already set or Return is null
        rT.setReturnId(1);
        assertThrows(Exception.class, () -> {
            aB.addReturnTransaction(rT);
        });
        ReturnTransaction rT2 = null;
        assertThrows(Exception.class, () -> {
            aB.addReturnTransaction(rT2);
        });
        //Valid Return
        ReturnTransaction rT3 = new ReturnTransactionClass(new SaleTransactionClass(Time.valueOf(LocalTime.now()),SaleStatus.STARTED),
                ReturnStatus.STARTED);
        Integer returnId = aB.addReturnTransaction(rT3);
        assertEquals(returnId,rT3.getReturnId());


        //getReturnTransaction
        assertThrows(Exception.class, ()->{aB.getReturnTransaction(null);});
        assertNotNull(aB.getReturnTransaction(returnId));
        assertThrows(Exception.class, ()->{aB.getReturnTransaction(-5);});
    }


}
