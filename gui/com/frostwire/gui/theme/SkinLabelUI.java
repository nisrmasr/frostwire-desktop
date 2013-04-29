/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.theme;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.synth.SynthLabelUI;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class SkinLabelUI extends SynthLabelUI {

    public static ComponentUI createUI(JComponent comp) {
        return new SkinLabelUI();
    }

    private Font oldFont;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        if (c instanceof JLabel) {
            oldFont = ThemeMediator.fixLabelFont((JLabel) c);
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        if (oldFont != null && c instanceof JLabel) {
            c.setFont(oldFont);
        }
    }
}
