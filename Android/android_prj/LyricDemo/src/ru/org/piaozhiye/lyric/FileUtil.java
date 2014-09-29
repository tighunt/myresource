/*
 * FileUtil.
 * 
 * JavaZOOM : jlgui@javazoom.net
 *            http://www.javazoom.net 
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package ru.org.piaozhiye.lyric;

import java.util.Arrays;

/**
 * @author Scott Pennell
 */
@SuppressWarnings("unchecked")
public class FileUtil {

	public static String padString(String s, int length) {
		return padString(s, ' ', length);
	}

	public static String padString(String s, char padChar, int length) {
		int slen, numPads = 0;
		if (s == null) {
			s = "";
			numPads = length;
		} else if ((slen = s.length()) > length) {
			s = s.substring(0, length);
		} else if (slen < length) {
			numPads = length - slen;
		}
		if (numPads == 0) {
			return s;
		}
		char[] c = new char[numPads];
		Arrays.fill(c, padChar);
		return s + new String(c);
	}

	public static String rightPadString(String s, int length) {
		return (rightPadString(s, ' ', length));
	}

	public static String rightPadString(String s, char padChar, int length) {
		int slen, numPads = 0;
		if (s == null) {
			s = "";
			numPads = length;
		} else if ((slen = s.length()) > length) {
			s = s.substring(length);
		} else if (slen < length) {
			numPads = length - slen;
		}
		if (numPads == 0) {
			return (s);
		}
		char[] c = new char[numPads];
		Arrays.fill(c, padChar);
		return new String(c) + s;
	}
}
