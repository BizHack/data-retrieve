/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
/**
 *
 * @author user
 */
@SuppressWarnings("serial")
public class zQueryFrame extends zFrame{
    JLabel logo = new JLabel("Welcome to Search Engine!");
    static JTextField queryField = new JTextField();
    JButton submit = new JButton("Search");

    public zQueryFrame() {
    	setTitle("Search Engine");
        logo.setSize(500,200);
        queryField.setSize(700,30);
        submit.setSize(200,30);
        logo.setFont(new Font(null, 0, 40));
        logo.setLocation(130, 100);
        queryField.setLocation(60, 250);
        submit.setLocation(290, 300);
        add(logo);
        add(queryField);
        add(submit);
        
        submit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	doQuery();
            }
        });
        
        queryField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					doQuery();
				}
			}
		});
    }
    
    public void doQuery(){
    	DocTermsAndTermsDoc.query = queryField.getText();
        Main.zqf.setVisible(false);
        Main.zrf.setVisible(true);
        try {
            Main.zrf.startOperations();
        } catch (IOException ex) {
            Logger.getLogger(zQueryFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
