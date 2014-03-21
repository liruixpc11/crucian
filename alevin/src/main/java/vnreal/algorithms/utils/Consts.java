/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.algorithms.utils;

/**
 * Collected constants of general utility.
 * <p/>
 * <P>All members of this class are immutable.
 * <p/>
 * Class extracted from:
 * <p/>
 * http://www.javapractices.com/topic/TopicAction.do?Id=2
 * <p/>
 * Modified by:
 *
 * @author Juan Felipe Botero
 * @since 27-04-2011
 */
public final class Consts {

    /**
     * Useful for String operations, which return an index of <tt>-1</tt> when
     * an item is not found.
     */
    public static final int NOT_FOUND = -1;

    /**
     * System property - <tt>line.separator</tt>
     */
    public static final String NEW_LINE = System.getProperty("line.separator");
    /**
     * System property - <tt>file.separator</tt>
     */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /**
     * System property - <tt>path.separator</tt>
     */
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String TAB = "\t";
    public static final String SINGLE_QUOTE = "'";
    public static final String PERIOD = ".";
    public static final String DOUBLE_QUOTE = "\"";


    // LP solver files folder

    public static String LP_SOLVER_FOLDER = "ILP-LP-Models" + FILE_SEPARATOR;
    public static String LP_SOLVER_DATAFILE = "datafile";
    public static String LP_LINKMAPPING_MODEL = "VNE-Model.mod";
    public static String LP_LINKMAPPING_MODEL_HIDDENHOPS = "HHVNE-Model.mod";
    public static String LP_NODEMAPPING_MODEL = "VNE-Model-NodeMapping.mod";

    // PRIVATE //

    /**
     * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>,
     * and so on. Thus, the caller should be prevented from constructing objects of
     * this class, by declaring this private constructor.
     */
    private Consts() {
        //this prevents even the native class from
        //calling this actor as well :
        throw new AssertionError();
    }
}