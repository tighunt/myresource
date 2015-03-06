/*
 * Java INI Package
 * Copyright (C) 2008 David Lewis
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public Licence as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public Licence for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dtools.ini;

/**
 * <p>The <code>InvalidNameException</code> is a <code>RuntimeException</code>
 * which is thrown if a user tries to add an <code>IniSection</code> to an
 * <code>IniFile</code> and the <code>IniSection</code>'s name is not considered
 * valid by the <code>IniFile</code>. This <code>RuntimeException</code> is also
 * thrown when an <code>IniItem</code> with an invalid name is added to a
 * <code>IniSection</code>.</p>
 * 
 * @author David Lewis
 * @since 0.1.12
 * @version 0.1.20
 */
public class InvalidNameException extends RuntimeException {

    /**
     * The serial version of this class.
     *
     * @since 0.1.20
     */
    private static final long serialVersionUID = -661380686122047923L;

    /**
     * <p>Default construct, creates a new instance of 
     * <code>InvalidNameException</code> with the vague message "<em>The name
     * given is invalid.</em>".</p>
     * 
     * @since 0.1.12
     */
    public InvalidNameException() {
        this( "The name given is invalid." );
    }
    
    /**
     * <p>Constructor which creates a new instance of 
     * <code>InvalidNameException</code> with the given message.
     * 
     * @param message The String message of the exception.
     * @since 0.1.12
     */
    public InvalidNameException( String message ) {
        super( message );
    }
}
