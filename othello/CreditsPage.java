package othello;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class CreditsPage extends JDialog implements Runnable {

   public int size = 1200;
   int posY = size;
   
   JTextArea jtaCredits = new JTextArea("   Project Manager\r\n" + "\t           SangHo Kim\r\n" + "\r\n" + "\r\n" + "\r\n"
         + "   GamePanel\r\n" + "\t      Seongjae Kang\r\n" + "\r\n" + "\r\n" + "\r\n" + "   LoginPanel\r\n" + "\t      Minjoong Yoon\r\n"
         + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "   ChatPanel\r\n" + "\t       Dongik Jeong\r\n" + "\r\n" + "\r\n" + "\r\n"
         + "\r\n" + "\r\n" + "   Special thanks to...\r\n\n\n\n" + "\t     Acorn's everyone");

   Font f = new Font("San Serif", Font.PLAIN, 30); 
   
   CreditsPage() {
      
      setLayout(null);
      setTitle("Credits");
      setBounds(100, 100, 500, 800);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      getContentPane().setBackground(Color.BLACK);
      setVisible(true);
      
      this.add(jtaCredits);
      jtaCredits.setFont(f);
      jtaCredits.setBackground(Color.black);
      jtaCredits.setForeground(Color.white);

   }

   @Override
   public void run() {

      try {
         while (true) {
            posY--;
            jtaCredits.setBounds(0, posY-200, 500, size);
            Thread.sleep(10);
            if(posY<=-size) posY=size;
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) {
      Thread th = new Thread(new CreditsPage());
      th.start();
   }
   
}// EO CreditsPage