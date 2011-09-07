package org.whired.ghost.net.reflection;

import java.util.ArrayList;

public class AccessorInvoker
{
	private Object curObj = null;
	private Object trunk = null;
	public Object invokeAccessor(ArrayList<Accessor> accessors) throws java.lang.reflect.InvocationTargetException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException,
															java.lang.IllegalAccessException, java.lang.NoSuchFieldException
	{
		for(Accessor a : accessors)
		{
			// Is a a method?
			if(a.isMethod())
			{
				// Cast as method
				org.whired.ghost.net.reflection.Method m = (org.whired.ghost.net.reflection.Method)a;
				//System.out.println("DEBUG: a ("+m.toString()+") is method");

				// Does m have arguments?
				trunk = curObj;
				if(m.getArguments().size() > 0) // yes
				{
					//System.out.println("DEBUG: m ("+m.toString()+") has "+m.getArguments().size()+" argument chains");
					// Iterate through argument chains
					for(java.util.ArrayList<Accessor> o : m.getArguments())
					{
						curObj = new AccessorInvoker().invokeAccessor(o);
						m.addArgumentValue(curObj);
						//System.out.println("DEBUG: Adding "+curObj.toString()+" as an argument for "+m.toString());
					}
				}
				//System.out.println("DEBUG: "+m.getName()+" from "+curObj);
				try
				{
					curObj = m.getDeclaringClass().getMethod(m.getName(), m.getParameters()).invoke(trunk, m.getArgumentValues());
				}
				catch(java.lang.reflect.InvocationTargetException e)
				{
					throw e;
				}
			}
			else // a is field
			{
				org.whired.ghost.net.reflection.Field f = (org.whired.ghost.net.reflection.Field)a;
				//System.out.println("DEBUG: "+f.getName()+" from "+curObj);
				curObj = f.getDeclaringClass().getField(f.getName()).get(curObj);
			}
		}
		return curObj;
	}
}
