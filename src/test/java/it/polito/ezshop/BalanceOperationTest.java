package it.polito.ezshop;

import it.polito.ezshop.data.OrderClass;
import it.polito.ezshop.data.OrderStatus;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import org.junit.Test;

import it.polito.ezshop.data.BalanceOperationClass;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.*;

public class BalanceOperationTest {

    @Test
    public void testConstructor() {
        // invalid money
//        assertThrows(Exception.class, () -> {
//            final BalanceOperationClass o = new BalanceOperationClass(0,"CREDIT");
//        });
        // invalid type
        assertThrows(Exception.class, () -> {
            final BalanceOperationClass o = new BalanceOperationClass(5,"cREEDiiiiit");
        });

    }

    @Test
    public void testInvalidSetBalanceId() {
        BalanceOperationClass bo = new BalanceOperationClass();

        assertThrows(Exception.class, () -> {bo.setBalanceId(-1);});

        //assertThrows(Exception.class, () -> {bo.setBalanceId(null);});

    }

    @Test
    public void testSetBalanceId()throws InvalidTransactionIdException {

        BalanceOperationClass bo = new BalanceOperationClass();

        bo.setBalanceId(10);
    }
    @Test
    public void testInvalidSetDescription() {
        BalanceOperationClass bo = new BalanceOperationClass();

        assertThrows(Exception.class, () -> {bo.setDescription(String.join("", Collections.nCopies(1001, ".")));});

        //assertThrows(Exception.class, () -> {bo.setDescription(null);});

    }

    @Test
    public void testSetDescription() throws Exception {

        BalanceOperationClass bo = new BalanceOperationClass();
        bo.setDescription("ciao");
    }

    @Test
    public void testWhiteBoxBalanceOperation() {
        assertThrows(Exception.class, () -> {
            final BalanceOperationClass b = new BalanceOperationClass(-5,"CREDIT");
        });

        BalanceOperationClass b2 = new BalanceOperationClass(5,"CREDIT");
        assertEquals("CREDIT",b2.getType());

        b2.setDate(LocalDate.now());
        assertEquals(LocalDate.now(),b2.getDate());

        try{
            b2.setDescription("Ciao");
        }catch(Exception e){
            fail();
        }
        assertEquals("Ciao",b2.getDescription());

        try{
            b2.setType("CREDIT");
        }catch(Exception e){
            fail();
        }
        assertEquals("CREDIT",b2.getType());

        assertThrows(Exception.class, () -> {b2.setType("ciao");});

    }
}
