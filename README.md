YAJFontChooser
==============

YAJFontChooser is an FontChooser for Java. With this font chooser it is possible that you can see the specific styles of the selected font, instead of the default styles Bold, Italic, Plain.

Usage
-----
First copy the YAJFontChooser.java file into your project directory.

**Example**

* Show the YAJFontChooser dialog with default method. The return value is an instance of the selected font or null if the user cancels the dialog with "ESC" key or "Cancel" button.  

**Note** The first chooseable font in the list is selected with first chooseable style and an font size of <code>12px</code>.
```java
Font font = YAJFontChooser.showDialog();
```
* Show the YAJFontChooser dialog with an preselected font. The return value is an instance of the font which the user had selected, if the user cancels with "ESC" key or "Cancel" button the return value will be <code>null</code>

**Note** If the given font is <code>null</code> the first chosseable font in the list is selected with first chooseable style and font size of <code>12px</code>. If the font size of the given font is less or equal than zero the default font size of 12px is used.
```java
//create an instance of the font which should be preselected
Font preselectedFont = new Font("HelveticaNeue-UltraLight", Font.PLAIN, 10);
Font selectedFont = YAJFontChooser.showDialog(preselectedFont);
```
See Main.java file for demo usage.

© 2014 Bjoern Poetzschke
