package org.whired.ghost.net.reflection;

import java.util.ArrayList;
import java.util.Arrays;

/**
* Represents a method; Used in conjunction with the reflecton system
*
* @author Whired
*/
public class Method extends Accessor
{
	/**
	* Creates a new instance of Method
	*
	* @param name the name of the Method
	* @param isStatic whether or not the Method is static
	* @param args the arguments of the Method (Must be in order!)
	*/
	public Method(String name, String typeName, String declaringClass, boolean isStatic, String description, Class[] params)
	{
		super(name, typeName, declaringClass, isStatic, description);
		if(params != null)
		{
			this.paramTypeNames = new String[params.length];
			for(int i = 0; i < this.paramTypeNames.length; i++)
			{
				this.paramTypeNames[i] = params[i].getName();
			}
		}
	}

	/** The names of the types of parameters of the Method, in order */
	private String[] paramTypeNames;

	private ArrayList<ArrayList<Accessor>> args = new ArrayList<ArrayList<Accessor>>();
	
	/** The arguments to be used while invoking this method, in order */
	private Object[] argValues = new Object[0];

	/**
	* Adds an argument to the end of the argument list
	*
	* @param o the Object to add as an argument
	*/
	public void addArgument(ArrayList<Accessor> o)
	{
		this.args.add(o);
	}

	public void addArgumentValue(Object o)
	{
		ArrayList<Object> l = new ArrayList<Object>(Arrays.asList(argValues));
		l.add(o);
		this.argValues = l.toArray();
	}

	/**
	* Gets the arguments used for this Method
	*
	* @return the Objects that were added as arguments, in order
	*/
	public ArrayList<ArrayList<Accessor>> getArguments()
	{
		return this.args;
	}

	public Object[] getArgumentValues()
	{
		return this.argValues;
	}

	/**
	* Gets the parameter types of this Method
	*
	* @return the types, in order
	*/
	public Class[] getParameters() throws ClassNotFoundException
	{
		Class[] params = null;
		if(this.paramTypeNames != null)
		{
			params = new Class[paramTypeNames.length];
			for(int i = 0; i < params.length; i++)
			{
				if(this.paramTypeNames[i].equals("int")) params[i] = Integer.TYPE;
				else if(this.paramTypeNames[i].equals("boolean")) params[i] = Boolean.TYPE;
				else params[i] = Class.forName(this.paramTypeNames[i]);
				//params[i] = this.paramTypeNames.equals("int") ? Integer.TYPE : this.paramTypeNames[i].equals("boolean") ? Boolean.TYPE : Class.forName(this.paramTypeNames[i]);
			}
		}
		return params;
	}

	/**
	* Gets the names of the types of the parameters of this Method
	*
	* @return the typenames of this Method, in order
	*/
	public String[] getParamTypeNames()
	{
		return this.paramTypeNames;
	}
}
