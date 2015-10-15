package nl.saxion.server.DNS.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.saxion.server.DNS.Flags;

public class FlagsTest {
	
	@Test
	public void testSetAnswer(){
		Flags flags = new Flags((byte) 1, (byte) 1);
		
		
		flags.setAnswer();
		assertFalse("flags should be an answer", flags.isQuestion());

		flags.setQuestion();
		assertTrue("flags should be a question", flags.isQuestion());
	}
	
	@Test
	public void testSetAuthorative(){
		Flags flags = new Flags((byte) 0x0000, (byte) 0x0000);
		
		flags.setAuthorative();
		assertTrue("flags should be authorative", flags.isAuthorative());
		
		flags.setNonAuthorative();
		assertFalse("flags should be non authorative", flags.isAuthorative());
	}
}
