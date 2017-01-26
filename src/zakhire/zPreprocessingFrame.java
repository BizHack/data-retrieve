/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author user
 */
@SuppressWarnings("serial")
public class zPreprocessingFrame extends zFrame{
    JLabel Stages[];
    JLabel Done[];
    JLabel wait = new JLabel("Please Wait... It may take long time depends on your data file size.");
    JButton next = new JButton("Next >");

    public zPreprocessingFrame(String[] str) {
    	setTitle("Preprocessing Stage ...");
        wait.setLocation(60, 70);
        wait.setSize(800,20);
        add(wait);

        next.setLocation(600, 600);
        next.setSize(100,30);
        next.setEnabled(false);
        add(next);

        Done = new JLabel[str.length];
        Stages = new JLabel[str.length];
        int startY = 100;
        int startX = 100; // > 70
        int margin = 10;
        int width = 700;
        int height = 20;

        for (int i=0;i<str.length;i++)
        {
            Done[i] = new JLabel("[ - ]");
            Done[i].setLocation(startX - 30,startY + height*i + margin*i);
            Done[i].setSize(60,20);
            Done[i].setForeground(Color.gray);
            Stages[i] = new JLabel(str[i]);
            Stages[i].setLocation(startX,startY + height*i + margin*i);
            Stages[i].setSize(width,height);
            Stages[i].setForeground(Color.gray);
            add(Stages[i]);
            add(Done[i]);
        }

        next.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Main.zppf.setVisible(false);
                Main.zqf.setVisible(true);
            }
        });
    }

    public void started(int index)
    {
        Done[index].setText("[ > ]");
        Done[index].setForeground(Color.black);
        Stages[index].setForeground(Color.black);
    }
    
    public void done(int index)
    {
        Done[index].setText("[ * ]");
        Done[index].setForeground(Color.blue);
        Stages[index].setForeground(Color.blue);

        if (index == Stages.length - 1)
        {
            next.setEnabled(true);
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Main.zppf.setVisible(false);
            Main.zqf.setVisible(true);
        }
    }
    
    public void setString(int index){
    	Stages[index].setText("Clustering Stage 2 : Iterate " + Clustering.TRACES +" Times and Update each Cluster");
    }
}
