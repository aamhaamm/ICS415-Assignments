import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PolyBezierEditor extends JPanel
    implements MouseListener, MouseMotionListener
{
    private static final int R = 6;               // click/drag radius
    private final List<Point> pts = new ArrayList<>(); // all control points
    private Point hover = null, selected = null;
    private static final Color POLY_COLOR = new Color(0,0,0,50);
    private static final Color CURVE_COLOR = Color.BLUE;

    public PolyBezierEditor() {
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;

        // 1) Instructions
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(14f));
        g.drawString("Click to add points; drag to move them.", 20, 30);
        g.drawString("Every 4 points → a cubic segment (shared endpoints).", 20, 50);

        // 2) Draw each cubic segment
        g.setStroke(new BasicStroke(2f));
        for (int i = 0; i + 3 < pts.size(); i += 3) {
            // control polygon
            g.setColor(POLY_COLOR);
            for (int j = 0; j < 3; j++) {
                Point a = pts.get(i+j), b = pts.get(i+j+1);
                g.drawLine(a.x, a.y, b.x, b.y);
            }
            // curve
            g.setColor(CURVE_COLOR);
            Point prev = deCasteljau(pts, i, 0f);
            for (int s = 1; s <= 100; s++) {
                float t = s/100f;
                Point curr = deCasteljau(pts, i, t);
                g.drawLine(prev.x, prev.y, curr.x, curr.y);
                prev = curr;
            }
        }

        // 3) Draw all control points
        for (Point p : pts) {
            if (p == hover) {
                g.setColor(Color.RED);
                g.fillOval(p.x-R, p.y-R, 2*R, 2*R);
            }
            g.setColor(Color.BLUE);
            g.fillOval(p.x-R/2, p.y-R/2, R, R);
        }
    }

    /** de Casteljau for a single cubic segment starting at index base */
    private Point deCasteljau(List<Point> P, int base, float t) {
        Point p0 = P.get(base+0), p1 = P.get(base+1),
              p2 = P.get(base+2), p3 = P.get(base+3);
        // linear interp
        float x01 = lerp(p0.x, p1.x, t), y01 = lerp(p0.y, p1.y, t);
        float x12 = lerp(p1.x, p2.x, t), y12 = lerp(p1.y, p2.y, t);
        float x23 = lerp(p2.x, p3.x, t), y23 = lerp(p2.y, p3.y, t);
        // next level
        float x012 = lerp(x01, x12, t), y012 = lerp(y01, y12, t);
        float x123 = lerp(x12, x23, t), y123 = lerp(y12, y23, t);
        // final
        float x = lerp(x012, x123, t), y = lerp(y012, y123, t);
        return new Point(Math.round(x), Math.round(y));
    }

    private float lerp(float a, float b, float t) {
        return a * (1 - t) + b * t;
    }

    // --- Mouse events (all in *canvas* coords) ---
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());
        pts.add(p);
        repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        hover = findPoint(e.getX(), e.getY());
        setCursor(hover != null
            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            : Cursor.getDefaultCursor());
        repaint();
    }
    @Override
    public void mousePressed(MouseEvent e) {
        selected = findPoint(e.getX(), e.getY());
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (selected != null) {
            selected.x = e.getX();
            selected.y = e.getY();
            repaint();
        }
    }
    @Override public void mouseReleased(MouseEvent e) { selected = null; }
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private Point findPoint(int x, int y) {
        for (Point p : pts) {
            if (p.distance(x, y) <= R) return p;
        }
        return null;
    }
}
