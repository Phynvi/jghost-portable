package org.whired.ghost.net.reflection;

/**
 * Represents a field; Used in conjunction with the reflection system '
 * 
 * @author Whired
 */
public class RMIClass extends Accessor {

	protected RMIClass(String name) {
		super(name, true);
	}

	/**
	 * Gets the declaring class for this class
	 * 
	 * @return the class that reflects this class
	 * @throws ClassNotFoundException
	 */
	public Class getDeclaringClass() throws ClassNotFoundException {
		return Class.forName(this.getName());
	}

}
