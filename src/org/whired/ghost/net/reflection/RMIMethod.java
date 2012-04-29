package org.whired.ghost.net.reflection;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents a method; Used in conjunction with the reflection system
 * @author Whired
 */
public class RMIMethod extends Accessor {

	/**
	 * Creates a new instance of RMIMethod
	 * @param name the name of the RMIMethod
	 * @param isStatic whether or not the RMIMethod is static
	 * @param args the arguments of the RMIMethod (Must be in order!)
	 */
	protected RMIMethod(final String name, final boolean isStatic, final Object... params) {
		super(name, isStatic);
		if (params != null) {
			this.paramTypeNames = new String[params.length];
			this.argValues = new Object[params.length];
			for (int i = 0; i < this.paramTypeNames.length; i++) {
				this.argValues[i] = params[i];
				this.paramTypeNames[i] = params[i].getClass().getName();
			}
		}
	}

	/** The names of the types of parameters of the RMIMethod, in order */
	private String[] paramTypeNames;

	/** The arguments to be used while invoking this method, in order */
	private Object[] argValues = new Object[0];

	protected Object[] getArgumentValues() {
		return this.argValues;
	}

	/**
	 * Gets the parameter types of this RMIMethod
	 * @return the types, in order
	 */
	protected Class<?>[] getParameters() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
		Class<?>[] params = null;
		if (this.paramTypeNames != null) {
			params = new Class[paramTypeNames.length];
			for (int i = 0; i < params.length; i++) {
				System.out.println(this.paramTypeNames[i]);
				if (this.paramTypeNames[i].equals(Integer.class.getName())) {
					params[i] = Integer.TYPE;
					System.out.println("Warning: Unboxed " + Integer.class.getName() + " to " + Integer.TYPE);
				}
				else if (this.paramTypeNames[i].equals("boolean")) {
					params[i] = Boolean.TYPE;
				}
				else {
					params[i] = Class.forName(this.paramTypeNames[i]);
					if (Accessor.class.isAssignableFrom(params[i])) {
						final Object o = ((Accessor) argValues[i]).invoke();
						this.argValues[i] = o;
						this.paramTypeNames[i] = o.getClass().getName();
						params[i] = Class.forName(this.paramTypeNames[i]);
					}
				}
			}
		}
		return params;
	}
}
