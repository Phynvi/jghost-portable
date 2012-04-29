package org.whired.ghostclient.client.impl;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

/**
 * Adds basic link functionality to a {@code JTextPane}
 * @author Whired
 */
public class LinkingJTextPane extends JTextPane {

	/**
	 * The criteria to consider as a link
	 */
	private final HashSet<String> matches = new HashSet<String>();
	/**
	 * The offsets of the currently highlighted link
	 */
	private final int[] curOffs = new int[2];
	/**
	 * The style to apply to applicable links
	 */
	private final MutableAttributeSet linkStyleSet;
	/**
	 * The listeners to notify when a link is clicked
	 */
	private final HashSet<LinkEventListener> linkEventListeners = new HashSet<LinkEventListener>();
	/**
	 * Whether or not matches must made in a case-sensitive manner
	 */
	private final boolean caseSensitive;

	/**
	 * Creates a new linking text pane
	 */
	public LinkingJTextPane(final boolean caseSensitive) {
		final MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setUnderline(set, true);
		this.linkStyleSet = set;
		this.caseSensitive = caseSensitive;
		setUpListeners();
	}

	/**
	 * Adds a link event listener to this text pane
	 * @param listener the listener to add
	 */
	public void addLinkEventListener(final LinkEventListener listener) {
		this.linkEventListeners.add(listener);
	}

	/**
	 * Removes a link event listener from this text pane
	 * @param listener the listener to remove
	 */
	public void removeLinkEventListener(final LinkEventListener listener) {
		this.linkEventListeners.remove(listener);
	}

	/**
	 * Notifies all listeners that a link was clicked
	 * @param linkText the text of the link that was clicked
	 */
	private void fireLinkClicked(final String linkText) {
		for (final LinkEventListener listener : linkEventListeners) {
			listener.linkClicked(linkText);
		}
	}

	private void setUpListeners() {
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(final java.awt.event.MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON1) {
					String word;
					if ((word = findWordAt(evt.getPoint())) != null) {
						fireLinkClicked(word);
					}
				}
			}

			@Override
			public void mouseExited(final MouseEvent evt) {
				clearUnderlines();
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent evt) {
				findWordAt(evt.getPoint());
			}
		});
		this.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				clearUnderlines();
			}

			@Override
			public void focusGained(final FocusEvent e) {
			}
		});
	}

	/**
	 * Adds an array of matches
	 * @param matches the matches to add
	 */
	public void addMatches(String[] matches) {
		if (!caseSensitive) {
			matches = allToLower(matches);
		}
		this.matches.addAll(Arrays.asList(matches));
	}

	/**
	 * Converts all strings in a given array to lowercase to maintain case-insensitivity
	 * @param sourceArr the array to convert
	 * @return the converted array
	 */
	private String[] allToLower(final String[] sourceArr) {
		for (int i = 0; i < sourceArr.length; i++) {
			sourceArr[i] = sourceArr[i].toLowerCase();
		}
		return sourceArr;
	}

	/**
	 * Adds a list of matches
	 * @param matches the matches to add
	 */
	public void addMatches(final HashSet<String> matches) {
		if (!caseSensitive) {
			final String[] newMatches = allToLower(matches.toArray(new String[matches.size()]));
			this.matches.addAll(Arrays.asList(newMatches));
		}
		else {
			this.matches.addAll(matches);
		}
	}

	/**
	 * Clears all matches
	 */
	public void clearMatches() {
		this.matches.clear();
	}

	/**
	 * Remove a single match
	 * @param match the match to remove
	 */
	public void removeMatch(final String match) {
		this.matches.remove(match);
	}

	/**
	 * Add a single match
	 * @param match the match to add
	 */
	public void addMatch(final String match) {
		this.matches.add(match);
	}

	/**
	 * Finds a word in {@code chatOuput} at any given {@code Point}
	 * @param p the point to inspect
	 * @return the word if one met the criteria, otherwise null
	 */
	public String findWordAt(final Point p) {
		final int inOffs = this.viewToModel(p);
		String word = "";
		try {
			final int firstOffs = Utilities.getWordStart(this, inOffs);
			final int lastOffs = Utilities.getWordEnd(this, firstOffs);
			word = getStyledDocument().getText(firstOffs, lastOffs - firstOffs);
			if (!caseSensitive) {
				word = word.toLowerCase();
			}
			if (word.length() > 0 && this.matches.contains(word)) {
				if (curOffs[0] != firstOffs || curOffs[1] != lastOffs) {
					clearUnderlines();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					StyleConstants.setUnderline(this.linkStyleSet, true);
					this.getStyledDocument().setCharacterAttributes(firstOffs, lastOffs - firstOffs, this.linkStyleSet, false);
					curOffs[0] = firstOffs;
					curOffs[1] = lastOffs;
				}
				return word;
			}
			else {
				clearUnderlines();
				return null;
			}
		}
		catch (final BadLocationException ble) {
			clearUnderlines();
			return null;
		}
	}

	private void clearUnderlines() {
		StyleConstants.setUnderline(this.linkStyleSet, false);
		this.getStyledDocument().setCharacterAttributes(curOffs[0], curOffs[1] - curOffs[0], this.linkStyleSet, false);
		this.setCursor(Cursor.getDefaultCursor());
		curOffs[0] = curOffs[1] = 0;
	}
}
