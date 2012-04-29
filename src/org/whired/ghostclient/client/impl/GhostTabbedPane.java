package org.whired.ghostclient.client.impl;

/**
 * @author eed3si9n
 * @author Whired
 */
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GhostTabbedPane extends JTabbedPane {

	public static final long serialVersionUID = 1L;
	private static final int LINEWIDTH = 3;
	private static final String NAME = "TabTransferData";
	private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
	private static GhostGlassPane s_glassPane = new GhostGlassPane();
	private boolean m_isDrawRect = false;
	private final Rectangle2D m_lineRect = new Rectangle2D.Double();
	private final Color m_lineColor = new Color(0, 100, 255);
	private TabAcceptor m_acceptor = null;
	public Runnable tabsReordered = null;

	public GhostTabbedPane() {
		super();
		final DragSourceListener dsl = new DragSourceListener() {

			@Override
			public void dragEnter(final DragSourceDragEvent e) {
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			}

			@Override
			public void dragExit(final DragSourceEvent e) {
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
				m_lineRect.setRect(0, 0, 0, 0);
				m_isDrawRect = false;
				s_glassPane.setPoint(new Point(-1000, -1000));
				s_glassPane.repaint();
			}

			@Override
			public void dragOver(final DragSourceDragEvent e) {
				// e.getLocation()
				// This method returns a Point indicating the cursor
				// location in screen coordinates at the moment

				final TabTransferData data = getTabTransferData(e);
				if (data == null) {
					e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
					return;
				} // if

				/*
				 * Point tabPt = e.getLocation(); SwingUtilities.convertPointFromScreen(tabPt, DnDTabbedPane.this); if (DnDTabbedPane.this.contains(tabPt)) { int targetIdx = getTargetTabIndex(tabPt); int sourceIndex = data.getTabIndex(); if (getTabAreaBound().contains(tabPt) && (targetIdx >= 0) && (targetIdx != sourceIndex) && (targetIdx != sourceIndex + 1)) { e.getDragSourceContext().setCursor( DragSource.DefaultMoveDrop); return; } // if e.getDragSourceContext().setCursor( DragSource.DefaultMoveNoDrop); return; } // if
				 */

				e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			}

			@Override
			public void dragDropEnd(final DragSourceDropEvent e) {
				m_isDrawRect = false;
				m_lineRect.setRect(0, 0, 0, 0);
				// m_dragTabIndex = -1;

				if (hasGhost()) {
					s_glassPane.setVisible(false);
					s_glassPane.setImage(null);
				}
			}

			@Override
			public void dropActionChanged(final DragSourceDragEvent e) {
			}
		};

		final DragGestureListener dgl = new DragGestureListener() {

			@Override
			public void dragGestureRecognized(final DragGestureEvent e) {
				// System.out.println("dragGestureRecognized");

				final Point tabPt = e.getDragOrigin();
				final int dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
				if (dragTabIndex < 0) {
					return;
				}

				initGlassPane(e.getComponent(), e.getDragOrigin(), dragTabIndex);
				try {
					e.startDrag(DragSource.DefaultMoveDrop, new TabTransferable(GhostTabbedPane.this, dragTabIndex), dsl);
				}
				catch (final InvalidDnDOperationException idoe) {
					idoe.printStackTrace();
				}
			}
		};

		// dropTarget =
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new CDropTargetListener(), true);
		new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
		m_acceptor = new TabAcceptor() {

			@Override
			public boolean isDropAcceptable(final GhostTabbedPane a_component, final int a_index) {
				return true;
			}
		};
	}

	public TabAcceptor getAcceptor() {
		return m_acceptor;
	}

	public void setAcceptor(final TabAcceptor a_value) {
		m_acceptor = a_value;
	}

	private TabTransferData getTabTransferData(final DropTargetDropEvent a_event) {
		try {
			final TabTransferData data = (TabTransferData) a_event.getTransferable().getTransferData(FLAVOR);
			return data;
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private TabTransferData getTabTransferData(final DropTargetDragEvent a_event) {
		try {
			final TabTransferData data = (TabTransferData) a_event.getTransferable().getTransferData(FLAVOR);
			return data;
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private TabTransferData getTabTransferData(final DragSourceDragEvent a_event) {
		try {
			final TabTransferData data = (TabTransferData) a_event.getDragSourceContext().getTransferable().getTransferData(FLAVOR);
			return data;
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	class TabTransferable implements Transferable {

		private TabTransferData m_data = null;

		public TabTransferable(final GhostTabbedPane a_tabbedPane, final int a_tabIndex) {
			m_data = new TabTransferData(GhostTabbedPane.this, a_tabIndex);
		}

		@Override
		public Object getTransferData(final DataFlavor flavor) {
			return m_data;
			// return DnDTabbedPane.this;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			final DataFlavor[] f = new DataFlavor[1];
			f[0] = FLAVOR;
			return f;
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return flavor.getHumanPresentableName().equals(NAME);
		}
	}

	class TabTransferData {

		private GhostTabbedPane m_tabbedPane = null;
		private int m_tabIndex = -1;

		public TabTransferData() {
		}

		public TabTransferData(final GhostTabbedPane a_tabbedPane, final int a_tabIndex) {
			m_tabbedPane = a_tabbedPane;
			m_tabIndex = a_tabIndex;
		}

		public GhostTabbedPane getTabbedPane() {
			return m_tabbedPane;
		}

		public void setTabbedPane(final GhostTabbedPane pane) {
			m_tabbedPane = pane;
		}

		public int getTabIndex() {
			return m_tabIndex;
		}

		public void setTabIndex(final int index) {
			m_tabIndex = index;
		}
	}

	private Point buildGhostLocation(final Point a_location) {
		Point retval = new Point(a_location);

		switch (getTabPlacement()) {
			case SwingConstants.TOP: {
				retval.y = 1;
				retval.x -= s_glassPane.getGhostWidth() / 2;
			}
			break;

			case SwingConstants.BOTTOM: {
				retval.y = getHeight() - 1 - s_glassPane.getGhostHeight();
				retval.x -= s_glassPane.getGhostWidth() / 2;
			}
			break;

			case SwingConstants.LEFT: {
				retval.x = 1;
				retval.y -= s_glassPane.getGhostHeight() / 2;
			}
			break;

			case SwingConstants.RIGHT: {
				retval.x = getWidth() - 1 - s_glassPane.getGhostWidth();
				retval.y -= s_glassPane.getGhostHeight() / 2;
			}
			break;
		} // switch

		retval = SwingUtilities.convertPoint(GhostTabbedPane.this, retval, s_glassPane);
		return retval;
	}

	class CDropTargetListener implements DropTargetListener {

		@Override
		public void dragEnter(final DropTargetDragEvent e) {
			// System.out.println("DropTarget.dragEnter: " +
			// DnDTabbedPane.this);

			if (isDragAcceptable(e)) {
				e.acceptDrag(e.getDropAction());
			}
			else {
				e.rejectDrag();
			}
		}

		@Override
		public void dragExit(final DropTargetEvent e) {
			// System.out.println("DropTarget.dragExit: " +
			// DnDTabbedPane.this);
			m_isDrawRect = false;
		}

		@Override
		public void dropActionChanged(final DropTargetDragEvent e) {
		}

		@Override
		public void dragOver(final DropTargetDragEvent e) {
			final TabTransferData data = getTabTransferData(e);

			if (getTabPlacement() == SwingConstants.TOP || getTabPlacement() == SwingConstants.BOTTOM) {
				initTargetLeftRightLine(getTargetTabIndex(e.getLocation()), data);
			}
			else {
				initTargetTopBottomLine(getTargetTabIndex(e.getLocation()), data);
			}

			repaint();
			if (hasGhost()) {
				s_glassPane.setPoint(buildGhostLocation(e.getLocation()));
				s_glassPane.repaint();
			}
		}

		@Override
		public void drop(final DropTargetDropEvent a_event) {
			// System.out.println("DropTarget.drop: " + DnDTabbedPane.this);

			if (isDropAcceptable(a_event)) {
				convertTab(getTabTransferData(a_event), getTargetTabIndex(a_event.getLocation()));
				a_event.dropComplete(true);
			}
			else {
				a_event.dropComplete(false);
			}

			m_isDrawRect = false;
			repaint();
		}

		public boolean isDragAcceptable(final DropTargetDragEvent e) {
			final Transferable t = e.getTransferable();
			if (t == null) {
				return false;
			}

			final DataFlavor[] flavor = e.getCurrentDataFlavors();
			if (!t.isDataFlavorSupported(flavor[0])) {
				return false;
			}

			final TabTransferData data = getTabTransferData(e);

			if (GhostTabbedPane.this == data.getTabbedPane() && data.getTabIndex() >= 0) {
				return true;
			}

			if (GhostTabbedPane.this != data.getTabbedPane()) {
				if (m_acceptor != null) {
					return m_acceptor.isDropAcceptable(data.getTabbedPane(), data.getTabIndex());
				}
			}

			return false;
		}

		public boolean isDropAcceptable(final DropTargetDropEvent e) {
			final Transferable t = e.getTransferable();
			if (t == null) {
				return false;
			}

			final DataFlavor[] flavor = e.getCurrentDataFlavors();
			if (!t.isDataFlavorSupported(flavor[0])) {
				return false;
			}

			final TabTransferData data = getTabTransferData(e);

			if (GhostTabbedPane.this == data.getTabbedPane() && data.getTabIndex() >= 0) {
				return true;
			}

			if (GhostTabbedPane.this != data.getTabbedPane()) {
				if (m_acceptor != null) {
					return m_acceptor.isDropAcceptable(data.getTabbedPane(), data.getTabIndex());
				}
			}

			return false;
		}
	}

	private boolean m_hasGhost = true;

	public void setPaintGhost(final boolean flag) {
		m_hasGhost = flag;
	}

	public boolean hasGhost() {
		return m_hasGhost;
	}

	/**
	 * returns potential index for drop.
	 * @param a_point point given in the drop site component's coordinate
	 * @return returns potential index for drop.
	 */
	private int getTargetTabIndex(final Point a_point) {
		final boolean isTopOrBottom = getTabPlacement() == SwingConstants.TOP || getTabPlacement() == SwingConstants.BOTTOM;

		// if the pane is empty, the target index is always zero.
		if (getTabCount() == 0) {
			return 0;
		}

		for (int i = 0; i < getTabCount(); i++) {
			final Rectangle r = getBoundsAt(i);
			if (isTopOrBottom) {
				r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
			}
			else {
				r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
			}

			if (r.contains(a_point)) {
				return i;
			}
		} // for

		final Rectangle r = getBoundsAt(getTabCount() - 1);
		if (isTopOrBottom) {
			final int x = r.x + r.width / 2;
			r.setRect(x, r.y, getWidth() - x, r.height);
		}
		else {
			final int y = r.y + r.height / 2;
			r.setRect(r.x, y, r.width, getHeight() - y);
		} // if-else

		return r.contains(a_point) ? getTabCount() : -1;
	}

	private void convertTab(final TabTransferData a_data, int a_targetIndex) {
		final GhostTabbedPane source = a_data.getTabbedPane();
		final int sourceIndex = a_data.getTabIndex();
		if (sourceIndex < 0) {
			return;
		}

		final Component cmp = source.getComponentAt(sourceIndex);
		final String str = source.getTitleAt(sourceIndex);
		if (this != source) {
			source.remove(sourceIndex);

			if (a_targetIndex == getTabCount()) {
				addTab(str, cmp);
			}
			else {
				if (a_targetIndex < 0) {
					a_targetIndex = 0;
				}

				insertTab(str, null, cmp, null, a_targetIndex);

			} // if

			setSelectedComponent(cmp);
			// System.out.println("press="+sourceIndex+" next="+a_targetIndex);
			return;
		} // if

		if (a_targetIndex < 0 || sourceIndex == a_targetIndex) {
			// System.out.println("press="+prev+" next="+next);
			return;
		}

		if (a_targetIndex == getTabCount()) {
			// System.out.println("last: press="+prev+" next="+next);
			source.remove(sourceIndex);
			addTab(str, cmp);
			setSelectedIndex(getTabCount() - 1);
		}
		else if (sourceIndex > a_targetIndex) {
			// System.out.println("   >: press="+prev+" next="+next);
			source.remove(sourceIndex);
			insertTab(str, null, cmp, null, a_targetIndex);
			setSelectedIndex(a_targetIndex);
		}
		else {
			// System.out.println("   <: press="+prev+" next="+next);
			source.remove(sourceIndex);
			insertTab(str, null, cmp, null, a_targetIndex - 1);
			setSelectedIndex(a_targetIndex - 1);
		}
		if (tabsReordered != null) {
			tabsReordered.run();
		}
	}

	private void initTargetLeftRightLine(final int next, final TabTransferData a_data) {
		if (next < 0) {
			m_lineRect.setRect(0, 0, 0, 0);
			m_isDrawRect = false;
			return;
		} // if

		if (a_data.getTabbedPane() == this && (a_data.getTabIndex() == next || next - a_data.getTabIndex() == 1)) {
			m_lineRect.setRect(0, 0, 0, 0);
			m_isDrawRect = false;
		}
		else if (getTabCount() == 0) {
			m_lineRect.setRect(0, 0, 0, 0);
			m_isDrawRect = false;
			return;
		}
		else if (next == 0) {
			final Rectangle rect = getBoundsAt(0);
			m_lineRect.setRect(-LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
			m_isDrawRect = true;
		}
		else if (next == getTabCount()) {
			final Rectangle rect = getBoundsAt(getTabCount() - 1);
			m_lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
			m_isDrawRect = true;
		}
		else {
			final Rectangle rect = getBoundsAt(next - 1);
			m_lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
			m_isDrawRect = true;
		}
	}

	private void initTargetTopBottomLine(final int next, final TabTransferData a_data) {
		if (next < 0) {
			m_lineRect.setRect(0, 0, 0, 0);
			m_isDrawRect = false;
			return;
		} // if

		if (a_data.getTabbedPane() == this && (a_data.getTabIndex() == next || next - a_data.getTabIndex() == 1)) {
			m_lineRect.setRect(0, 0, 0, 0);
			m_isDrawRect = false;
		}
		else if (getTabCount() == 0) {
			m_lineRect.setRect(0, 0, 0, 0);
			m_isDrawRect = false;
			return;
		}
		else if (next == getTabCount()) {
			final Rectangle rect = getBoundsAt(getTabCount() - 1);
			m_lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2, rect.width, LINEWIDTH);
			m_isDrawRect = true;
		}
		else if (next == 0) {
			final Rectangle rect = getBoundsAt(0);
			m_lineRect.setRect(rect.x, -LINEWIDTH / 2, rect.width, LINEWIDTH);
			m_isDrawRect = true;
		}
		else {
			final Rectangle rect = getBoundsAt(next - 1);
			m_lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2, rect.width, LINEWIDTH);
			m_isDrawRect = true;
		}
	}

	private void initGlassPane(final Component c, final Point tabPt, final int a_tabIndex) {
		// Point p = (Point) pt.clone();
		getRootPane().setGlassPane(s_glassPane);
		if (hasGhost()) {
			final Rectangle rect = getBoundsAt(a_tabIndex);
			BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
			final Graphics g = image.getGraphics();
			c.paint(g);
			image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
			s_glassPane.setImage(image);
		} // if

		s_glassPane.setPoint(buildGhostLocation(tabPt));
		s_glassPane.setVisible(true);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (m_isDrawRect) {
			final Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(m_lineColor);
			g2.fill(m_lineRect);
		} // if
	}

	public interface TabAcceptor {

		boolean isDropAcceptable(GhostTabbedPane a_component, int a_index);
	}
}

class GhostGlassPane extends JPanel {

	public static final long serialVersionUID = 1L;
	private final AlphaComposite m_composite;
	private final Point m_location = new Point(0, 0);
	private BufferedImage m_draggingGhost = null;

	public GhostGlassPane() {
		setOpaque(false);
		m_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
	}

	public void setImage(final BufferedImage draggingGhost) {
		m_draggingGhost = draggingGhost;
	}

	public void setPoint(final Point a_location) {
		m_location.x = a_location.x;
		m_location.y = a_location.y;
	}

	public int getGhostWidth() {
		if (m_draggingGhost == null) {
			return 0;
		}

		return m_draggingGhost.getWidth(this);
	}

	public int getGhostHeight() {
		if (m_draggingGhost == null) {
			return 0;
		}

		return m_draggingGhost.getHeight(this);
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (m_draggingGhost == null) {
			return;
		}

		final Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(m_composite);

		g2.drawImage(m_draggingGhost, (int) m_location.getX(), (int) m_location.getY(), null);
	}
}
