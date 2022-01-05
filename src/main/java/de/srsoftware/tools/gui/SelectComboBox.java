package de.srsoftware.tools.gui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectComboBox extends JComboBox<Object> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SelectComboBox.class);
	
	public interface TextListener{
		public void textUpdated(String newText);
	}

	private static final long serialVersionUID = -3123598811223301887L;
	private List<Object> elements = new Vector<>();
	private JTextField textField;
	private HashSet<TextListener> textListeners = new HashSet<>();

	public SelectComboBox(List<Object> elements) {
		super();
		setElements(elements);
		setEditable(true);
		textField = (JTextField) getEditor().getEditorComponent();

		textField.addKeyListener(new KeyAdapter() {			
			public void keyReleased(KeyEvent key) {
				switch (key.getKeyChar()) {
					case KeyEvent.VK_ESCAPE:
					case KeyEvent.CHAR_UNDEFINED:
						break;
					default:
						filterText();			
				}
			}
		});
		addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {				
				textListeners.forEach(l -> l.textUpdated(arg0.getItem().toString()));
			}
		});
		if (!elements.isEmpty()) textField.setText(elements.get(0).toString());
	}
	
	@Override
	public void addItem(Object item) {
		elements.add(item);
	}
	
	public void comboBoxFilter(String enteredText) {		
		String lower = enteredText.toLowerCase();
		List<Object> filterArray  = elements.stream().filter(elem -> elem.toString().toLowerCase().contains(lower)).collect(Collectors.toList());
		if (filterArray.size() > 0) {
			DefaultComboBoxModel<Object> model = (DefaultComboBoxModel<Object>) getModel();
			model.removeAllElements();
			model.addElement("");
			filterArray.forEach(model::addElement);

			textField.setText(enteredText);
			if (!isPopupVisible()) showPopup();			
		} else {
			if (isPopupVisible()) hidePopup();
		}
		textListeners.forEach(l -> l.textUpdated(enteredText == null ? null : enteredText.trim()));		
	}
	
	protected void filterText() {
		SwingUtilities.invokeLater(() -> comboBoxFilter(textField.getText()));
	}
	
	public SelectComboBox onUpdateText(TextListener textListener) {
		textListeners.add(textListener);
		return this;
	}


	public void setElements(Collection<Object> values) {
		LOG.debug("elements: {}",elements.getClass());
		elements.clear();
		elements.add("");
		elements.addAll(values);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		List<Object> elements = List.of("Lion", "LionKing", "Mufasa", "Nala", "KingNala", "Animals", "Anims", "Fish", "Jelly Fish", "I am the boss");

		frame.add(new SelectComboBox(elements).onUpdateText(tx -> LOG.debug("Updated text: {}",tx)));
		frame.pack();
		frame.setVisible(true);
	}

}
