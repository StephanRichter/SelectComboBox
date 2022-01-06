# SelectComboBox

Looking for a Java Swing text field with autocompltion? Here it is:

## Usage

```java
	public static void main(String[] args) {		
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		JLabel textDisplay = new JLabel("<html>");
		textDisplay.setPreferredSize(new Dimension(800,600));
		frame.add(textDisplay,BorderLayout.CENTER);

    /* provide a list of options */
		HashSet<String> elements = new HashSet<>();
		elements.addAll(List.of("", "Lion", "Lion ", " Lion", "LionKing", "Mufasa", "Nala", "KingNala", "Animals", "Anims", "Fish", "Jelly Fish", "I am the boss"));
		
    /* create input field */
		SelectComboBox select = new SelectComboBox(elements)
			.onUpdateText(tx -> LOG.debug("Current text: {}",tx))
			.onEnter(entered -> textDisplay.setText(textDisplay.getText()+entered+"<br/>"))
			.onDelete(elements::remove);
		frame.add(select,BorderLayout.NORTH);
    
		frame.pack();
		frame.setVisible(true);
	}
  ```
