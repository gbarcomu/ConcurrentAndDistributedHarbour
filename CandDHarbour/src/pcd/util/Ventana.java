package pcd.util;
import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;


public class Ventana extends JFrame {
		JScrollPane scrollPane;
		JTextArea textArea;
		public Ventana(String title){
			BorderLayout bl = new BorderLayout();
			this.getContentPane().setLayout(bl);
			scrollPane = new JScrollPane();
			this.getContentPane().add(scrollPane, BorderLayout.CENTER);
			textArea = new JTextArea();
			textArea.setMargin( new Insets(7,7,7,7) );
			scrollPane.setViewportView(textArea);
			setSize(500, 400);
			setTitle(title);
			DefaultCaret caret = (DefaultCaret) textArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setVisible(true);
			
			
			
		}
        public void addText(String str) {
        	textArea.append("\n" + str);
        }
}