package org.whired.ghostclient.awt;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class JAutoScrollPane extends JScrollPane {

	public JAutoScrollPane() {
		super();
		initScrollListener();
	}

	public JAutoScrollPane(final Component arg0) {
		super(arg0);
		initScrollListener();
	}

	public JAutoScrollPane(final int arg0, final int arg1) {
		super(arg0, arg1);
		initScrollListener();
	}

	public JAutoScrollPane(final Component arg0, final int arg1, final int arg2) {
		super(arg0, arg1, arg2);
		initScrollListener();
	}

	private boolean notFromUser = true;

	/**
	 * Notifies this scroll pane that it should autoscroll on the next adjustment
	 */
	public void autoscrollNext() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					notFromUser = true;
				}
			});
		}
		catch (final Throwable e) {
			notFromUser = true;
		}

	}

	private final void initScrollListener() {
		getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			BoundedRangeModel brm = getVerticalScrollBar().getModel();
			boolean wasAtBottom = true;

			@Override
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				if (notFromUser) {
					if (wasAtBottom) {
						brm.setValue(brm.getMaximum());
					}
					notFromUser = false;
				}
				else {
					wasAtBottom = brm.getValue() + brm.getExtent() == brm.getMaximum();
				}
			}
		});
	}
}
