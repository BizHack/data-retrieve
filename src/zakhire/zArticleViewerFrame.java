/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zakhire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import zakhire.zArticleViewerFrame;

@SuppressWarnings("serial")
public final class zArticleViewerFrame extends JDialog{
    JButton next = new JButton();
    JButton prev = new JButton();
    JButton back = new JButton();
    zTextArea textArea = new zTextArea();
    int currentArticle;
    int currentPage;
    JLabel fractionLabel = new JLabel();
    JLabel articleNumber = new JLabel();
    Paginator pg = new Paginator();

    public zArticleViewerFrame(int article,int page) throws FileNotFoundException, IOException
    {
        setLayout(null);
        setModal(true);
        setSize(800,700);
        
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-800)/2,(Toolkit.getDefaultToolkit().getScreenSize().height-700)/2);

        currentArticle = article;
        currentPage = page;
        changePage(article, page);

        

        prev.setLocation(570,20);
        next.setLocation(670,20);
        back.setLocation(20,10);
        fractionLabel.setLocation(400,10);
        articleNumber.setLocation(150,10);
        textArea.setLocation(20,55);
        next.setSize(100,20);
        prev.setSize(100,20);
        back.setSize(100,40);
        fractionLabel.setSize(300,40);
        articleNumber.setSize(300,40);
        textArea.setSize(750,600);
        textArea.setMargin(new Insets(5, 5, 5, 5));
        next.setText("Next >");
        prev.setText("< Prev");
        back.setText("Back");
        fractionLabel.setForeground(Color.GRAY);
        fractionLabel.setFont(new Font(null, 0, 25));
        articleNumber.setForeground(Color.GRAY);
        articleNumber.setFont(new Font(null, 0, 25));
        textArea.setBackground(new Color(100000));
        
        add(textArea);
        add(prev);
        add(fractionLabel);
        add(next);
        add(articleNumber);
        add(back);
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pg.hasNextPage(currentArticle, currentPage))
                {
                    try {
                        changePage(currentArticle, currentPage + 1);
                    } catch (IOException ex) {
                        Logger.getLogger(zArticleViewerFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        prev.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (pg.hasPrevPage(currentArticle, currentPage))
                {
                    try {
                        changePage(currentArticle, currentPage - 1);
                    } catch (IOException ex) {
                        Logger.getLogger(zArticleViewerFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    public void changePage(int article,int page) throws IOException
    {
        currentArticle = article;
        currentPage = page;
        
        
        textArea.setText(pg.GetPage(article, page));
        updateFrame();
        
        
    }

    public void updateFrame()
    {
        next.setEnabled(pg.hasNextPage(currentArticle, currentPage));
        prev.setEnabled(pg.hasPrevPage(currentArticle, currentPage));
        fractionLabel.setText("Page: " + currentPage+"/"+pg.countPages(currentArticle));
        articleNumber.setText("Article: " + (currentArticle - 1));
        
        aliSafe();
    }
    
    public void aliSafe(){
    	Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.YELLOW);
    	if (DocTermsAndTermsDoc.query == null) return;
    	String pats[] = DocTermsAndTermsDoc.query.split(" ");
    	if (pats == null) return;
    	
    	Highlighter hilite = textArea.getHighlighter();
        Document doc = textArea.getDocument();
        
        String text;
		try {
			text = doc.getText(0, doc.getLength());
			text = text.toLowerCase();
			
			//System.out.println(text);
			// Search for pattern
			for (int i = 0; i < pats.length; i++) {
				int pos = 0;
				pats[i] = pats[i].toLowerCase();
				while ((pos = text.indexOf(pats[i], pos)) >= 0) {
					// Create highlighter using private painter and apply around pattern
					boolean sar, tah;
					sar = (pos == 0) || !isAlpha((text.charAt(pos - 1)));
					tah = (pos + pats[i].length() == text.length()) || !isAlpha(text.charAt(pos + pats[i].length()));
					if (sar && tah) 
						hilite.addHighlight(pos, pos + pats[i].length(), myHighlightPainter);
					
					pos += pats[i].length();
				}
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	    public MyHighlightPainter(Color color) {
	        super(color);
	    }
	    
	}
    
    public boolean isAlpha(char c){
    	return (c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a');
    }
}
