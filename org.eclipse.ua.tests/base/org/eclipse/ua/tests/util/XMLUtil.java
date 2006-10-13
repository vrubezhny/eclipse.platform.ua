/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ua.tests.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A utility class for working with XML.
 */
public class XMLUtil extends Assert {
	
	public static void assertXMLEquals(String msg, String s1, String s2) throws Exception {
		InputStream in1 = new ByteArrayInputStream(s1.getBytes("UTF-8"));
		InputStream in2 = new ByteArrayInputStream(s2.getBytes("UTF-8"));
		assertXMLEquals(msg, in1, in2);
	}
	
	public static void assertXMLEquals(String msg, InputStream in1, InputStream in2) throws Exception {
		String s1 = process(in1);
		String s2 = process(in2);
		assertEquals(msg, s1, s2);
	}
	
	private static String process(InputStream in) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		Handler handler = new Handler();
		parser.parse(in, handler);
		return handler.toString();
	}
	
	private static class Handler extends DefaultHandler {
		
		private StringBuffer buf = new StringBuffer();
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			buf.append('<');
			buf.append(qName);
			
			List list = new ArrayList();
			for (int i=0;i<attributes.getLength();++i) {
				list.add(attributes.getQName(i));
			}
			Collections.sort(list);
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				String name = (String)iter.next();
				buf.append(' ');
				buf.append(name);
				buf.append('=');
				buf.append('"');
				buf.append(attributes.getValue(name));
				buf.append('"');
			}
			buf.append('>');
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String qName) throws SAXException {
			buf.append('<');
			buf.append('/');
			buf.append(qName);
			buf.append('>');
		}		
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		public void characters(char[] ch, int start, int length) throws SAXException {
			buf.append(ch, start, length);
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
			return new InputSource(new StringReader("")); //$NON-NLS-1$
		}
		
		public String toString() {
			return buf.toString();
		}
	}
}
