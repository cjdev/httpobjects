/**
 * Copyright (C) 2011, 2012 Commission Junction Inc.
 *
 * This file is part of httpobjects.
 *
 * httpobjects is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * httpobjects is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with httpobjects; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.httpobjects.header.request;

import org.junit.Assert;

import org.junit.Test;

public class CookieFieldTest {
	
	@Test
	public void multipleCookies(){
		// GIVEN:
		String aValueWithMultipleCookies = "JSESSIONID=abcFzcwh5FefNdVW1cVlt; Authorization=YW5nZWxpbkBhcHBsZS5jb206";
		
		// WHEN:
		CookieField field = new CookieField(aValueWithMultipleCookies);
		
		// THEN:
		Assert.assertEquals(2, field.cookies().size());
		Assert.assertEquals("JSESSIONID", field.cookies().get(0).name);
		Assert.assertEquals("abcFzcwh5FefNdVW1cVlt", field.cookies().get(0).value);
		Assert.assertEquals("Authorization", field.cookies().get(1).name);
		Assert.assertEquals("YW5nZWxpbkBhcHBsZS5jb206", field.cookies().get(1).value);
		
	}
	
	@Test
	public void singleCookie(){
		// GIVEN:
		String aValueWithMultipleCookies = "JSESSIONID=abcFzcwh5FefNdVW1cVlt";
		
		// WHEN:
		CookieField field = new CookieField(aValueWithMultipleCookies);
		
		// THEN:
		Assert.assertEquals(1, field.cookies().size());
		Assert.assertEquals("JSESSIONID", field.cookies().get(0).name);
		Assert.assertEquals("abcFzcwh5FefNdVW1cVlt", field.cookies().get(0).value);
		
	}
	
	@Test
	public void singleCookieWithPathAndDomain(){
		// GIVEN:
		String aValueWithMultipleCookies = "JSESSIONID=abcFzcwh5FefNdVW1cVlt;$Path=/path/to/file;$Domain=abc.def.com";
		
		// WHEN:
		CookieField field = new CookieField(aValueWithMultipleCookies);
		
		// THEN:
		Assert.assertEquals(1, field.cookies().size());
		Assert.assertEquals("JSESSIONID", field.cookies().get(0).name);
		Assert.assertEquals("abcFzcwh5FefNdVW1cVlt", field.cookies().get(0).value);
		Assert.assertEquals("/path/to/file", field.cookies().get(0).path);
		Assert.assertEquals("abc.def.com", field.cookies().get(0).domain);
		
	}
	
	@Test
	public void multipleCookiesWithOnePathAndDomain(){
		// GIVEN:
		String aValueWithMultipleCookies = "JSESSIONID=abcFzcwh5FefNdVW1cVlt;$Path=/path/to/file;$Domain=abc.def.com;Authorization=YW5nZWxpbkBhcHBsZS5jb206";
		
		// WHEN:
		CookieField field = new CookieField(aValueWithMultipleCookies);
		
		// THEN:
		Assert.assertEquals(2, field.cookies().size());
		Assert.assertEquals("JSESSIONID", field.cookies().get(0).name);
		Assert.assertEquals("abcFzcwh5FefNdVW1cVlt", field.cookies().get(0).value);
		Assert.assertEquals("/path/to/file", field.cookies().get(0).path);
		Assert.assertEquals("abc.def.com", field.cookies().get(0).domain);
		
		Assert.assertEquals("Authorization", field.cookies().get(1).name);
		Assert.assertEquals("YW5nZWxpbkBhcHBsZS5jb206", field.cookies().get(1).value);
		
	}
	
	
	@Test
	public void valueWithOneCookieAndExtraParams(){
		// GIVEN:
		CookieField field = new CookieField(new Cookie("haircolor", "brown", "/some/path", "xyz.com"));
		
		// WHEN:
		String text = field.value();
		
		// THEN:
		Assert.assertTrue(text.equals("haircolor=brown;$Path=/some/path;$Domain=xyz.com") || text.equals("haircolor=brown;$Domain=xyz.com;$Path=/some/path"));
	}
	
	@Test
	public void valueWithOneCookie(){
		// GIVEN:
		CookieField field = new CookieField(new Cookie("haircolor", "brown"));
		
		// WHEN:
		String text = field.value();
		
		// THEN:
		Assert.assertEquals("haircolor=brown", text);
	}
	
	@Test
	public void valueWithTwoCookies(){
		// GIVEN:
		CookieField field = new CookieField(new Cookie("haircolor", "brown"), new Cookie("height", "10ft"));
		
		// WHEN:
		String text = field.value();
		
		// THEN:
		Assert.assertEquals("haircolor=brown;height=10ft", text);
	}

	@Test
	public void nameOnly(){
		// GIVEN:
		String cookieHeader = "__test; name=Bob";

		// WHEN:
		CookieField field = new CookieField(cookieHeader);

		// THEN:
		Assert.assertNotNull(field.cookies().get(0));
		Assert.assertEquals("__test", field.cookies().get(0).name);
		Assert.assertNull(field.cookies().get(0).value);
		Assert.assertEquals("Bob", field.cookies().get(1).value);
	}

}
