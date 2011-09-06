package org.whired.ghostclient.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

/**
 * Adds basic link functionality to a {@code JTextPane}
 *
 * @author Whired
 */
public class LinkingJTextPane extends JTextPane
{

	/** The criteria to consider as a link */
	private ArrayList<String> matches = new ArrayList<String>();
	/** The offsets of the currently highlighted link */
	private int[] curOffs = new int[2];
	/** The style to apply to applicable links */
	private MutableAttributeSet linkStyleSet;
	/** The listeners to notify when a link is clicked */
	private ArrayList<LinkEventListener> linkEventListeners = new ArrayList<LinkEventListener>();

	/**
	 * Creates a new linking text pane
	 */
	public LinkingJTextPane()
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setUnderline(set, true);
		this.linkStyleSet = set;
		setUpListeners();
	}

	/**
	 * Adds a link event listener to this text pane
	 * @param listener the listener to add
	 */
	public void addLinkEventListener(LinkEventListener listener)
	{
		this.linkEventListeners.add(listener);
	}

	/**
	 * Removes a link event listener from this text pane
	 * @param listener the listener to remove
	 */
	public void removeLinkEventListener(LinkEventListener listener)
	{
		this.linkEventListeners.remove(listener);
	}

	/**
	 * Notifies all listeners that a link was clicked
	 * @param linkText the text of the link that was clicked
	 */
	private void fireLinkClicked(String linkText)
	{
		for (LinkEventListener listener : linkEventListeners)
		{
			listener.linkClicked(linkText);
		}
	}

	private void setUpListeners()
	{
		this.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(java.awt.event.MouseEvent evt)
			{
				if (evt.getButton() == MouseEvent.BUTTON1)
				{
					String word;
					if ((word = findWordAt(evt.getPoint())) != null)
					{
						fireLinkClicked(word);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent evt)
			{
				clearUnderlines();
			}
		});
		this.addMouseMotionListener(new MouseAdapter()
		{

			@Override
			public void mouseMoved(java.awt.event.MouseEvent evt)
			{
				findWordAt(evt.getPoint());
			}
		});
		this.addFocusListener(new FocusListener()
		{

			public void focusLost(FocusEvent e)
			{
				clearUnderlines();
			}

			public void focusGained(FocusEvent e)
			{
			}
		});
	}

	/**
	 * Adds an array of matches
	 * @param matches the matches to add
	 */
	public void addMatches(String[] matches)
	{
		this.matches.addAll(Arrays.asList(matches));
	}

	/**
	 * Adds a list of matches
	 * @param matches the matches to add
	 */
	public void addMatches(List<String> matches)
	{
		this.matches.addAll(matches);
	}

	/**
	 * Clears all matches
	 */
	public void clearMatches()
	{
		this.matches.clear();
	}

	/**
	 * Remove a single match
	 * @param match the match to remove
	 */
	public void removeMatch(String match)
	{
		this.matches.remove(match);
	}

	/**
	 * Add a single match
	 * @param match the match to add
	 */
	public void addMatch(String match)
	{
		this.matches.add(match);
	}

	/**
	 * Finds a word in {@code chatOuput} at any given {@code Point}
	 * @param p the point to inspect
	 * @param highlight whether or not to highlight this word
	 * @return the word if one met the criteria, otherwise null
	 */
	public String findWordAt(Point p)
	{
		int inOffs = this.viewToModel(p);
		String word = "";
		try
		{
			int firstOffs = Utilities.getWordStart(this, inOffs);
			int lastOffs = Utilities.getWordEnd(this, firstOffs);
			word = getStyledDocument().getText(firstOffs, lastOffs - firstOffs);
			if (word.length() > 0 && this.matches.contains(word))
			{
				if(curOffs[0] != firstOffs || curOffs[1] != lastOffs)
				{
					clearUnderlines();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					StyleConstants.setUnderline(this.linkStyleSet, true);
					this.getStyledDocument().setCharacterAttributes(firstOffs, lastOffs - firstOffs, this.linkStyleSet, false);
					curOffs[0] = firstOffs;
					curOffs[1] = lastOffs;
				}
				return word;
			}
			else
			{
				clearUnderlines();
				return null;
			}
		}
		catch (BadLocationException ble)
		{
			clearUnderlines();
			return null;
		}
	}

	private void clearUnderlines()
	{
		StyleConstants.setUnderline(this.linkStyleSet, false);
		this.getStyledDocument().setCharacterAttributes(curOffs[0], curOffs[1] - curOffs[0], this.linkStyleSet, false);
		this.setCursor(Cursor.getDefaultCursor());
		curOffs[0] = curOffs[1] = 0;
	}
}
