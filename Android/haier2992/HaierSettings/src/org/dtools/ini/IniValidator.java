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

import java.util.regex.Pattern;

/**
 * <p>An IniValidator provides <code>IniFile</code>s, <code>IniSection</code>s,
 * and <code>IniItems</code> a way of validating a name for an
 * <code>IniSection</code> or <code>IniItem</code> which is performed by the
 * methods:</p>
 * 
 * <ul>
 *   <li><code>boolean <strong>isValidItemName</strong>(String)</li></code>
 *   <li><code>boolean <strong>isValidSectionName</strong>(String)</li></code>
 * </ul>
 * 
 * <p>This class also allows users to define their own definitions of valid 
 * names through the use of regular expressions and the object
 * <code>java.util.regex.<strong>Pattern</strong></code> being passed to one
 * of the following methods:</p>
 * 
 * <ul>
 *   <li><code>boolean <strong>setItemNameRegEx</strong>(Pattern)</li></code>
 *   <li><code>boolean <strong>setSectionNameRegEx</strong>(Pattern)</li></code>
 * </ul>
 * 
 * @author David Lewis
 * @version 1.0.0
 * @since 0.1.12
 */
public class IniValidator implements Cloneable {
    
    /**
     * The regular expression pattern for IniItem names
     * 
     * @since 0.1.12
     */
    private Pattern itemNameRegEx;
    
    /**
     * The regular expression pattern for IniSection names
     * 
     * @since 0.1.12
     */
    private Pattern sectionNameRegEx;
    
    /**
     * Default constructor, creates a new IniValidator
     * 
     * @since 0.1.12
     */
    public IniValidator() {
        
        String validChars = "A-Za-z0-9_!£$%^&*()+\\-{}'#@~<>,.|¨`¶È…˙⁄ÌÕÛ”·¡°" +
                "¢≤≈√À«ﬂÊ·‚Â£§•ß©Æ±∞≥¿¡¬ƒ∆»… ÃÕŒœ–—“”‘’÷◊ÿŸ⁄€‹›ﬁ‡„‰ÁËÈÍÎÏÌÓÔ" +
                "ÒÚÛÙıˆ˜¯˘˙˚¸˝˛ˇ";
        
        String regex = "(^[" + validChars + "]" +
                         "[" + validChars + " ]*" +   // notice that this includes a space character
                         "[" + validChars + "]$)" +
                      "|(^[" + validChars + "]$)";
        
        setItemNameRegEx( Pattern.compile(regex) );
        setSectionNameRegEx( Pattern.compile(regex) );
    }
    
    /**
     * Predicate that returns true if the given name is valid for a IniItem.
     * 
     * @param name The name of an IniItem.
     * @return True if the given name is valid, false otherwise.
     * @since 0.1.12
     */
    public boolean isValidItemName( String name ) {
        return itemNameRegEx.matcher( name ).matches();
    }
    
    /**
     * Predicate that returns true is the given name is valid for a IniSection.
     * 
     * @param name The name of the IniSection.
     * @return True if the given name is valid, false otherwise.
     * @since 0.1.12
     */
    public boolean isValidSectionName( String name ) {
        return sectionNameRegEx.matcher( name ).matches();
    }
    
    /**
     * Sets the regular Expression pattern for a valid IniItem name. The Pattern
     * will be used by the <code>isValidItemName(String)</code> method to
     * validate names.
     * 
     * @param itemNameRegEx the regular expression pattern for an item's name
     * @since 0.1.12
     */
    public void setItemNameRegEx( Pattern itemNameRegEx ) {
        this.itemNameRegEx = itemNameRegEx;
    }
    
    /**
     * Sets the regular Expression pattern for a valid IniSection's name. The
     * Pattern will be used by the <code>isValidSectioname(String)</code> method
     * to validate names.
     * 
     * @param sectionNameRegEx the regular expression pattern for a section's
     * name
     * @since 0.1.12
     */
    public void setSectionNameRegEx( Pattern sectionNameRegEx ) {
        this.sectionNameRegEx = sectionNameRegEx;
    }
    
    /**
     * Predicate that returns true if another <code>IniValidator</code> is equal
     * to this <code>IniValidator</code>. Two <code>IniValidator</code>s are
     * considered equal if they both have the same regular expression used for
     * for validating items names and section names.
     * 
     * @param other The other object to compare with
     * @return <strong>boolean</strong>, true if the other object is equal to
     * this one, false otherwise.
     * @since 0.1.16
     */
    @Override
    public boolean equals( Object other ) {
        
        if( !(other instanceof IniValidator) ) {
            return false;
        }
        else {
            
            IniValidator otherV = (IniValidator) other;
            
            String thisItemPattern = itemNameRegEx.pattern();
            String otherItemPattern = otherV.itemNameRegEx.pattern();
            
            String thisSectionPattern = sectionNameRegEx.pattern();
            String otherSectionPattern = otherV.sectionNameRegEx.pattern();
            
            return thisItemPattern.equals(otherItemPattern) &&
                   thisSectionPattern.equals(otherSectionPattern);
        }
    }
    
    /**
     * This overrides and implements Object's hashCode method, and produces a
     * hash code for this object.
     * 
     * @return a hash code which represents this object.
     * @since 0.3.01
     */
    @Override
    public int hashCode() {

        String sectionRegEx = sectionNameRegEx.pattern();
        String itemRegEx = itemNameRegEx.pattern();
        
        return sectionRegEx.hashCode()^2 + itemRegEx.hashCode();
    }
    
    /**
     * The method provides cloning features to the IniValidator class.
     * 
     * @return a new IniValidator object which is a clone of the current object.
     * @since 1.0.00
     */
    @Override
    public Object clone() {
        
        IniValidator cloneValidator = new IniValidator();
        
        cloneValidator.setItemNameRegEx( this.itemNameRegEx );
        cloneValidator.setSectionNameRegEx( this.sectionNameRegEx );
        
        return cloneValidator;
    }
}
