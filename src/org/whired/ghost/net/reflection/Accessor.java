package org.whired.ghost.net.reflection;

import java.util.ArrayList;

/**
* Represents an Object; Used in conjunction with the reflection system.
*
* @author Whired
*/
public class Accessor implements java.io.Serializable
{
	/** The name of the accessor */
	private final String name;

	/** The name of the type of the accessor */
	private final String typeName;

	/** The class that contains this accessor */
	private final String declaringClass;

	/** Whether or not the accessor is static */
	private final boolean isStatic;

	/** The description of the Object this accessor represents */
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
	* Gets the name of this accessor
	*
	* @return the name of this accessor
	*/
	public String getName()
	{
		return this.name;
	}

	/**
	* Gets the declaring class for this accessor
	*
	 * @return the <code>Class</code> that contains this accessor
	 * @throws ClassNotFoundException When this accessor cannot be cast to {@link org.whired.ghost.net.reflection.Accessor#getDeclaringClassName()}
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
	* Specifies whether or not this accessor is static
	*
	* @return {@code true} if this accessor is static, otherwise {@code false}
	*/
	public boolean isStatic()
	{
		return this.isStatic;
	}

	/**
	* Checks to see if this accessor is a {@link org.whired.ghost.net.reflection.Field}
	*
	* @return {@code true} if the accessor is a {@code Field}, otherwise {@code false}
	*/
	public boolean isField()
	{
		return this instanceof org.whired.ghost.net.reflection.Field;
	}

	/**
	* Checks to see if this accessor is a {@link org.whired.ghost.net.reflection.Method}
	*
	* @return {@code true} if the accessor is a {@code Method}, otherwise {@code false}
	*/
	public boolean isMethod()
	{
		return this instanceof org.whired.ghost.net.reflection.Method;
	}

	/**
	* Gets the type of this accessor
	*
	* @return the name of the type this accessor represents
	*/
	public String getType()
	{
		return this.typeName;
	}

	/**
	* Gets the String that represents this accessor
	*
	* @return the formatted String that represents this accessor
	*/
	@Override
	public String toString()
	{
		return this.description;
	}

	/**
	* Gets a list of accessors within a given class
	*
	* @throws ClassNotFoundException if the given class could not be found
	*
	* @param top the name of the class to search in
	* @param staticOnly whether or not to search only static accessors
	*
	* @return the list of accessors that were found
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

