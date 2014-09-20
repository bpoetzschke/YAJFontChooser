/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Björn Pötzschke<bjoern.poetzschke@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.TitledBorder;

public class YAJFontChooser extends JDialog
{
	
	public static Font showDialog()
	{
		return showDialog(null);
	}
	
	
	public static Font showDialog(Font _PreselectedFont)
	{
		YAJFontChooser fontChooser = new YAJFontChooser(_PreselectedFont);
		fontChooser.setVisible(true);
		
		return fontChooser.getSelectedFont();
	}
	
	private static final long	serialVersionUID	= -3162469862533723830L;
	
	private JPanel						m_ButtonPanel			= null;
	private JButton 					m_OkButton				= null;
	private JButton 					m_CancelButton			= null;
	private JPanel 						m_FontSelectionPanel	= null;
	private Font						m_SelectedFont			= null;
	private JLabel						m_FontLabel				= null;
	private JTextField					m_FontFamilyTextField	= null;
	private JScrollPane					m_FontFamilyScrollPane	= null;
	private JList<String>				m_FontFamilyList		= null;
	private HashMap<String, List<Font>>	m_FontMap				= null;
	private JLabel						m_FontStyleLabel		= null;
	private JTextField					m_FontStyleTextField	= null;
	private JScrollPane 				m_FontStyleScrollPane	= null;
	private JList<FontStyleSelection>	m_FontStyleList			= null;
	private JPanel 						m_PreviewPanel			= null;
	private JLabel 						m_PreviewLabel			= null;
	private ButtonActionListener		m_ActionListener		= new ButtonActionListener();
	private SelectionListener			m_SelectionListener		= new SelectionListener();
	
	private YAJFontChooser(Font _PreselectedFont)
	{
		super();
		
		initUI();
		
		//required to wait for close of dialog
		setModal(true);
		
		initFonts();
		
		if(_PreselectedFont != null)
		{
			
		}
	}
	
	private void initUI()
	{
		setSize(450,400);
		setPreferredSize(getSize());
		setResizable(false);
		setLocationRelativeTo(null);
		
		m_ButtonPanel = new JPanel();
		getContentPane().add(m_ButtonPanel, BorderLayout.SOUTH);
		
		m_CancelButton = new JButton("Cancel");
		m_CancelButton.addActionListener(m_ActionListener);
		m_ButtonPanel.add(m_CancelButton);
		
		m_OkButton = new JButton("OK");
		m_OkButton.addActionListener(m_ActionListener);
		m_ButtonPanel.add(m_OkButton);
		
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
		m_FontFamilyTextField.setBounds(2, 24, 173, 20);
		m_FontSelectionPanel.add(m_FontFamilyTextField);
		m_FontFamilyTextField.setColumns(10);
		
		m_FontFamilyScrollPane = new JScrollPane();
		m_FontFamilyScrollPane.setBorder(null);
		m_FontFamilyScrollPane.setBounds(6, 51, 165, 175);
		m_FontSelectionPanel.add(m_FontFamilyScrollPane);
		
		m_FontFamilyList = new JList<String>();
		m_FontFamilyList.setBorder(null);
		m_FontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_FontFamilyScrollPane.setViewportView(m_FontFamilyList);
		
		m_FontStyleLabel = new JLabel("Style");
		m_FontStyleLabel.setBounds(180, 6, 61, 16);
		m_FontSelectionPanel.add(m_FontStyleLabel);
		
		m_FontStyleTextField = new JTextField();
		m_FontStyleTextField.setEnabled(false);
		m_FontStyleTextField.setEditable(false);
		m_FontStyleTextField.setBounds(176, 24, 138, 20);
		m_FontSelectionPanel.add(m_FontStyleTextField);
		m_FontStyleTextField.setColumns(10);
		
		m_FontStyleScrollPane = new JScrollPane();
		m_FontStyleScrollPane.setBounds(180, 51, 130, 175);
		m_FontStyleScrollPane.setBorder(null);
		m_FontSelectionPanel.add(m_FontStyleScrollPane);
		
		m_FontStyleList = new JList<FontStyleSelection>();
		m_FontStyleScrollPane.setViewportView(m_FontStyleList);
		
		m_PreviewPanel = new JPanel();
		m_PreviewPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		m_PreviewPanel.setBounds(6, 235, 438, 98);
		m_FontSelectionPanel.add(m_PreviewPanel);
		m_PreviewPanel.setLayout(new BorderLayout(0, 0));
		
		m_PreviewLabel = new JLabel("");
		m_PreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		m_PreviewPanel.add(m_PreviewLabel);
		m_FontFamilyList.addListSelectionListener(m_SelectionListener);
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
		
		DefaultListModel<String> fontFamilyListModel = new DefaultListModel<String>();
		for(String fontFamily : fontFamilyNames)
		{
			fontFamilyListModel.addElement(fontFamily);
		}
		
		m_FontFamilyList.setModel(fontFamilyListModel);
	}
	
	private void loadFontsForFamily(String _FontFamily)
	{
		DefaultListModel<FontStyleSelection> familyListModel = new DefaultListModel<FontStyleSelection>();
		
		List<Font> familyFonts = m_FontMap.get(_FontFamily);
		
		for(Font font : familyFonts)
		{
			String fontName = font.getFontName();
			familyListModel.addElement(new FontStyleSelection(font));
		}
		
		m_FontStyleList.setModel(familyListModel);
		m_FontStyleList.setSelectedIndex(0);
	}
	
	private Font getSelectedFont()
	{
		return m_SelectedFont;
	}
	
	//----------------------------------------------------------------------------
	
	private class SelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent _Event)
		{
			if(_Event.getSource().equals(m_FontFamilyList))
			{
				m_FontFamilyTextField.setText(m_FontFamilyList.getSelectedValue());
				m_FontFamilyTextField.setCaretPosition(0);
				
				loadFontsForFamily(m_FontFamilyTextField.getText());
			}
			else if(_Event.getSource().equals(m_FontStyleList))
			{
				
			}
		}
	}
	
	private class ButtonActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _Event)
		{
			if(_Event.getSource().equals(m_CancelButton) || _Event.getSource().equals(m_OkButton))
			{
				setVisible(false);
			}
		}
	}
	
	private class FontStyleSelection
	{
		public FontStyleSelection(Font _Font)
		{
			m_Selection = _Font;
		}
		
		@Override
		public String toString()
		{
			return m_Selection.getFamily();
		}
		
		private Font m_Selection = null;
	}
}