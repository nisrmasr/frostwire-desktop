/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.utils.os
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.utils.os;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import javax.swing.filechooser.FileFilter;

//import net.miginfocom.swing.MigLayout;

import org.appwork.exceptions.WTFException;
import org.appwork.shutdown.ShutdownController;
import org.appwork.shutdown.ShutdownEvent;
import org.appwork.storage.JSonStorage;
import org.appwork.storage.StorageException;
import org.appwork.utils.Application;
import org.appwork.utils.locale._AWU;
import org.appwork.utils.logging.Log;
import org.appwork.utils.os.mime.Mime;
import org.appwork.utils.os.mime.MimeDefault;
import org.appwork.utils.os.mime.MimeLinux;
import org.appwork.utils.os.mime.MimeWindows;
//import org.appwork.utils.swing.dialog.Dialog;
//import org.appwork.utils.swing.dialog.DialogCanceledException;
//import org.appwork.utils.swing.dialog.DialogClosedException;
//import org.appwork.utils.swing.dialog.DialogNoAnswerException;

/**
 * This class provides a few native features.
 * 
 * @author $Author: unknown$
 */

public class CrossSystem {
    public static final byte    OS_LINUX_OTHER         = 6;
    public static final byte    OS_MAC_OTHER           = 5;
    public static final byte    OS_WINDOWS_OTHER       = 4;
    public static final byte    OS_WINDOWS_NT          = 3;
    public static final byte    OS_WINDOWS_2000        = 2;
    public static final byte    OS_WINDOWS_XP          = 0;
    public static final byte    OS_WINDOWS_2003        = 7;
    public static final byte    OS_WINDOWS_VISTA       = 1;
    public static final byte    OS_WINDOWS_7           = 8;
    public static final byte    OS_WINDOWS_SERVER_2008 = 9;

    private static Boolean      openURLSupport         = null;
    private static Boolean      openFILESupport        = null;
    private static String       JAVAINT                = null;

    /**
     * Cache to store the OS string in
     */
    private final static String OS_STRING;

    /**
     * Cache to store the OS ID in
     */
    private final static byte   OS_ID;

    /**
     * Cache to store the Mime Class in
     */
    private static final Mime   MIME;
    static {
        OS_STRING = System.getProperty("os.name");
        final String OS = CrossSystem.OS_STRING.toLowerCase();
        if (OS.contains("windows 7")) {
            OS_ID = CrossSystem.OS_WINDOWS_7;
        } else if (OS.contains("windows xp")) {
            OS_ID = CrossSystem.OS_WINDOWS_XP;
        } else if (OS.contains("windows vista")) {
            OS_ID = CrossSystem.OS_WINDOWS_VISTA;
        } else if (OS.contains("windows 2000")) {
            OS_ID = CrossSystem.OS_WINDOWS_2000;
        } else if (OS.contains("windows 2003")) {
            OS_ID = CrossSystem.OS_WINDOWS_2003;
        } else if (OS.contains("windows server 2008")) {
            OS_ID = CrossSystem.OS_WINDOWS_SERVER_2008;
        } else if (OS.contains("nt")) {
            OS_ID = CrossSystem.OS_WINDOWS_NT;
        } else if (OS.contains("windows")) {
            OS_ID = CrossSystem.OS_WINDOWS_OTHER;
        } else if (OS.contains("mac")) {
            OS_ID = CrossSystem.OS_MAC_OTHER;
        } else {
            OS_ID = CrossSystem.OS_LINUX_OTHER;
        }
        if (CrossSystem.isWindows()) {
            MIME = new MimeWindows();
        } else if (CrossSystem.isLinux()) {
            MIME = new MimeLinux();
        } else {
            MIME = new MimeDefault();
        }
    }

    private static boolean _isOpenBrowserSupported() {
        if (CrossSystem.isWindows()) { return true; }
        try {
            if (!Desktop.isDesktopSupported()) { return false; }
            final Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.BROWSE)) { return false; }
            return true;
        } catch (final Throwable e) {
            Log.exception(Level.WARNING, e);
        }
        return false;
    }

    private static boolean _isOpenFileSupported() {
        if (CrossSystem.isWindows()) { return true; }
        try {
            if (!Desktop.isDesktopSupported()) { return false; }
            final Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.OPEN)) { return false; }
            return true;
        } catch (final Throwable e) {
            Log.exception(Level.WARNING, e);
        }
        return false;
    }

    /**
     * internal function to open a file/folder
     * 
     * @param file
     * @throws IOException
     */
    private static void _openFILE(final File file) throws IOException {
        if (CrossSystem.isWindows()) {
            // workaround for windows
            // see http://bugs.sun.com/view_bug.do?bug_id=6599987
            Runtime.getRuntime().exec(new String[] { "rundll32.exe", "url.dll,FileProtocolHandler", file.getAbsolutePath() });
        } else {
            final Desktop desktop = Desktop.getDesktop();
            final URI uri = file.getCanonicalFile().toURI();
            desktop.open(new File(uri));
        }
    }

    /**
     * internal function to open an URL in a browser
     * 
     * @param _url
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void _openURL(final String _url) throws IOException, URISyntaxException {
        final URL url = new URL(_url);
        if (CrossSystem.isWindows()) {
            Runtime.getRuntime().exec(new String[] { "rundll32.exe", "url.dll,FileProtocolHandler", _url });
        } else {
            final Desktop desktop = Desktop.getDesktop();
            desktop.browse(url.toURI());
        }
    }

//    public static String[] getEditor(final String extension) throws DialogCanceledException, DialogClosedException, StorageException {
//        final File[] ret = Dialog.getInstance().showFileChooser("FILE_EDIT_CONTROLLER_" + extension, _AWU.T.fileditcontroller_geteditor_for(extension), Dialog.FileChooserSelectionMode.FILES_ONLY, new FileFilter() {
//
//            @Override
//            public boolean accept(final File f) {
//                if (f.isDirectory()) { return true; }
//                if (CrossSystem.isWindows()) {
//                    return f.getName().endsWith(".exe");
//                } else {
//                    return f.canExecute();
//                }
//
//            }
//
//            @Override
//            public String getDescription() {
//
//                return _AWU.T.fileeditcontroller_exechooser_description(extension);
//
//            }
//
//        }, false, Dialog.FileChooserType.OPEN_DIALOG_WITH_PRESELECTION, new File(JSonStorage.getPlainStorage("EDITORS").get(extension, "")));
//        if (ret != null && ret.length > 0) {
//            JSonStorage.getPlainStorage("EDITORS").put(extension, ret[0].toString());
//
//            return new String[] { ret[0].toString() };
//        } else {
//            return null;
//        }
//    }

    public static byte getID() {
        return CrossSystem.OS_ID;
    }

    public static String getJavaBinary() {
        if (CrossSystem.JAVAINT != null) { return CrossSystem.JAVAINT; }
        String javaBinary = "java";
        if (CrossSystem.isWindows()) {
            javaBinary = "javaw.exe";
        }
        final String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            /* get path from system property */
            final File java = new File(new File(javaHome), "/bin/" + javaBinary);
            if (java.exists() && java.isFile()) {
                CrossSystem.JAVAINT = java.getAbsolutePath();

            }
        } else {
            CrossSystem.JAVAINT = javaBinary;
        }
        return CrossSystem.JAVAINT;
    }

    /**
     * Returns the Mime Class for the current OS
     * 
     * @return
     * @see Mime
     */
    public static Mime getMime() {
        return CrossSystem.MIME;
    }

    public static String getOSString() {
        return CrossSystem.OS_STRING;
    }

    /**
     * Returns true if the OS is a linux system
     * 
     * @return
     */
    public static boolean isLinux() {
        return CrossSystem.OS_ID == CrossSystem.OS_LINUX_OTHER;
    }

    /**
     * Returns true if the OS is a MAC System
     * 
     * @return
     */
    public static boolean isMac() {
        return CrossSystem.OS_ID == CrossSystem.OS_MAC_OTHER;
    }

    /**
     * returns true in case of "open an URL in a browser" is supported
     * 
     * @return
     */
    public static boolean isOpenBrowserSupported() {
        if (CrossSystem.openURLSupport != null) { return CrossSystem.openURLSupport; }
        CrossSystem.openURLSupport = CrossSystem._isOpenBrowserSupported();
        return CrossSystem.openURLSupport;
    }

    /**
     * returns true in case of "open a File" is supported
     * 
     * @return
     */
    public static boolean isOpenFileSupported() {
        if (CrossSystem.openFILESupport != null) { return CrossSystem.openFILESupport; }
        CrossSystem.openFILESupport = CrossSystem._isOpenFileSupported();
        return CrossSystem.openFILESupport;
    }

    /**
     * Returns true if the OS is a Windows System
     * 
     * @return
     */
    public static boolean isWindows() {
        switch (CrossSystem.OS_ID) {
        case OS_WINDOWS_XP:
        case OS_WINDOWS_VISTA:
        case OS_WINDOWS_2000:
        case OS_WINDOWS_2003:
        case OS_WINDOWS_NT:
        case OS_WINDOWS_OTHER:
        case OS_WINDOWS_7:
        case OS_WINDOWS_SERVER_2008:
            return true;
        }
        return false;
    }

//    public static void main(final String[] args) {
//        restartApplication(MigLayout.class);
//    }

    /**
     * Opens a file or directory
     * 
     * @see java.awt.Desktop#open(File)
     * @param file
     */
    public static void openFile(final File file) {
        try {
            CrossSystem._openFILE(file);
        } catch (final Throwable e) {
            Log.exception(Level.WARNING, e);
        }
    }

    /**
     * Open an url in the systems default browser
     * 
     * @param url
     */
    public static void openURL(final String url) {
        try {
            CrossSystem._openURL(url);
        } catch (final Throwable e) {
            Log.exception(Level.WARNING, e);
        }
    }

    public static void openURL(final URL url) {
        CrossSystem.openURL(url.toString());
    }

    /**
     * @param update_dialog_news_button_url
     */
    public static void openURLOrShowMessage(final String urlString) {
//        try {
//            CrossSystem._openURL(urlString);
//        } catch (final Throwable e) {
//            Log.exception(Level.WARNING, e);
//            try {
//                Dialog.getInstance().showInputDialog(Dialog.BUTTONS_HIDE_CANCEL, _AWU.T.crossSystem_open_url_failed_msg(), urlString);
//            } catch (final DialogNoAnswerException donothing) {
//            }
//        }
    }

    /**
     * @param class1
     */
    public static void restartApplication(Class<?> class1, String... parameters) {

        try {
            ArrayList<String> nativeParameters = new ArrayList<String>();
            File runin = null;
            if (isMac()) {

                // find .app
                File rootpath = Application.getRootByClass(class1, null);
                HashSet<File> loopMap = new HashSet<File>();
                while (rootpath != null && loopMap.add(rootpath)) {
                    if (rootpath.getName().endsWith(".app")) {
                        break;

                    }
                    rootpath = rootpath.getParentFile();

                }
                if (rootpath.getName().endsWith(".app")) {

                    // found app.- restart it.

                    nativeParameters.add("open");
                    nativeParameters.add("-n");
                    nativeParameters.add(rootpath.getAbsolutePath());
                    runin = rootpath.getParentFile();

                }

            }
            if (nativeParameters.isEmpty()) {
                URL root = class1.getClassLoader().getResource(class1.getName().replace(".", "/") + ".class");
                
                //Filenames may contain ! !!
                int index = root.getPath().indexOf("!");
                if (index <= 0 || !"jar".equalsIgnoreCase(root.getProtocol())) {
                    throw new WTFException("REstart works only in Jared mode");
                } else {
                    File jarFile = new File(new URI(root.getPath().substring(0, index)));
                    runin = jarFile.getParentFile();
                    if (CrossSystem.isWindows()) {
                        File exeFile = new File(jarFile.getParentFile(), jarFile.getName().substring(0, jarFile.getName().length() - 4) + ".exe");
                        if (exeFile.exists()) {
                            nativeParameters.add(exeFile.getAbsolutePath());
                        } else {
                            nativeParameters.add(CrossSystem.getJavaBinary());
                            nativeParameters.add("-jar");
                            nativeParameters.add(jarFile.getAbsolutePath());
                        }
                    } else {
                        nativeParameters.add(CrossSystem.getJavaBinary());
                        nativeParameters.add("-jar");
                        nativeParameters.add(jarFile.getAbsolutePath());
                    }

                }
            }
            if (parameters != null) {
                for (String s : parameters) {
                    nativeParameters.add(s);
                }
            }

            final ProcessBuilder pb = new ProcessBuilder(nativeParameters.toArray(new String[] {}));
            /*
             * needed because the root is different for jre/class version
             */

            System.out.println("Root: " + runin);
            if (runin != null) pb.directory(runin);

            ShutdownController.getInstance().addShutdownEvent(new ShutdownEvent() {
                {
                    setHookPriority(Integer.MIN_VALUE);
                }

                @Override
                public void run() {
                    try {
                        pb.start();
                    } catch (final IOException e) {
                        Log.exception(e);
                    }

                }
            });

            ShutdownController.getInstance().requestShutdown();

        } catch (Throwable e) {
            throw new WTFException(e);
        }

    }
}