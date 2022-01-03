package de.srsoftware.tools.gui;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SelectComboBox extends JComboBox<Object> {

	private static final long serialVersionUID = -3123598811223301887L;
	private List<Object> elements;

	public SelectComboBox(List<Object> elements) {
		super();
		this.elements = elements;
		setEditable(true);
		JTextField textField = (JTextField) getEditor().getEditorComponent();

		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent key) {
				switch (key.getKeyChar()) {
					case KeyEvent.VK_ESCAPE:
					case KeyEvent.CHAR_UNDEFINED:
						break;
					default:
						filterText(textField);			
				}
			}
		});
	}
	
	@Override
	public void addItem(Object item) {
		elements.add(item);
	}

	protected void filterText(JTextField textField) {
		SwingUtilities.invokeLater(() -> {
			if (!textField.getText().isEmpty()) comboBoxFilter(textField.getText().toLowerCase());
		});

	}

	public void comboBoxFilter(String enteredText) {
		List<Object> filterArray  = elements.stream().filter(elem -> elem.toString().toLowerCase().contains(enteredText)).collect(Collectors.toList());
		if (filterArray.size() > 0) {
			if (!isPopupVisible()) showPopup();
			DefaultComboBoxModel<Object> model = (DefaultComboBoxModel<Object>) getModel();
			model.removeAllElements();
			filterArray.forEach(model::addElement);

			((JTextField) getEditor().getEditorComponent()).setText(enteredText);
		} else {
			if (isPopupVisible()) hidePopup();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		List<Object> elements = List.of("Lion", "LionKing", "Mufasa", "Nala", "KingNala", "Animals", "Anims", "Fish", "Jelly Fish", "I am the boss");

		frame.add(new SelectComboBox(elements));
		frame.pack();
		frame.setVisible(true);
	}
}
