import java.awt.Font;

public class Main
{
	public static void main(String[] args)
	{
		Font preselected = new Font("HelveticaNeue-UltraLight",Font.PLAIN, 43);
		Font font = YAJFontChooser.showDialog(preselected);
	}
}