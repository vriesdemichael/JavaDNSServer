import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorDialog {

  public ErrorDialog() 
	  {
		    String message = "Yo trippin? That aint no good input bro!";
		    JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
		        JOptionPane.ERROR_MESSAGE);
		  
  }
}