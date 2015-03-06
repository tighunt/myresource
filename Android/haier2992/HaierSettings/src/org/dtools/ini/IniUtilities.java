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
 * <p>This class provides static methods to perform simple operations on 
 * <code>IniFile</code>s, <code>IniSection</code>s or <code>IniItem</code>s.
 * The class also provides access to global settings for the whole package, 
 * such as defining what an end line character(s) is.</p>
 * 
 * @author David Lewis
 * @version 0.1.20
 * @since 0.1.17
 */
public final class IniUtilities {
    
    /**
     * The new line character(s) that the ini classes uses.
     */
    public static final String NEW_LINE = "\r\n";
    
    /**
     * This predicate tests to see if the two given IniSections are disjoint,
     * that is, that they do not contain any Items with the same name.
     * 
     * @param s1 The first IniSection
     * @param s2 The second IniSection
     * @return boolean, true if the two sections have no items with the same
     * name, false otherwise.
     */
    public static boolean isDisjoint( IniSection s1, IniSection s2 ) {

        //**********************************************************************
        // check that s1 doesn't contain any elements of s2
        //**********************************************************************
        for( IniItem item : s2.getItems() ) {
            
            String itemName = item.getName();
            
            if( s1.hasItem(itemName) ) {
                return false;
            }
        }

        //**********************************************************************
        // check that s1 doesn't contain any elements of s1
        //**********************************************************************
        for( IniItem item : s1.getItems() ) {
            
            String itemName = item.getName();
            
            if( s2.hasItem(itemName) ) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * This predicate tests to see if the two given IniFiles are disjoint,
     * that is, that they do not contain any sections with the same name OR, if
     * the two IniFiles do contain sections with the same name, that those
     * sections are disjoint.
     * 
     * @param ini1 The first IniFile
     * @param ini2 The second IniFile
     * @return boolean, true if the two ini files have no sections with the 
     * same name, or true if the ini files have two sections with the same name
     * but no items within the sections with the same name. False if there is a
     * section or an item with the same name.
     */
    public static boolean isDisjoint( IniFile ini1, IniFile ini2 ) {

        //**********************************************************************
        // check that f1 doesn't contain any elements of f2
        //**********************************************************************
        for( IniSection s2 : ini2.getSections() ) {
            
            String sectionName = s2.getName();
            
            if( ini1.hasSection(sectionName) ) {
                // check that two sections are disjoint
                IniSection s1 = ini1.getSection(sectionName);
                
                if( !isDisjoint(s1, s2) ) {
                    return false;
                }
            }
        }

        //**********************************************************************
        // check that s1 doesn't contain any elements of s1
        //**********************************************************************
        for( IniSection s1 : ini1.getSections() ) {
            
            String sectionName = s1.getName();
            
            if( ini2.hasSection(sectionName) ) {
                // check that two sections are disjoint
                IniSection s2 = ini2.getSection(sectionName);
                
                if( !isDisjoint(s1, s2) ) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
}
