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

import java.util.*;

/**
 * <p>This implementation of the <code>IniFile</code> interface offers faster
 * performance compared with the <code>BasicIniFile</code> implementation, but
 * at the expense of greater memory usage.</p>
 * 
 * @author David Lewis
 * @version 1.1.0
 * @since 0.2.00
 */
public class AdvancedIniFile extends IniFile {
    
    /**
     * A Map of all the sections within this IniFile, with the IniSections
     * indexed by their names.
     * 
     * @since 0.1.27
     */
    private Map<String,IniSection> sections;
    
    /**
     * A List of the order that the sections will be outputted.
     * 
     * @since 0.1.27
     */
    private List<IniSection> sectionOrder;


    /**
     * <p>Default constructor which creates a new instance of
     * <code>BasicIniFile</code> and sets the <code>IniFile</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @since 0.1.15
     */
    public AdvancedIniFile() {
        super();
        sections = new TreeMap<String,IniSection>();
        sectionOrder = new ArrayList<IniSection>();
    }

    /**
     * <p>Default constructor which creates a new instance of
     * <code>BasicIniFile</code> and sets the <code>IniFile</code> to have a
     * default <code>IniValidator</code>.</p>
     * 
     * @param caseSensitive Sets whether this instance of <code>IniFile</code>
     * is case sensitive or not. 
     * 
     * @since 0.1.15
     */
    public AdvancedIniFile( boolean caseSensitive ) {
        super( caseSensitive );
        sections = new TreeMap<String,IniSection>();
        sectionOrder = new ArrayList<IniSection>();
    }

    /**
     * <p>Default constructor which creates a new instance of
     * <code>BasicIniFile</code> and sets the <code>IniFile</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @param validator Sets the <code>IniValidator</code> if this instance of
     * <code>IniFile</code>.
     * 
     * @since 0.1.15
     */
    public AdvancedIniFile( IniValidator validator ) {
        super( validator );
        sections = new TreeMap<String,IniSection>();
        sectionOrder = new ArrayList<IniSection>();
    }

    /**
     * <p>Default constructor which creates a new instance of
     * <code>BasicIniFile</code> and sets the <code>IniFile</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @param caseSensitive Sets whether this instance of <code>IniFile</code>
     * is case sensitive or not. 
     * @param validator Sets the <code>IniValidator</code> if this instance of
     * <code>IniFile</code>.
     * 
     * @since 0.1.15
     */
    public AdvancedIniFile( IniValidator validator, boolean caseSensitive ) {
        super( validator, caseSensitive );
        sections = new TreeMap<String,IniSection>();
        sectionOrder = new ArrayList<IniSection>();
    }
    
    @Override
    public boolean addSection(IniSection section, int index) {
        
        if( section == null ) {
            return false;
        }
        
        String sectionName = section.getName();

        //**********************************************************************
        // check that section is compatible
        //**********************************************************************
        if( !super.validator.isValidSectionName(sectionName) ) {
            throw new InvalidNameException( "The IniSection given does not " +
                    "have a valid name for this IniFile. IniFile is unable to" +
                    "add this Section" );
        }
        
        if( !this.validator.equals(section.getValidator()) ) {
            return false;
        }
        
        if( this.isCaseSensitive() != section.isCaseSensitive() ) {
            return false;
        }

        //**********************************************************************
        // add section
        //**********************************************************************
        
        // add section only if it doesn't already exists
        if( hasSection(section) ) {
            return false;
        }
        else {
            sections.put( sectionName, section );
            sectionOrder.add( index, section );
            return true;
        }
    }

    @Override
    protected IniSection createSection(String name) {
        return new AdvancedIniSection(
                name, getValidator(), isCaseSensitive() );
    }

    @Override
    public IniSection getSection(int index) {
        return sectionOrder.get( index );
    }

    @Override
    public Collection<IniSection> getSections() {
        return new ArrayList<IniSection>( sectionOrder );
    }

    @Override
    public int indexOf(IniSection section) {
        return sectionOrder.indexOf( section );
    }
    
    @Override
    public boolean removeSection( IniSection section ) {
        
        if( hasSection(section) ) {
            sections.remove( section.getName() );
            sectionOrder.remove( section );
            return true;
        }
        else {
            return false;
        }
        
    }
    
    @Override
    public Object clone() {
        return new AdvancedIniFile();
    }

    @Override
    public Iterator<IniSection> iterator() {
        return sectionOrder.iterator();
    }
}
