package org.View;

import org.Model.Facade;
import org.View.IDisplay;

public class App {

	private IDisplay[] view;
	public static void main(String[] args) {
		// TODO - implement App.Main
		var test = new Facade();
		var twest = test.GetEventsById(1);
		throw new UnsupportedOperationException();
	}
}