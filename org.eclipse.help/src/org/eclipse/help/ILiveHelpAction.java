/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help;
/**
 * Live Help Extension.  Classes that implement this interface
 * can be used as live help extensions.
 * When user clicks live help link on the help document,
 * the class will be loaded and run.
 * @since 2.0
 */
public interface ILiveHelpAction extends Runnable {
	/**
	 * This method will be called upon instantiation of the
	 * live help extension.  The data will be passed as specified
	 * in the help document live help link.
	 * @param data - initialization data as a String
	 */
	public void setInitializationString(String data);
}
