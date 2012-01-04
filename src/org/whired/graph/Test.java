package org.whired.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.whired.graph.Graph.Label;

/**rend
 *
 * @author whired
 */
public class Test {

	public static void main(String[] args) {
		final Line line = new Line(Color.BLUE);
		final Line line2 = new Line(Color.CYAN);
		final LineGraph graph = new LineGraph(400, 300);
		graph.setLabels(Label.ALL ^ Label.POINT_X);
		graph.addLine(line);
		graph.addLine(line2);
		graph.setFont(new Font("Arial", Font.PLAIN, 9));
		graph.setBackground(Color.BLACK);
		JFrame f = new JFrame("Players V Staff");
		f.setLocationByPlatform(true);
		f.getContentPane().add(graph);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int x = 0;
					int lastPlr = 0;
					int lastMod = 0;
					while (true) {
						x++;
						Thread.sleep(700);
						int i = (int) (Math.random() * 5);
						int m = (int)(Math.random() * 2);
						if((int)(Math.random() * 2) == 0) {
							if(lastPlr > 5)
								lastPlr -= i;
							if(lastMod > 1)
								lastMod -= m;
						}
						else {
							lastPlr += i;
							lastMod += m;
						}
						line.addNextY(lastPlr);
						line2.addNextY(lastMod);
					}
				}
				catch (InterruptedException ex) {
					Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}).start();
	}
}
