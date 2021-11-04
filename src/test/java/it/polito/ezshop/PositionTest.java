package it.polito.ezshop;

import static org.junit.Assert.*;

import org.junit.Test;

import it.polito.ezshop.data.Position;

public class PositionTest {

	@Test
	public void testConstructor() {
		Position p = null;
		// null
		try {
		p = new Position(null);
		}catch(Exception e) {
			fail();
		}
		assertEquals("", p.toString());
		// empty
		p = new Position("");
		assertEquals("", p.toString());
		// wrong format
		assertThrows(Exception.class, ()->{Position p1 = new Position("1_2_3_4");});
		assertThrows(Exception.class, ()->{Position p1 = new Position("1--4");});
		assertThrows(Exception.class, ()->{Position p1 = new Position("-a");});
		// not numeric
		assertThrows(Exception.class, ()->{Position p1 = new Position("a-2-c");});
		// valid
		try {
			Position p2 = new Position("1-b-2");
			assertEquals(1, p2.getAisleId());
			assertEquals("b", p2.getRackId());
			assertEquals(2, p2.getLevelId());
			assertEquals("1-b-2", p2.toString());
		}catch(Exception e) {
			fail();
		}
	}
	
	@Test
	public void testEquals() {
		Position p1 = new Position("1-a-1");
		// different class
		assertFalse(p1.equals(""));
		// same object
		assertTrue(p1.equals(p1));
		// null
		assertFalse(p1.equals(null));
		// diffreent asile
		assertFalse(p1.equals(new Position("2-a-1")));
		// diffreent rack
		assertFalse(p1.equals(new Position("1-b-1")));
		// diffreent level
		assertFalse(p1.equals(new Position("1-a-2")));
		// equal
		assertTrue(p1.equals(new Position("1-a-1")));
	}
}
