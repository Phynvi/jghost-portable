package org.whired.ghost.net.reflection;

/**
* Represents a field; Used in conjunction with the reflection system
*
* @author Whired
*/
public class RMIField extends Accessor
{
	/**
	* Creates a new instance of RMIField
	*
	* @param name the RMIField
	* @param isStatic whether or not this RMIField is static
	*/
	protected RMIField(String name, boolean isStatic)
	{
		super(name, isStatic);
	}
}
