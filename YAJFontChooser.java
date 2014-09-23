/**
 * YAJFontChooser
 * Copyright (c) 2014, Bjoern Poetzschke<bjoern.poetzschke@gmail.com>, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Simple java font chooser which allows the user to select the font specific styles instead of the default
 * values (Bold, Italic, Plain).
 * To use the dialog just call YAJFontChooser.showDialog();
 * If you want to have an font preselected just call the showDialog with the Font as parameters
 * 
 * @author Bjoern Poetzschke <bjoern.poetzschke@gmail.com>
 *
 */
public class YAJFontChooser extends JDialog
{
	/**
	 * Shows the font chooser dialog. This function will wait until the user closes the dialog.
	 * The dialog will return an instance of the selected color if the user clicks the "OK" button,
	 * otherwise the dialog will return null. As default the first font is preselected.
	 * @return
	 * <ul>
	 * 	<li>Font - The font which the user had selected</li>
	 * 	<li>null - If the user cancels the dialog.
	 * </ul>
	 */
	public static Font showDialog()
	{
		return showDialog(null);
	}
	
	/**
	 * Shows the font chooser dialog and preselect the font which is given in the parameter.
	 * If the font in the parameter is null the first font of the chooseable fonts is preselected.
	 * This function will wait until the user closes the dialog.
	 * The dialog will return an instance of the selected color if the user clicks the "OK" button,
	 * otherwise the dialog will return null.
	 * @param _PreselectedFont - The font which should be preselected.<br />
	 * <strong>Note:</strong><br />
	 * <ul>
	 * 	<li>If the font is null the first font in the list will be preselected.</li>
	 * 	<li>If the size of the given font is less or equal than zero the default font size of 12px will be used</li>
	 * </ul>
	 * @return
	 * <ul>
	 * 	<li>Font - The font which the user had selected</li>
	 * 	<li>null - If the user cancels the dialog.
	 * </ul>
	 */
	public static Font showDialog(Font _PreselectedFont)
	{
		YAJFontChooser fontChooser = new YAJFontChooser(_PreselectedFont);
		fontChooser.setVisible(true);
		
		return fontChooser.getSelectedFont();
	}
	
	//-------------------------------
	// Private Variables
	//-------------------------------
	
	private static final long	serialVersionUID	= -3162469862533723830L;
	
	private JPanel						m_ButtonPanel			= null;
	private JButton 					m_OkButton				= null;
	private JButton 					m_CancelButton			= null;
	private JPanel 						m_FontSelectionPanel	= null;
	private Font						m_SelectedFont			= null;
	private JLabel						m_FontLabel				= null;
	private JTextField					m_FontFamilyTextField	= null;
	private JScrollPane					m_FontFamilyScrollPane	= null;
	private JList<FontFamilyModel>		m_FontFamilyList		= null;
	private HashMap<String, List<Font>>	m_FontMap				= null;
	private JLabel						m_FontStyleLabel		= null;
	private JTextField					m_FontStyleTextField	= null;
	private JScrollPane 				m_FontStyleScrollPane	= null;
	private JList<FontStyleSelection>	m_FontStyleList			= null;
	private JPanel 						m_PreviewPanel			= null;
	private JLabel 						m_PreviewLabel			= null;
	private JLabel						m_FontSizeLabel			= null;
	private JTextField					m_FontSizeTextField		= null;
	private JScrollPane					m_FontSizeScrollPane	= null;
	private JList<Integer>				m_FontSizeList			= null;
	private int							m_SelectedFontSize		= 12;
	private ComponentActionListener		m_ActionListener		= new ComponentActionListener();
	private SelectionListener			m_SelectionListener		= new SelectionListener();
	private KeyHandler					m_KeyListener			= new KeyHandler();
	
	private static int[] 				DEFAULT_FONT_SIZES		= {5,6,7,8,9,10,11,12,13,14,18,24,36,48,64,72,96};
	
	//--------------------------------------------
	// Private Methods
	// No documentation here because it is private
	//--------------------------------------------
	
	private YAJFontChooser(Font _PreselectedFont)
	{
		super();
		
		initUI();
		
		//required to wait for close of dialog
		setModal(true);
		
		initFonts();
		initFontSizeList();
		
		preSelectFont(_PreselectedFont);
	}
	
	private void initUI()
	{
		setSize(500,400);
		setPreferredSize(getSize());
		setResizable(false);
		setLocationRelativeTo(null);
		
		setTitle("Select Font");
		
		//setFocusable required to be set to true to repsond to key listener
		setFocusable(true);
		addKeyListener(m_KeyListener);
		
		m_ButtonPanel = new JPanel();
		getContentPane().add(m_ButtonPanel, BorderLayout.SOUTH);
		
		m_OkButton = new JButton("OK");
		m_OkButton.addActionListener(m_ActionListener);
		m_ButtonPanel.add(m_OkButton);
		
		m_CancelButton = new JButton("Cancel");
		m_ButtonPanel.add(m_CancelButton);
		m_CancelButton.addActionListener(m_ActionListener);
		
		m_FontSelectionPanel = new JPanel();
		getContentPane().add(m_FontSelectionPanel, BorderLayout.CENTER);
		m_FontSelectionPanel.setLayout(null);
		
		m_FontLabel = new JLabel("Font");
		m_FontLabel.setBounds(6, 6, 61, 16);
		m_FontSelectionPanel.add(m_FontLabel);
		
		m_FontFamilyTextField = new JTextField();
		m_FontFamilyTextField.setEditable(false);
		m_FontFamilyTextField.setHorizontalAlignment(SwingConstants.LEFT);
		m_FontFamilyTextField.setEnabled(false);
		m_FontFamilyTextField.setBounds(2, 24, 193, 20);
		m_FontSelectionPanel.add(m_FontFamilyTextField);
		m_FontFamilyTextField.setColumns(10);
		
		m_FontFamilyScrollPane = new JScrollPane();
		m_FontFamilyScrollPane.setBorder(null);
		m_FontFamilyScrollPane.setBounds(6, 51, 185, 175);
		m_FontSelectionPanel.add(m_FontFamilyScrollPane);
		
		m_FontFamilyList = new JList<FontFamilyModel>();
		m_FontFamilyList.setBorder(null);
		m_FontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_FontFamilyList.addListSelectionListener(m_SelectionListener);
		m_FontFamilyScrollPane.setViewportView(m_FontFamilyList);
		
		m_FontStyleLabel = new JLabel("Style");
		m_FontStyleLabel.setBounds(200, 6, 61, 16);
		m_FontSelectionPanel.add(m_FontStyleLabel);
		
		m_FontStyleTextField = new JTextField();
		m_FontStyleTextField.setEnabled(false);
		m_FontStyleTextField.setEditable(false);
		m_FontStyleTextField.setBounds(196, 24, 148, 20);
		m_FontSelectionPanel.add(m_FontStyleTextField);
		m_FontStyleTextField.setColumns(10);
		
		m_FontStyleScrollPane = new JScrollPane();
		m_FontStyleScrollPane.setBounds(200, 51, 140, 175);
		m_FontStyleScrollPane.setBorder(null);
		m_FontSelectionPanel.add(m_FontStyleScrollPane);
		
		m_FontStyleList = new JList<FontStyleSelection>();
		m_FontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_FontStyleList.setBorder(null);
		m_FontStyleList.addListSelectionListener(m_SelectionListener);
		m_FontStyleScrollPane.setViewportView(m_FontStyleList);
		
		m_FontSizeLabel = new JLabel("Size");
		m_FontSizeLabel.setBounds(349, 6, 61, 16);
		m_FontSelectionPanel.add(m_FontSizeLabel);
		
		m_FontSizeTextField = new JTextField();
		m_FontSizeTextField.setBounds(345, 24, 138, 20);
		m_FontSizeTextField.setColumns(10);
		m_FontSizeTextField.addActionListener(m_ActionListener);
		m_FontSelectionPanel.add(m_FontSizeTextField);
		
		m_PreviewPanel = new JPanel();
		m_PreviewPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		m_PreviewPanel.setBounds(6, 235, 488, 98);
		m_FontSelectionPanel.add(m_PreviewPanel);
		m_PreviewPanel.setLayout(new BorderLayout(0, 0));
		
		m_PreviewLabel = new JLabel("");
		m_PreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		m_PreviewPanel.add(m_PreviewLabel);
		
		m_FontSizeScrollPane = new JScrollPane();
		m_FontSizeScrollPane.setBounds(349, 51, 130, 175);
		m_FontSizeScrollPane.setBorder(null);
		m_FontSelectionPanel.add(m_FontSizeScrollPane);
		
		m_FontSizeList = new JList<Integer>();
		m_FontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_FontSizeList.setBorder(null);
		m_FontSizeList.addListSelectionListener(m_SelectionListener);
		m_FontSizeScrollPane.setViewportView(m_FontSizeList);
	}
	
	private void initFonts()
	{
		m_FontMap = new HashMap<String, List<Font>>();
		
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		Font[] fonts = graphicsEnvironment.getAllFonts();
		
		for(Font font : fonts)
		{
			//check if hasmap entry exists
			String fontFamily = font.getFamily();
			
			List<Font> fontFamilyList = m_FontMap.get(fontFamily);
			
			//create new list if there is no one for the font family
			if(fontFamilyList == null)
			{
				fontFamilyList = new ArrayList<Font>();
				m_FontMap.put(fontFamily, fontFamilyList);
			}
			
			fontFamilyList.add(font);
		}
		
		String[] fontFamilyNames = graphicsEnvironment.getAvailableFontFamilyNames();
		
		DefaultListModel<FontFamilyModel> fontFamilyListModel = new DefaultListModel<FontFamilyModel>();
		for(String fontFamily : fontFamilyNames)
		{
			fontFamilyListModel.addElement(new FontFamilyModel(fontFamily));
		}
		
		m_FontFamilyList.setModel(fontFamilyListModel);
		
		m_FontFamilyList.setSelectedIndex(0);
	}
	
	private void initFontSizeList()
	{
		DefaultListModel<Integer> sizeListModel = new DefaultListModel<Integer>();
		
		for(int sizeIndex = 0; sizeIndex < DEFAULT_FONT_SIZES.length; sizeIndex++)
		{
			sizeListModel.addElement(new Integer(DEFAULT_FONT_SIZES[sizeIndex]));
		}
		
		m_FontSizeList.setModel(sizeListModel);
		m_FontSizeList.setSelectedIndex(7);
	}
	
	private void preSelectFont(Font _PreselectedFont)
	{
		if(_PreselectedFont != null)
		{
			//select font family 
			//m_FontFamilyList.setSelectedValue(_PreselectedFont.getFamily(), true);
			ListModel<FontFamilyModel> listModel = m_FontFamilyList.getModel();
			
			for(int familyIndex = 0; familyIndex < listModel.getSize(); familyIndex++)
			{
				if(listModel.getElementAt(familyIndex).getFontFamily().equals(_PreselectedFont.getFamily()))
				{
					m_FontFamilyList.setSelectedValue(listModel.getElementAt(familyIndex), true);
					break;
				}
			}
			
			//check if font family is realy selected
			if(m_FontFamilyList.getSelectedValue().getFontFamily().equals(_PreselectedFont.getFamily()))
			{
				ListModel<FontStyleSelection> fontStyleListModel = m_FontStyleList.getModel();
				boolean styleFound = false;
				
				for(int fontStyleIndex = 0; fontStyleIndex < fontStyleListModel.getSize(); fontStyleIndex++)
				{
					FontStyleSelection fontStyle = fontStyleListModel.getElementAt(fontStyleIndex);
					
					if(fontStyle.getSelection().getFontName().equals(_PreselectedFont.getFontName()))
					{
						m_FontStyleList.setSelectedValue(fontStyle, true);
						styleFound = true;
						
						break;
					}
				}
				
				if(!styleFound)
				{
					m_FontStyleList.setSelectedIndex(0);
				}
			}
			
			if(_PreselectedFont.getSize() >= 1)
			{
				m_SelectedFontSize = _PreselectedFont.getSize();
			}
			
			m_FontSizeTextField.setText(Integer.toString(m_SelectedFontSize));
			m_FontSizeTextField.setCaretPosition(m_FontSizeTextField.getText().length());
			
			m_FontSizeList.clearSelection();
			
			//search if font size is in list
			for(int fontSizeIndex = 0; fontSizeIndex < DEFAULT_FONT_SIZES.length; fontSizeIndex++)
			{
				if(m_SelectedFontSize == DEFAULT_FONT_SIZES[fontSizeIndex])
				{
					m_FontSizeList.setSelectedIndex(fontSizeIndex);
					m_FontSizeList.ensureIndexIsVisible(fontSizeIndex);
					break;
				}
			}
			
			loadFont(_PreselectedFont.getFontName(), _PreselectedFont.getStyle(), m_SelectedFontSize);
		}
	}
	
	private void loadFontsForFamily(String _FontFamily)
	{
		DefaultListModel<FontStyleSelection> familyListModel = new DefaultListModel<FontStyleSelection>();
		
		List<Font> familyFonts = m_FontMap.get(_FontFamily);
		
		for(Font font : familyFonts)
		{
			familyListModel.addElement(new FontStyleSelection(font));
		}
		
		m_FontStyleList.setModel(familyListModel);
		m_FontStyleList.setSelectedIndex(0);
	}
	
	private Font getSelectedFont()
	{
		return m_SelectedFont;
	}
	
	private void loadFont(String _FontName, int _FontStyle, int _FontSize)
	{
		m_SelectedFont = new Font(_FontName, _FontStyle, _FontSize);
		m_PreviewLabel.setFont(m_SelectedFont);
	}
	
	//----------------------------------------------------------------------------
	// Internal classes for event handling
	//----------------------------------------------------------------------------
	
	private class SelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent _Event)
		{
			if(_Event.getSource().equals(m_FontFamilyList))
			{
				m_FontFamilyTextField.setText(m_FontFamilyList.getSelectedValue().toString());
				m_FontFamilyTextField.setCaretPosition(0);
				
				loadFontsForFamily(m_FontFamilyList.getSelectedValue().getFontFamily());
			}
			else if(_Event.getSource().equals(m_FontStyleList))
			{
				FontStyleSelection styleSelection = m_FontStyleList.getSelectedValue();
				if(styleSelection != null)
				{
					m_FontStyleTextField.setText(styleSelection.toString());
					m_FontStyleTextField.setCaretPosition(0);
				
					loadFont(styleSelection.getSelection().getFontName(), styleSelection.getSelection().getStyle(), m_SelectedFontSize);
					
					m_PreviewLabel.setText(styleSelection.toString());
				}
			}
			else if(_Event.getSource().equals(m_FontSizeList))
			{
				FontStyleSelection styleSelection = m_FontStyleList.getSelectedValue();
				if(styleSelection != null)
				{
					if(m_FontSizeList.getSelectedValue() != null)
					{
						m_FontSizeTextField.setText(m_FontSizeList.getSelectedValue().toString());
						m_FontSizeTextField.setCaretPosition(m_FontSizeTextField.getText().length());
						m_SelectedFontSize = m_FontSizeList.getSelectedValue().intValue();
						
						loadFont(styleSelection.getSelection().getFontName(), styleSelection.getSelection().getStyle(), m_SelectedFontSize);
					}
				}
			}
		}
	}
	
	private class ComponentActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _Event)
		{
			if(_Event.getSource().equals(m_CancelButton))
			{
				setVisible(false);
				m_SelectedFont = null;
			}
			else if(_Event.getSource().equals(m_OkButton))
			{
				setVisible(false);
			}
			else if(_Event.getSource().equals(m_FontSizeTextField))
			{
				requestFocus();
				
				int fontSize = Integer.parseInt(m_FontSizeTextField.getText());
				
				if(fontSize <= 0)
				{
					fontSize = 1;
				}
				
				if(fontSize >= 1)
				{
					int possibleSizeIndex = -1;
					
					for(int sizeIndex = 0; sizeIndex < DEFAULT_FONT_SIZES.length; sizeIndex++)
					{
						if(fontSize == DEFAULT_FONT_SIZES[sizeIndex])
						{
							possibleSizeIndex = sizeIndex;
						}
					}
					
					if(possibleSizeIndex > -1)
					{
						m_FontSizeList.setSelectedIndex(possibleSizeIndex);
						m_FontSizeList.ensureIndexIsVisible(possibleSizeIndex);
					}
					else
					{
						m_FontSizeList.clearSelection();
						
						m_SelectedFontSize = fontSize;
						
						FontStyleSelection styleSelection = m_FontStyleList.getSelectedValue();
						if(styleSelection != null)
						{
							loadFont(styleSelection.getSelection().getFontName(), styleSelection.getSelection().getStyle(), m_SelectedFontSize);
						}
					}
				}
			}
		}
	}
	
	private class FontStyleSelection
	{
		public FontStyleSelection(Font _Font)
		{
			m_Selection = _Font;
			m_FontText  = parseFontStyle();
		}
		
		public String parseFontStyle()
		{
			//split font name as '-' character if existing
			String[] fontNameParts = m_Selection.getFontName().split("-");
			
			//clean up possible mt and ms chars
			for(int partIndex = 0; partIndex < fontNameParts.length; partIndex++)
			{
				fontNameParts[partIndex] = cleanupFontString(fontNameParts[partIndex]);
			}
			
			String toSplit = "";
			
			//font name did not contains an '-' character, use first part for further usage
			if(fontNameParts.length == 1)
			{
				toSplit = fontNameParts[0];
			}
			//font name did contains an '-' char, used second part for further usage
			else if(fontNameParts.length == 2)
			{
				toSplit = fontNameParts[1];
			}
			else if(fontNameParts.length >= 3)
			{
				return fontNameParts[fontNameParts.length - 1];
			}
			
			//remove MT from font style name
			toSplit = cleanupFontString(toSplit);
			
			//see http://stackoverflow.com/a/17512351 for splitting string at uppercase
			String[] parts = toSplit.split("(?<=\\p{Ll})(?=\\p{Lu})");
			String fontText = "";
			
			for(int partIndex = 0; partIndex < parts.length; partIndex++)
			{
				fontText += parts[partIndex];
				
				if(partIndex < (parts.length - 1))
				{
					fontText +=" ";
				}
			}
			
			//check if style name is equal to font family name
			if(fontText.equals(m_Selection.getFamily()))
			{
				return "Regular";
			}
			
			//replace possible occurrences of font family name
			String fontFamily = cleanupFontString(m_Selection.getFamily());

			fontFamily = fontFamily.replace("Lt", "Light");
			fontFamily = fontFamily.replace("Cn", "Condensed");
			fontFamily = fontFamily.replace("Bk", "Black");
			fontFamily = fontFamily.replace("Th", "Thin");
			
			if(fontFamily.charAt(fontFamily.length() - 1) == ' ')
			{
				fontFamily = fontFamily.substring(0, fontFamily.length() - 1);
			}
			
			fontText = fontText.replace(fontFamily, "");
			
			if(fontText.equals(""))
			{
				return "Regular";
			}
			
			//split up font family if possible
			String familyParts[] = m_FontFamilyList.getSelectedValue().getFontFamily().split("(?<=\\p{Ll})(?=\\p{Lu})");
			if(familyParts.length >= 1)
			{
				String family = "";
				for(int index = 0; index < familyParts.length; index++)
				{
					family += familyParts[index];
					
					if(index < familyParts.length - 1)
					{
						family += " ";
					}
				}
				
				fontText = fontText.replace(family, "");
			}
			
			if(fontText.length() == 0)
			{
				return "Regular";
			}
			
			/*if(fontText.equals(fontFamily))
			{
				return "Regular";
			}*/
			
			//font name is part of family name return default value
			String cleanedFamily = m_FontFamilyList.getSelectedValue().toString().replace(" ","");
			cleanedFamily = cleanedFamily.replace("LET", "Let");
			
			//check if font family contains the whole font name part, if yes return "Regular"
			if( cleanedFamily.contains(fontText)
				|| m_FontFamilyList.getSelectedValue().toString().contains(fontText)
				|| m_FontFamilyList.getSelectedValue().toString().contains(fontText.replaceAll(" ","")))
			{
				return "Regular";
			}
			
			fontText = fontText.replace(m_FontFamilyList.getSelectedValue().toString().replaceAll("LET", "Let"), "");
			
			//remove leading white space
			if(fontText.charAt(0) == ' ')
			{
				fontText = fontText.substring(1);
			}
			
			return fontText;
		}
		
		@Override
		public String toString()
		{
			return m_FontText;
		}
		
		public Font getSelection()
		{
			return m_Selection;
		}
		
		private Font	m_Selection = null;
		private String	m_FontText  = "";
	}
	
	private class FontFamilyModel
	{
		public FontFamilyModel(String _FontFamily)
		{
			m_FontFamily = _FontFamily;
		}
		
		public String getFontFamily()
		{
			return m_FontFamily;
		}
		
		@Override
		public String toString()
		{
			String retVal = m_FontFamily;
			retVal = retVal.replaceAll("_", " ");
			retVal = retVal.replaceAll("  ", " ");
			
			if(retVal.charAt(0) == ' ')
			{
				retVal = retVal.substring(1);
			}
			
			if(retVal.equals("Gothic"))
			{
				System.out.println(m_FontFamily);
			}
			
			return retVal;
		}
		
		private String m_FontFamily = "";
	}
	
	private String cleanupFontString(String _FontString)
	{
		_FontString = _FontString.replaceAll("MT", "" );
		_FontString = _FontString.replaceAll("  ", " ");
		_FontString = _FontString.replaceAll("_" , " ");
		
		return _FontString;
	}
	
	private class KeyHandler implements KeyListener
	{
		@Override
		public void keyTyped(KeyEvent _Event)
		{	
		}

		@Override
		public void keyPressed(KeyEvent _Event)
		{
			if(_Event.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				m_SelectedFont = null;
				setVisible(false);
			}
		}

		@Override
		public void keyReleased(KeyEvent _Event)
		{
		}
		
	}
}
