package org.whired.ghost.net.reflection;

import java.util.ArrayList;

/**
* Represents an Object; Used in conjunction with the reflection system.
*
* @author Whired
*/
public class Accessor implements java.io.Serializable
{
	/** The name of the Accessor */
	private final String name;

	/** The name of the type of the Accessor */
	private final String typeName;
	
	/** The class that contains this Accessor */
	private final String declaringClass;

	/** Whether or not the Accessor is static */
	private final boolean isStatic;

	/** The description of the Object this Accessor represents */
	private final String description;
	
	public Accessor(String name, String typeName, String declaringClass, boolean isStatic, String description)
	{
		this.name = name;
		this.typeName = typeName.equals("int") ? "java.lang.Integer" : typeName.equals("boolean") ? "java.lang.Boolean" : typeName; // TODO add all prims
		this.declaringClass = declaringClass;
		this.isStatic = isStatic;
		this.description = description;
	}

	/**
	* Gets the name of this Accessor
	*
	* @return the name of this Accessor
	*/
	public String getName()
	{
		return this.name;
	}

	/**
	* Gets the declaring class for this Accessor
	*
	* @return the <code>Class</code> that contains this Accessor
	*/
	public Class getDeclaringClass() throws ClassNotFoundException
	{
		return Class.forName(this.declaringClass);
	}

	/**
	* Gets the name of the declaring class for this Accessor
	*
	* @return the name of the class that contains this Accessor
	*/
	public String getDeclaringClassName()
	{
		return this.declaringClass;
	}

	/**
	* Specifies whether or not this Accessor is static
	*
	* @return true if this Accessor is static, otherwise false
	*/
	public boolean isStatic()
	{
		return this.isStatic;
	}

	/**
	* Checks to see if this Accessor is a ghost.network.reflection.Field
	*
	* @return true if the Accessor is a ghost.network.reflection.Field, otherwise false.
	*/
	public boolean isField()
	{
		return this instanceof org.whired.ghost.net.reflection.Field;
	}
	
	/**
	* Checks to see if this Accessor is a ghost.network.reflection.Method
	*
	* @return true if the Accessor is a ghost.network.reflection.Method, otherwise false.
	*/
	public boolean isMethod()
	{
		return this instanceof org.whired.ghost.net.reflection.Method;
	}

	/**
	* Gets the type of this Accessor
	*
	* @return the name of the type this Accessor represents
	*/
	public String getType()
	{
		return this.typeName;
	}

	/**
	* Gets the String that represents this Accessor
	*
	* @return the formatted String that represents this Accessor
	*/
	@Override
	public String toString()
	{
		return this.description;
	}

	/**
	* Gets a list of Accessors within a given class
	*
	* @throws ClassNotFoundException if the given class could not be found
	*
	* @param top the name of the class to search in
	* @param staticOnly whether or not to search only static Accessors
	*
	* @return the list of Accessors that were found
	*/
	public static ArrayList<Accessor> getAccessors(String top, boolean staticOnly) throws ClassNotFoundException
	{
		Class c = Class.forName(top);
		ArrayList<Accessor> accessorList = new ArrayList<Accessor>();
  		java.lang.reflect.Method[] methods = c.getMethods();
  		java.lang.reflect.Field[] fields = c.getFields();
  		for(java.lang.reflect.Field field : fields)
  		{
  			if(!staticOnly || java.lang.reflect.Modifier.isStatic(field.getModifiers()))
  			{
  				accessorList.add(new Field(field.getName(), field.getType().getName(), top, staticOnly, field.toString()));
			}
  		}
  		for(java.lang.reflect.Method method : methods)
  		{
  			if(!staticOnly || java.lang.reflect.Modifier.isStatic(method.getModifiers()))
  			{
  				accessorList.add(new Method(method.getName(), method.getReturnType().getName(),top, staticOnly, method.toString(), method.getParameterTypes()));
			}
		}
  		return accessorList;
	}

	private Object currentObject = null;

	public void setCurrentObject(Object currentObject)
	{
		this.currentObject = currentObject;
	}

	public Object getCurrentObject()
	{
		return this.currentObject;
	}
}

