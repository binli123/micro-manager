/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

import java.util.Collections;
import java.util.Comparator;
import org.micromanager.data.Coords;

public class SortedCoordsList extends java.util.ArrayList<Coords> {
    /**
     * Takes in a Coords iterable from Datastore, sorts it,
     * and turns it into a list.
     */
    public SortedCoordsList() {
        // Empty constructor, create an empty list.
        super();
    }
    
    public SortedCoordsList(java.lang.Iterable<Coords> iter) {
        super();
        // Just add the unordered coords into the list
        for (Coords item: iter) {
            this.add(item);
        }
        // Sort the list using the CoordsComparator
        Collections.sort(this, SortedCoordsList.CoordsComparator);
    }
    
    private static final Comparator<Coords> CoordsComparator = new Comparator<Coords>() {
        // Compare the String outputs of Coords.toString method.
        @Override
        public int compare(Coords o1, Coords o2) {
            return o1.toString().compareTo(o2.toString());
        }
    };
}
