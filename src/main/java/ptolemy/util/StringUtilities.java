/* Utilities used to manipulate strings.

 Copyright (c) 2002-2014 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

 PT_COPYRIGHT_VERSION_2
 COPYRIGHTENDKEY

 */
package ptolemy.util;

// Note that classes in ptolemy.util do not depend on any
// other ptolemy packages.
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

///////////////////////////////////////////////////////////////////
//// StringUtilities

/**
 A collection of utilities for manipulating strings.
 These utilities do not depend on any other ptolemy packages.

 @author Christopher Brooks, Contributors: Teale Fristoe
 @version $Id: StringUtilities.java 70402 2014-10-23 00:52:20Z cxh $
 @since Ptolemy II 2.1
 @Pt.ProposedRating Green (eal)
 @Pt.AcceptedRating Green (cxh)
 */
public class StringUtilities {
    /** Instances of this class cannot be created.
     */
    private StringUtilities() {
    }

    /** If the ptolemy.ptII.exitAfterWrapup or the
     *  ptolemy.ptII.doNotExit properties are not set, then call
     *  System.exit().
     *  Ptolemy code should call this method instead of directly calling
     *  System.exit() so that we can test code that would usually exit.
     *  @param returnValue The return value of this process, where
     *  non-zero values indicate an error.
     */
    public static void exit(int returnValue) {
        if (!inApplet()) {
            // Only call System.exit if we are not in an applet.
            // Non-zero indicates a problem.
            System.exit(returnValue);
        }
    }

    /** Get the specified property from the environment. An empty
     *  string is returned if the property named by the "propertyName"
     *  argument environment variable does not exist, though if
     *  certain properties are not defined, then we make various
     *  attempts to determine them and then set them.  See the javadoc
     *  page for java.util.System.getProperties() for a list of system
     *  properties.

     *  <p>The following properties are handled specially
     *  <dl>
     *  <dt> "user.dir"
     *  <dd> Return the canonical path name to the current working
     *  directory.  This is necessary because under Windows with
     *  JDK1.4.1, the System.getProperty() call returns
     *  <code><b>c</b>:/<i>foo</i></code> whereas most of the other
     *  methods that operate on path names return
     *  <code><b>C</b>:/<i>foo</i></code>.
     *  </dl>
     *  @param propertyName The name of property.
     *  @return A String containing the string value of the property.
     *  If the property is not found, then we return the empty string.
     */
    public static String getProperty(String propertyName) {
        // NOTE: getProperty() will probably fail in applets, which
        // is why this is in a try block.
        String property = null;

        try {
            property = System.getProperty(propertyName);
        } catch (SecurityException ex) {
            SecurityException security = new SecurityException(
                    "Could not find '" + propertyName + "' System property");
            security.initCause(ex);
            throw security;
        }

        if (propertyName.equals("user.dir")) {
            try {
                if (property == null) {
                    return property;
                }
                File userDirFile = new File(property);
                return userDirFile.getCanonicalPath();
            } catch (IOException ex) {
                return property;
            }
        }
        
        return "";
    }

    /** Return true if we are in an applet.
     *  @return True if we are running in an applet.
     */
    public static boolean inApplet() {
        boolean inApplet = false;
        try {
            StringUtilities.getProperty("HOME");
        } catch (SecurityException ex) {
            inApplet = true;
        }
        return inApplet;
    }

    /** Replace all occurrences of <i>pattern</i> in the specified
     *  string with <i>replacement</i>.  Note that the pattern is NOT
     *  a regular expression, and that relative to the
     *  String.replaceAll() method in jdk1.4, this method is extremely
     *  slow.  This method does not work well with back slashes.
     *  @param string The string to edit.
     *  @param pattern The string to replace.
     *  @param replacement The string to replace it with.
     *  @return A new string with the specified replacements.
     */
    public static String substitute(String string, String pattern,
            String replacement) {
        if (string == null) {
            return null;
        }
        int start = string.indexOf(pattern);

        while (start != -1) {
            StringBuffer buffer = new StringBuffer(string);
            buffer.delete(start, start + pattern.length());
            buffer.insert(start, replacement);
            string = new String(buffer);
            start = string.indexOf(pattern, start + replacement.length());
        }

        return string;
    }
}
