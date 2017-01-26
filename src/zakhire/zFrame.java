/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author user
 */
@SuppressWarnings({"serial" })
public class zFrame extends JFrame{
    public zFrame()
    {
        setLayout(null);
        setSize(800,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-800)/2,(Toolkit.getDefaultToolkit().getScreenSize().height-700)/2);
    }

}
