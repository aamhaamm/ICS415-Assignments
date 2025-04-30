// BezierSketch.java
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BezierSketch extends JPanel
    implements MouseListener, MouseMotionListener
{
    // vertical offset for all shapes (but not the text)
    private static final int SHAPE_OFFSET_Y = 100;
    private static final int R = 8; // control-point radius

    // simple struct for control points + color
    private static class CP {
        float x, y;
        Color color;
        CP(float x, float y, Color c) { this.x = x; this.y = y; this.color = c; }
    }

    private final List<CP> quad  = new ArrayList<>();  // quadratic (3 pts)
    private final List<CP> cubic = new ArrayList<>();  // cubic     (4 pts)
    private final List<CP> quint = new ArrayList<>();  // quintic   (5 pts)

    private CP hoverPoint   = null;
    private CP selectedPoint= null;

    public BezierSketch() {
        setBackground(new Color(240,240,240));
        // initialize exactly as your p5.js example
        quad.add(new CP(100,100, Color.RED));
        quad.add(new CP(200, 50, Color.RED));
        quad.add(new CP(300,100, Color.RED));

        cubic.add(new CP(100,300, Color.GREEN));
        cubic.add(new CP(150,200, Color.GREEN));
        cubic.add(new CP(250,200, Color.GREEN));
        cubic.add(new CP(300,300, Color.GREEN));

        quint.add(new CP(100,500, Color.BLUE));
        quint.add(new CP(150,400, Color.BLUE));
        quint.add(new CP(200,550, Color.BLUE));
        quint.add(new CP(250,400, Color.BLUE));
        quint.add(new CP(300,500, Color.BLUE));

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;

        // 1) Draw instructions at the very top (no offset)
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(14f));
        g.drawString("Click & drag the control points to modify the curves", 20, 30);
        g.drawString("Red:  quadratic (3 pts)", 20, 50);
        g.drawString("Green: cubic     (4 pts)", 20, 70);
        g.drawString("Blue:  quintic   (5 pts)", 20, 90);

        // 2) Push down for everything else
        g.translate(0, SHAPE_OFFSET_Y);

        // 3) Draw control polygons (light pastel)
        drawControlLines(g, quad,   new Color(0xFF,0xCC,0xCC));
        drawControlLines(g, cubic, new Color(0xCC,0xFF,0xCC));
        drawControlLines(g, quint,  new Color(0xCC,0xCC,0xFF));

        // 4) Draw bezier curves
        drawBezier(g, quad,   2);
        drawBezier(g, cubic,  3);
        drawBezier(g, quint,  4);

        // 5) Draw control points (with hover highlight)
        drawControlPoints(g, quad);
        drawControlPoints(g, cubic);
        drawControlPoints(g, quint);

        // undo translation (good practice)
        g.translate(0, -SHAPE_OFFSET_Y);
    }

    private void drawControlLines(Graphics2D g, List<CP> pts, Color c) {
        g.setColor(c);
        for (int i = 0; i + 1 < pts.size(); i++) {
            CP a = pts.get(i), b = pts.get(i+1);
            g.drawLine(Math.round(a.x), Math.round(a.y),
                       Math.round(b.x), Math.round(b.y));
        }
    }

    private void drawBezier(Graphics2D g, List<CP> pts, int degree) {
        if (pts.size() < degree+1) return;
        g.setColor(pts.get(0).color);
        g.setStroke(new BasicStroke(2f));
        Point prev = eval(pts, 0f);
        int steps = 100;
        for (int i = 1; i <= steps; i++) {
            float t = i/(float)steps;
            Point cur = eval(pts, t);
            g.drawLine(prev.x, prev.y, cur.x, cur.y);
            prev = cur;
        }
    }

    // generic de Casteljau evaluation, returns an integer Point for drawing
    private Point eval(List<CP> pts, float t) {
        List<CP> tmp = new ArrayList<>();
        for (CP p: pts) tmp.add(new CP(p.x, p.y, p.color));
        int n = tmp.size();
        for (int r = 1; r < n; r++) {
            for (int i = 0; i < n - r; i++) {
                CP p0 = tmp.get(i), p1 = tmp.get(i+1);
                float x = (1-t)*p0.x + t*p1.x;
                float y = (1-t)*p0.y + t*p1.y;
                tmp.set(i, new CP(x, y, p0.color));
            }
        }
        CP result = tmp.get(0);
        return new Point(Math.round(result.x), Math.round(result.y));
    }

    private void drawControlPoints(Graphics2D g, List<CP> pts) {
        for (CP p: pts) {
            if (p == hoverPoint) {
                // highlight
                g.setColor(new Color(255,255,0,180));
                g.fillOval(Math.round(p.x - R), Math.round(p.y - R), 2*R, 2*R);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(2f));
                g.drawOval(Math.round(p.x - R), Math.round(p.y - R), 2*R, 2*R);
            }
            // draw actual
            g.setColor(p.color);
            g.setStroke(new BasicStroke(1f));
            int d = Math.round(1.5f * R);
            g.fillOval(Math.round(p.x - d/2f),
                       Math.round(p.y - d/2f),
                       d, d);
        }
    }

    // Mouse events: map raw Y → shape‐space by subtracting SHAPE_OFFSET_Y
    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY() - SHAPE_OFFSET_Y;
        hoverPoint = (my >= 0) ? findPointAt(mx, my) : null;
        setCursor(hoverPoint != null
            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            : Cursor.getDefaultCursor());
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY() - SHAPE_OFFSET_Y;
        selectedPoint = (my >= 0) ? findPointAt(mx, my) : null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPoint != null) {
            selectedPoint.x = e.getX();
            selectedPoint.y = e.getY() - SHAPE_OFFSET_Y;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selectedPoint = null;
    }

    // unused MouseListener stubs
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // helper to find the first point within R px of (x,y)
    private CP findPointAt(int x, int y) {
        for (CP p: quad)  if (dist(p, x, y) < R) return p;
        for (CP p: cubic) if (dist(p, x, y) < R) return p;
        for (CP p: quint) if (dist(p, x, y) < R) return p;
        return null;
    }
    private float dist(CP p, int x, int y) {
        float dx = p.x - x, dy = p.y - y;
        return (float)Math.hypot(dx, dy);
    }
}
