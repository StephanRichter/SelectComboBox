package de.srsoftware.tools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectComboBox extends JComboBox<Object> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SelectComboBox.class);
	
	public interface DeleteListener{
		public void textDeleted(String deletedText);
	}
	
	public interface EnterListener{
		public void textEntered(String enteredText);
	}

	public interface TextListener{
		public void textUpdated(String newText);
	}

	private static final long serialVersionUID = -3123598811223301887L;
	private Collection<? extends Object> elements = new Vector<>();
	private JTextField inputField;
	private HashSet<DeleteListener> deleteListeners = new HashSet<>();
	private HashSet<EnterListener> enterListeners = new HashSet<>();
	private HashSet<TextListener> textListeners = new HashSet<>();

	public SelectComboBox(Collection<? extends Object> elements) {
		super();
		setElements(elements);
		setEditable(true);
		inputField = (JTextField) getEditor().getEditorComponent();
		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				handleKey(e);
			}
		});
	}
		

	public String getText(){
		return inputField.getText();
	}
	
	protected void handleKey(KeyEvent e) {
		//LOG.debug("handleKey({})",e);
		String tx = inputField.getText();
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:				
			case KeyEvent.VK_KP_UP:
			case KeyEvent.VK_HOME:
			case KeyEvent.VK_END:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_CONTROL:			
				break;
			case KeyEvent.VK_DELETE:
				if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0) {
					deleteListeners.forEach(l -> l.textDeleted(tx));
					inputField.setText("");
					hidePopup();
				}
				break;
			case KeyEvent.VK_ENTER:
				hidePopup();
				enterListeners.forEach(l -> l.textEntered(tx));
				break;
			case KeyEvent.VK_DOWN:
				if (!tx.isEmpty()) break;
			default:
				showFiltered();
				break;
		}
		textListeners.forEach(l -> l.textUpdated(tx));
	}


	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		JLabel textDisplay = new JLabel("<html>");
		textDisplay.setPreferredSize(new Dimension(800,600));
		frame.add(textDisplay,BorderLayout.CENTER);

		HashSet<String> elements = new HashSet<>();
		elements.addAll(List.of("", "Lion", "Lion ", " Lion", "LionKing", "Mufasa", "Nala", "KingNala", "Animals", "Anims", "Fish", "Jelly Fish", "I am the boss"));

		SelectComboBox select = new SelectComboBox(elements)
				.onUpdateText(tx -> LOG.debug("Current text: {}",tx))
				.onEnter(entered -> textDisplay.setText(textDisplay.getText()+entered+"<br/>"))
				.onDelete(elements::remove);
		frame.add(select,BorderLayout.NORTH);


		frame.pack();
		frame.setVisible(true);
	}
	

	public SelectComboBox onDelete(DeleteListener deleteListener) {
		deleteListeners.add(deleteListener);
		return this;
	}

	public SelectComboBox onEnter(EnterListener enterListener) {
		enterListeners.add(enterListener);
		return this;
	}

	public SelectComboBox onUpdateText(TextListener textListener) {
		textListeners.add(textListener);		
		return this;
	}
	



	public SelectComboBox setElements(Collection<? extends Object> values) {
		//LOG.debug("elements: {}",elements.getClass());
		this.elements = values;
		return this;
	}

	public SelectComboBox setText(String newVal){
		inputField.setText(newVal);
		return this;
	}

	private void showFiltered() {
		String text = inputField.getText();
		hidePopup();
		if (elements == null || elements.isEmpty()) {
			LOG.info("No items to display!");
			return;
		}
		String lower = text.toLowerCase();
		Stream<String> stream = elements.stream().map(Object::toString).filter(s -> !s.isBlank());
		if (lower.isEmpty()) {
			LOG.debug("lower is blank!");
		} else {
			stream = stream.filter(element -> element.toString().toLowerCase().contains(lower));
		}
		List<String> filtered = stream.map(String::trim).distinct().sorted().collect(Collectors.toList());
		DefaultComboBoxModel<Object> model = (DefaultComboBoxModel<Object>) getModel();
		model.removeAllElements();
		if (!filtered.isEmpty()) {
			model.addAll(filtered);
			showPopup();
		}
		inputField.setText(text);

	}
	


}
