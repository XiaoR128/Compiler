package lex;

import java.awt.EventQueue;

public class app {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Gui().setVisible(true);
			}
		});
	}
}
