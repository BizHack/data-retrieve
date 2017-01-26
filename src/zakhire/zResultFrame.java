/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author user
 */
@SuppressWarnings("serial")
class zResultFrame extends zFrame{
    JLabel wait = new JLabel("Please Wait...");
    JTable list = new JTable();
    JButton back = new JButton("Back");
    
    DefaultTableModel model;
    zArticleViewerFrame zavf;
    
    public static ExecuteQuery eq;
    
    private class myTableModel extends DefaultTableModel
    {
        @Override
        public boolean isCellEditable(int row,int column)
        {
            return false;
        }

    }

    public zResultFrame() throws IOException {
    	wait.setSize(200,50);
        wait.setLocation(30,30);
        
        add(wait);

        model = new myTableModel();
        
        model.addColumn("id");
        model.addColumn("Article Number");
        model.addColumn("Similarity");
        
        list = new JTable(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
        zavf = new zArticleViewerFrame(1, 1);
        zavf.setVisible(false);
        
        list.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && !e.isConsumed()) {

                    int sel = list.getSelectedRow();
                    if (sel != -1)
                    {
                        String t =  (String)list.getValueAt(sel, 1);
                        int articleId = Integer.valueOf(t);
                        try {
                        	zavf.changePage(articleId + 1, 1);
                        	zavf.setVisible(true);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(zResultFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(zResultFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                
            }

            public void mouseReleased(MouseEvent e) {
                
            }

            public void mouseEntered(MouseEvent e) {
                
            }

            public void mouseExited(MouseEvent e) {
                
            }
        });
        JScrollPane jsp = new JScrollPane(list);
        jsp.setSize(700,500);
        jsp.setLocation(50,100);

        add(jsp);
        
        
        back.setSize(100, 30);
        back.setLocation(50, 50);
        add(back);
        back.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				setVisible(false);
				Main.zqf.setVisible(true);
			}
		});
        
    }


    void startOperations() throws IOException {
        wait.setVisible(true);
        setTitle("Serch Results for : " + '\"' + zQueryFrame.queryField.getText() + '\"');
        DocTermsAndTermsDoc.makeQueryVector();
        
        for (int i = 0;i < DocTermsAndTermsDoc.queryVectorDat.length;++i)
        	DocTermsAndTermsDoc.queryVectorDat[i]--;
        
        DocTermsAndTermsDoc.searchResults = eq.executeQuery(DocTermsAndTermsDoc.queryVectorDat, DocTermsAndTermsDoc.queryVectorCnt);
        System.out.println("<<<<<<<<SEE " + DocTermsAndTermsDoc.searchResults.size() + " RESULTS>>>>>>>>");
		int temp = model.getRowCount();
		for(int i = 0;i < temp;++i)
			model.removeRow(0);
		
		
		for (int i=0;i<DocTermsAndTermsDoc.searchResults.size();i++)
            model.addRow(DocTermsAndTermsDoc.searchResults.get(i));

	    wait.setVisible(false);
    }
    

}
