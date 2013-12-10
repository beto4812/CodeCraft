package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class LineHighlightPainter implements Highlighter.HighlightPainter {

  // paint a thick line under one line of text, from r extending rightward to
  // x2
  private void paintLine(Graphics g, Rectangle r, int x2) {
    int ytop = r.y + r.height - 3;
    g.fillRect(r.x, ytop, x2 - r.x, 3);
  }

  // paint thick lines under a block of text
  public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {

    Rectangle r0 = null, r1 = null, rbounds = bounds.getBounds();
    int xmax = rbounds.x + rbounds.width; // x coordinate of right edge
    try { // convert positions to pixel coordinates
      r0 = c.modelToView(p0);
      r1 = c.modelToView(p1);
    } catch (BadLocationException ex) {
      return;
    }
    if ((r0 == null) || (r1 == null))
      return;
    g.setColor(Color.BLACK);

    // special case if p0 and p1 are on the same line
    if (r0.y == r1.y) {
      paintLine(g, r0, r1.x);
      return;
    }

    // first line, from p1 to end-of-line
    paintLine(g, r0, xmax);

    // all the full lines in between, if any (assumes that all lines have
    // the same height--not a good assumption with JEditorPane/JTextPane)
    r0.y += r0.height; // move r0 to next line
    r0.x = rbounds.x; // move r0 to left edge
    while (r0.y < r1.y) {
      paintLine(g, r0, xmax);
      r0.y += r0.height; // move r0 to next line
    }

    // last line, from beginning-of-line to p1
    paintLine(g, r0, r1.x);
  }

  public static void main(String args[]) {

    // extend DefaultCaret as an anonymous inner class
    Caret lineHighlightPainterCaret = new DefaultCaret() {
      private Highlighter.HighlightPainter lhp = new LineHighlightPainter();

      // override getSelectionPainter to return the LineHighlightPainter
      protected Highlighter.HighlightPainter getSelectionPainter() {
        return lhp;
      }
    };

    JFrame frame = new JFrame("LineHighlightPainter demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JTextArea area = new JTextArea(9, 45);
    area.setCaret(lineHighlightPainterCaret);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area
        .setText("This is the story\nof the hare who\nlost his spectacles.");
    frame.getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
  }
}