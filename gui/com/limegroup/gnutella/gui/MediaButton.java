/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(R). All rights reserved.
 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.limegroup.gnutella.gui;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * This class is really just a hack to make it easier to get the media player 
 * buttons to display correctly.
 * 
 * @author gubatron
 * @author aldenml
 */
public final class MediaButton extends JButton {

    private String tipText;
    private String upName;
    private String downName;

    public MediaButton(String tipText, String upName, String downName) {
        init(tipText,upName,downName);
    }
    
    public void init(String tipText, String upName, String downName) {
        this.tipText = tipText;
        this.upName = upName;
        this.downName = downName;

        setupUI();
    }

    private void setupUI() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setRolloverEnabled(true);
        if (upName != null) {
            setIcon(GUIMediator.getThemeImage(upName));
        }
        setHorizontalAlignment(SwingConstants.CENTER);
        if (downName != null) {
            setPressedIcon(GUIMediator.getThemeImage(downName));
        }
        //        setPreferredSize(new Dimension(
        //            getIcon().getIconWidth(), getIcon().getIconHeight()));
        setMargin(new Insets(0, 0, 0, 0));
        setToolTipText(tipText);
    }
}
