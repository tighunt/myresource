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
 * <p>A basic\simple implementation of the <code>IniSection</code> 
 * interface.</p>
 * 
 * <p>This class implements only the abstract methods of 
 * <code>AbstractIniSection</code> and nothing else. Therefore, the performance
 * of this class is not as good as it could be. However the class stays simple
 * and provides a quick implementation for the <code>IniFile</code>
 * interface.</p>
 * 
 * @author David Lewis
 * @version 1.0.0
 * @since 0.1.15
 */
public class BasicIniSection extends IniSection {
    
    /**
     * A Map of all the items, referenced by the item's name
     */
    private List<IniItem> items;
    
    /**
     * <p>Default constructor which creates a new instance of this 
     * <code>IniSection</code> and sets the <code>IniSection</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @param name The name of this <code>IniSection</code>.
     * @throws InvalidNameException if the name of the <code>IniSection</code>
     * given is not considered valid by this object's <code>IniValidator</code>.
     */
    public BasicIniSection( String name ) {
        super(name);
        
        items = new ArrayList<IniItem>();
    }
    
    /**
     * <p>Default constructor which creates a new instance of this 
     * <code>IniSection</code> and sets the <code>IniSection</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @param name The name of this <code>IniSection</code>.
     * @param validator The validator of this <code>IniSection</code>.
     * @throws InvalidNameException if the name of the <code>IniSection</code>
     * given is not considered valid by this object's <code>IniValidator</code>.
     */
    public BasicIniSection(String name, IniValidator validator ) {
        super( name, validator );
        
        items = new ArrayList<IniItem>();
    }
    
    /**
     * <p>Default constructor which creates a new instance of this 
     * <code>IniSection</code> and sets the <code>IniSection</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @param name The name of this <code>IniSection</code>.
     * @param caseSensitive Sets whether this <code>IniSection</code> is case 
     * sensitive.
     * @throws InvalidNameException if the name of the <code>IniSection</code>
     * given is not considered valid by this object's <code>IniValidator</code>.
     */
    public BasicIniSection(String name, boolean caseSensitive ) {
        super( name, caseSensitive );
        
        items = new ArrayList<IniItem>();
    }
    
    /**
     * <p>Default constructor which creates a new instance of this 
     * <code>IniSection</code> and sets the <code>IniSection</code> to have a
     * default <code>IniValidator</code> and to be case insensitive.</p>
     * 
     * @param name The name of this <code>IniSection</code>.
     * @param validator The validator of this <code>IniSection</code>.
     * @param caseSensitive Sets whether this <code>IniSection</code> is case 
     * sensitive.
     * @throws InvalidNameException if the name of the <code>IniSection</code>
     * given is not considered valid by this object's <code>IniValidator</code>.
     */
    public BasicIniSection( String name, IniValidator validator,
                                                       boolean caseSensitive ) {
        
        super( name, validator, caseSensitive );
        
        items = new ArrayList<IniItem>();
    }

    @Override
    public boolean addItem(IniItem item, int index) {
        
        if( item == null ) {
            return false;
        }
        
        String itemName = item.getName();

        //**********************************************************************
        // check that item can be added
        //**********************************************************************
        if( !super.validator.isValidItemName(itemName) ) {
            throw new InvalidNameException( "The item's name \"" + itemName + 
                    "\" is invalid for this IniSection." );
        }
        else {
            if( !super.validator.equals(item.getValidator()) ) {
                
                throw new InvalidNameException( "The item's name, \"" + 
                        itemName + "\", is valid for this section, but the " +
                        "item has an IniValidator that is incompatible with " +
                        "this section's IniValidator." );
            }
        }
        
        //**********************************************************************
        
        //**********************************************************************
        if( this.hasItem(itemName) ) {
            return false;
        }
        else {
            items.add( index, item );
            return true;
        }
    }

    @Override
    protected IniItem createItem(String name) {
        return new IniItem( name, super.validator, isCaseSensitive() );
    }

    @Override
    public IniItem getItem(int index) {
        return items.get( index );
    }

    @Override
    public Collection<IniItem> getItems() {
        return new ArrayList<IniItem>( items );
    }

    @Override
    public int indexOf(IniItem item) {
        return items.indexOf(item);
    }

    @Override
    public boolean removeItem( IniItem item ) {
        if( hasItem(item) ) {
            items.remove( item );
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @since 1.0.00
     */
    @Override
    public Object clone() {
        
        IniSection clonedSection = new BasicIniSection( new String(getName()),
                (IniValidator) validator.clone(), isCaseSensitive() );
        
        clonedSection.setPreComment( new String(getPreComment()) );
        clonedSection.setPostComment( new String(getPostComment()) );
        clonedSection.setEndLineComment( new String(getEndLineComment()) );
        
        for( IniItem item : this.getItems() ) {
            clonedSection.addItem( (IniItem) item.clone() );
        }
        
        return clonedSection;
    }

    @Override
    public Iterator<IniItem> iterator() {
        return items.iterator();
    }
}
