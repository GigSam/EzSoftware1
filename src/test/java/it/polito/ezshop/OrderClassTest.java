package it.polito.ezshop;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import it.polito.ezshop.data.OrderClass;
import it.polito.ezshop.data.OrderStatus;

public class OrderClassTest {
	@Test
	public void testConstructor() {
		// invalid productCode
		assertThrows(Exception.class, ()->{final OrderClass o = new OrderClass(1,  LocalDate.now(), null, null, 1, 10, OrderStatus.ISSUED);});
		// invalid unit price
		assertThrows(Exception.class, ()->{final OrderClass o = new OrderClass(1,  LocalDate.now(), null, "4006381333931", -0.0, 10, OrderStatus.ISSUED);});
		// invalid quantity
		assertThrows(Exception.class, ()->{final OrderClass o = new OrderClass(1,  LocalDate.now(), null, "4006381333931", 1, -10, OrderStatus.ISSUED);});
		// valid
		try {
			final OrderClass o = new OrderClass(1, LocalDate.now(), null, "4006381333931", 1, 10, OrderStatus.ISSUED);
		}catch(Exception e) {
			fail();
		}
	}
	@Test
	public void testSetOrderId() {
		final OrderClass o = new OrderClass(1, LocalDate.now(), null, "4006381333931", 1, 10, OrderStatus.ISSUED);
		assertEquals(new Integer(1), o.getOrderId());
		// null
		assertThrows(Exception.class, ()->{o.setOrderId(null);});
		assertEquals(new Integer(1), o.getOrderId());
		// invalid
		assertThrows(Exception.class, ()->{o.setOrderId(-1);});
		assertEquals(new Integer(1), o.getOrderId());
		// valid
		try {
			o.setOrderId(2);
		}catch(Exception e) {
			fail();
		}
	}
	@Test
	public void testSetQuantity() {
		final OrderClass o = new OrderClass(1, LocalDate.now(), null, "4006381333931", 1, 10, OrderStatus.ISSUED);
		assertEquals(10, o.getQuantity());
		// lower than 0
		assertThrows(Exception.class, ()->{o.setQuantity(-1);});
		assertEquals(10, o.getQuantity());
		// valid
		try{
			o.setQuantity(1);
		}catch(Exception e) {
			fail();
		}
		assertEquals(1, o.getQuantity());
		// check if money has been updated
		assertEquals(1, o.getMoney(), 0.00001);
	}
	@Test
	public void testSetPricePerUnit() {
		final OrderClass o = new OrderClass(1,  LocalDate.now(), null, "4006381333931", 1, 10, OrderStatus.ISSUED);
		assertEquals(1, o.getPricePerUnit(), 1e-6);
		// lower than 0
		assertThrows(Exception.class, ()->{o.setPricePerUnit(-0.0);});
		assertEquals(1, o.getPricePerUnit(), 1e-6);
		// valid
		try {
			o.setPricePerUnit(2.0);
		}catch(Exception e) {
			fail();
		}
		assertEquals(2.0, o.getPricePerUnit(), 1e-6);
		// check if money has been updated
		assertEquals(20.0, o.getMoney(), 0.00001);
		
	}
	@Test
	public void testSetProductCode() {
		final OrderClass o = new OrderClass(1,LocalDate.now(), null, "4006381333931", 1, 10, OrderStatus.ISSUED);
		assertEquals("4006381333931", o.getProductCode());
		// null
		assertThrows(Exception.class, ()->{o.setProductCode(null);});
		assertEquals("4006381333931", o.getProductCode());
		// invalid
		assertThrows(Exception.class, ()->{o.setProductCode("");});
		assertEquals("4006381333931", o.getProductCode());
		// valid
		try {
			o.setProductCode("4006381333900");
		}catch(Exception e) {
			fail();
		}
		assertEquals("4006381333900", o.getProductCode());
		
	}
	
	@Test
	public void testWhiteBox() {
		try {
		OrderClass o2 = new OrderClass("4006381333931", 1, 10, OrderStatus.PAYED);
		}catch(Exception e) {fail();}
		final OrderClass o = new OrderClass( "4006381333931", 1, 10);
		try {
			o.setBalanceId(2);
		}catch(Exception e) {
			fail();
		}
		assertEquals(new Integer(2), o.getBalanceId());
		// status
		assertEquals("ISSUED", o.getStatus());
		try {
			o.setStatus("PAYED");
		}catch(Exception e) {
			fail();
		}
		// invalid status
		assertThrows(Exception.class, ()->{o.setStatus("xx");});
		// OrderStatus
		assertEquals(OrderStatus.PAYED, o.getOrderStatus());
	}
}
