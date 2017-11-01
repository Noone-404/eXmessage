package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ErrorManagerTest {

	private ErrorManager manager;

	@Before
	public void setUp() {
		manager = new ErrorManager();
	}

	@Test
	public void testSetStreamProcessor() {
		StreamProcessorInterface pro = Mockito.mock(StreamProcessorInterface.class);
		assertThat("Reference must be null", manager.getStreamProcessor(), nullValue());
		manager.setStreamProcessor(pro);
		assertThat("Reference is not expected to be null", manager.getStreamProcessor(), notNullValue());
		assertThat("Reference is expected to be the object used in the test case", manager.getStreamProcessor(), is(pro));
	}

	@Test
	public void testRegisterObserverAt() {
		ErrorManagerObserverInterface manObserver = Mockito.mock(ErrorManagerObserverInterface.class);
		StreamProcessorInterface      pro         = Mockito.mock(StreamProcessorInterface.class);
		manager.registerObserver(manObserver);
		manager.setStreamProcessor(pro);

		ProtocolErrorInterface error   = Mockito.mock(ProtocolErrorInterface.class);
		ErrorElement           element = Mockito.mock(ErrorElement.class);
		when(error.makeErrorElement()).thenReturn(element);

		TestObservable component = new TestObservable();
		manager.registerObserverAt(component);

		Object[] observers = component.getObservers().toArray();
		assertThat("New registered observer expected", observers.length, is(1));
		ProtocolErrorObserverInterface errorObserver = (ProtocolErrorObserverInterface) observers[0];
		errorObserver.onProtocolError(error);

		Mockito.verify(error).makeErrorElement();
		Mockito.verify(pro).pushDataDown(element);
		Mockito.verify(manObserver).errorReceived(error);
	}

	@Test
	public void testRegisterObserver() {
		assertThat("No event listeners expected", this.manager.getObservers().size(), is(0));
		this.manager.registerObserver(Mockito.mock(ErrorManagerObserverInterface.class));
		assertThat("An event listener has to exist", this.manager.getObservers().size(), is(1));
	}

	@Test
	public void testRegisterObserverImmutable() {
		ErrorManagerObserverInterface obs = Mockito.mock(ErrorManagerObserverInterface.class);

		this.manager.registerObserver(obs);
		int size = this.manager.getObservers().size();
		this.manager.registerObserver(obs);

		assertThat("Multiple event listener registrations do not change listener count", this.manager.getObservers().size(), is(size));
	}

	@Test
	public void testUnregisterObserver() {
		ErrorManagerObserverInterface obs = Mockito.mock(ErrorManagerObserverInterface.class);
		this.manager.registerObserver(obs);

		int size = this.manager.getObservers().size();
		this.manager.unregisterObserver(obs);

		assertThat("Un-registering event listener reduces listener count", this.manager.getObservers().size(), is(size - 1));
	}

	@Test
	public void testUnregisterObserverUnregistered() {
		ErrorManagerObserverInterface obs  = Mockito.mock(ErrorManagerObserverInterface.class);
		ErrorManagerObserverInterface obs2 = Mockito.mock(ErrorManagerObserverInterface.class);

		this.manager.registerObserver(obs);
		assertThat("Event listener required", this.manager.getObservers().size(), is(1));

		this.manager.unregisterObserver(obs2);
		assertThat("Un-registration of non-registered event listener must not change observer count", this.manager.getObservers().size(), is(1));
	}

	class TestObservable implements ProtocolErrorObservableInterface {

		private Collection<ProtocolErrorObserverInterface> observers;

		TestObservable() {
			this.observers = new ArrayList<>();
		}

		@Override
		public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
			observers.add(observer);
		}

		@Override
		public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
			observers.remove(observer);
		}

		Collection<ProtocolErrorObserverInterface> getObservers() {
			return this.observers;
		}
	}
}