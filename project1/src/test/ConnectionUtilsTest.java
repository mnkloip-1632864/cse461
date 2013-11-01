package test;

import org.junit.Before;
import org.junit.Test;

public class ConnectionUtilsTest {
	
	private byte[] b1;
	private byte[] b2;
	private byte[] b1b2;
	
	@Before
	public void setUpArrays() {
		b1 = new byte[] {0, 1, 2, 3, 4};
		b2 = new byte[] {};
	}
	
	@Test
	public void testMerge() {
		
	}
	
}
