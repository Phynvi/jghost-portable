/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.whired.ghostclient;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author whired
 */
public class Updater
{
	public static void main(String[] args) throws URISyntaxException, IOException
	{
		// Check for update

		// Download JAR if applicable

		// Run
		String pathToJar = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		String jarName = pathToJar.substring(pathToJar.lastIndexOf("/ghost")+1, pathToJar.length());
		System.out.println("Jar name:" + jarName);

		ProcessBuilder pb = new ProcessBuilder("java", "-classpath", pathToJar, "org.whired.Main");
		pb.start();
	}
}
