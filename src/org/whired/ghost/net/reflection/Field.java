package org.whired.ghost.net.reflection;

/**
* Represents a field; Used in conjunction with the reflecton system
*
* @author Whired
*/
public class Field extends Accessor
{
	/**
	* Creates a new instance of Field
	*
	* @param name the Field
	* @param isStatic whether or not this Field is static
	*/
	public Field(String name, String typeName, String declaringClass, boolean isStatic, String description)
	{
		super(name, typeName, declaringClass, isStatic, description);
	}
}
