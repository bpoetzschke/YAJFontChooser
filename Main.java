import java.awt.Font;

public class Main
{
	public static void main(String[] args)
	{
		Font preselected = new Font("Foo",Font.PLAIN, 43);
		Font font = YAJFontChooser.showDialog(preselected);
	}
}