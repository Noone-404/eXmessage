package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class ErrorManagerFactoryTest {

	@Test
	public void testGetInstance() {
		ErrorManagerFactory factory = ErrorManagerFactory.getInstance();
		assertThat("return type is not allowed to be null", factory, notNullValue());
	}

	@Test
	public void testMakeInstance() {
		ErrorManagerInterface manager = ErrorManagerFactory.getInstance().make();
		assertThat("return type is not allowed to be null", manager, notNullValue());
		assertThat("return object must be of type ErrorManager", manager.getClass().getSimpleName(), equalTo("ErrorManager"));
	}
}
