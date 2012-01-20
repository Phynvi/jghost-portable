package org.whired.ghost.net.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Represents an Object; Used in conjunction with the reflection system.
 * 
 * @author Whired
 */
public abstract class Accessor {

	/**
	 * The name of the accessor
	 */
	private final String name;
	/**
	 * The class that contains this accessor
	 */
	protected String declaringClass;
	/**
	 * Whether or not the accessor is static
	 */
	private final boolean isStatic;

	protected Accessor(String name, boolean isStatic) {
		this.name = name;
		this.isStatic = isStatic;
	}

	public RMIField getField(String name) {
		RMIField f = new RMIField(name, instruction.size() == 1);
		f.declaringClass = this.name;
		f.instruction.addAll(instruction);
		f.instruction.add(f);
		return f;
	}

	/**
	 * Gets a method by name and parameter signature
	 * 
	 * @param name the name of the method
	 * @param params the
	 * @return
	 */
	public RMIMethod getMethod(String name, Object... params) {
		RMIMethod m = new RMIMethod(name, instruction.size() == 1, params);
		m.declaringClass = this.name;
		m.instruction.addAll(instruction);
		m.instruction.add(m);
		return m;
	}

	/**
	 * Gets a class by its name
	 * 
	 * @param name the name of the class to get
	 * @return the class as an accessor
	 */
	public static RMIClass getClass(String name) {
		RMIClass c = new RMIClass(name);
		c.instruction.add(c);
		return c;
	}

	protected LinkedList<Accessor> instruction = new LinkedList<Accessor>();

	/**
	 * Gets the name of this accessor
	 * 
	 * @return the name of this accessor
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the name of the declaring class for this Accessor
	 * 
	 * @return the name of the class that contains this Accessor
	 */
	public String getDeclaringClassName() {
		return this.declaringClass;
	}

	/**
	 * Specifies whether or not this accessor is static
	 * 
	 * @return {@code true} if this accessor is static, otherwise {@code false}
	 */
	public boolean isStatic() {
		return this.isStatic;
	}

	public boolean isClass() {
		return this instanceof RMIClass;
	}

	public RMIClass asClass() {
		if (this.isClass())
			return (RMIClass) this;
		else
			throw new ClassCastException(this.getName() + " is not a " + RMIClass.class.getName());
	}

	/**
	 * Checks to see if this accessor is a {@link org.whired.ghost.net.reflection.RMIField}
	 * 
	 * @return {@code true} if the accessor is a {@code RMIField}, otherwise {@code false}
	 */
	public boolean isField() {
		return this instanceof RMIField;
	}

	/**
	 * Gets this accessor as a field; used for quick chaining
	 * 
	 * @return the field representation if it is a field
	 */
	public RMIField asField() {
		if (this.isField())
			return (RMIField) this;
		else
			throw new ClassCastException(this.getName() + " is not a " + RMIField.class.getName());
	}

	/**
	 * Checks to see if this accessor is a {@link org.whired.ghost.net.reflection.RMIMethod}
	 * 
	 * @return {@code true} if the accessor is a {@code RMIMethod}, otherwise {@code false}
	 */
	public boolean isMethod() {
		return this instanceof org.whired.ghost.net.reflection.RMIMethod;
	}

	/**
	 * Gets this accessor as a method; used for quick chaining
	 * 
	 * @return the method representation if it is a field
	 */
	public RMIMethod asMethod() {
		if (this.isMethod())
			return (org.whired.ghost.net.reflection.RMIMethod) this;
		else
			throw new ClassCastException(this.getName() + " is not a " + RMIMethod.class.getName());
	}

	/**
	 * Gets the String that represents this accessor
	 * 
	 * @return the formatted String that represents this accessor
	 */
	@Override
	public String toString() {
		return this.name;
	}

	public Object invoke() throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
		Object curObj = null;
		if (instruction.size() > 1) {
			Class<?> cls;
			Accessor a = instruction.get(0);
			if (a.isClass())
				cls = a.asClass().getDeclaringClass();
			else
				throw new ClassCastException("First instruction must be a " + RMIClass.class.getName());
			for (int i = 1; i < instruction.size(); i++) {
				a = instruction.get(i);
				if (a.isField()) {
					Field f = cls.getField(a.name);
					cls = f.getType();
					curObj = f.get(curObj);
				}
				else {
					Method m = cls.getMethod(a.name, a.asMethod().getParameters());
					cls = m.getReturnType();
					curObj = m.invoke(curObj, a.asMethod().getArgumentValues());
				}
			}
		}
		else
			throw new InvocationTargetException(new RuntimeException("Instructions not complete"));
		return curObj;
	}
}
