import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Platno {
    private static Map<String, Platno> platna = new HashMap<>();

    public static Platno dajPlatno(String key, String title, int sizeX, int sizeY) {
        if (!platna.containsKey(key)) {
            platna.put(key, new Platno(title, sizeX, sizeY, Color.BLACK));
        }
        Platno platno = platna.get(key);
        platno.setVisible(true);
        return platno;
    }

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color pozadie;
    private Image canvasImage;
    private Timer timer;
    private List<Object> objekty;
    private HashMap<Object, PopisTvaru> tvary;
    private List<Object> zmeneneObjekty;

    private Platno(String titulok, int sirka, int vyska, Color pozadie) {
        this.frame = new JFrame();
        this.canvas = new CanvasPane();
        this.frame.setContentPane(this.canvas);
        this.frame.setTitle(titulok);
        this.canvas.setPreferredSize(new Dimension(sirka, vyska));
        this.timer = new javax.swing.Timer(25, null);
        this.timer.start();
        this.pozadie = pozadie;
        this.frame.pack();
        this.objekty = new ArrayList<Object>();
        this.tvary = new HashMap<Object, PopisTvaru>();
        this.zmeneneObjekty = new ArrayList<>();
    }

    public void setVisible(boolean visible) {
        if (this.graphic == null) {
            Dimension size = this.canvas.getSize();
            this.canvasImage = this.canvas.createImage(size.width, size.height);
            this.graphic = (Graphics2D)this.canvasImage.getGraphics();
            this.graphic.setColor(this.pozadie);
            this.graphic.fillRect(0, 0, size.width, size.height);
            this.graphic.setColor(Color.black);
        }
        this.frame.setVisible(visible);
    }

    public void fill(Object objekt, Color farba, Shape tvar) {
        this.objekty.remove(objekt); 
        this.objekty.add(objekt); 
        this.tvary.put(objekt, new PopisTvaru(tvar, farba, null, true));
        this.zmeneneObjekty.add(objekt); 
        this.redraw();
    }

    public void draw(Object objekt, Color farba, Shape tvar, BasicStroke stroke) {
        this.objekty.remove(objekt); 
        this.objekty.add(objekt); 
        this.tvary.put(objekt, new PopisTvaru(tvar, farba, stroke, false));
        this.zmeneneObjekty.add(objekt); 
        this.redraw();
    }

    public void erase(Object objekt) {
        this.objekty.remove(objekt); 
        this.tvary.remove(objekt);
        this.zmeneneObjekty.add(objekt); 
        this.redraw();
    }

    public void wait(int milisekundy) {
        try {
            Thread.sleep(milisekundy);
        } catch (Exception e) {
            System.out.println("Waiting failed");
        }
    }

    private void redraw() {
        for (Object tvar : this.zmeneneObjekty) {
            PopisTvaru popis = this.tvary.get(tvar);
            if (popis != null) {
                popis.draw(this.graphic);
            } else {
                this.eraseArea(this.getBoundingBox(tvar));
            }
        }
        this.zmeneneObjekty.clear();
        this.canvas.repaint();
    }

    private void eraseArea(Rectangle area) {
        Color original = this.graphic.getColor();
        this.graphic.setColor(this.pozadie);
        this.graphic.fill(area);
        this.graphic.setColor(original);
    }

    private Rectangle getBoundingBox(Object objekt) {
        PopisTvaru popis = this.tvary.get(objekt);
        if (popis != null) {
            return popis.tvar.getBounds();
        }
        return new Rectangle();
    }

    public void addKeyListener(KeyListener listener) {
        this.frame.addKeyListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        this.canvas.addMouseListener(listener);
    }

    public void addTimerListener(ActionListener listener) {
        this.timer.addActionListener(listener);
    }

    private class CanvasPane extends JPanel {
        public void paint(Graphics graphic) {
            graphic.drawImage(Platno.this.canvasImage, 0, 0, null);
        }
    }

    private class PopisTvaru {
        private Shape tvar;
        private Color farba;
        private BasicStroke stroke;
        private boolean isFill;

        // Odstránený 'public' prístupový modifikátor
        PopisTvaru(Shape tvar, Color farba, BasicStroke stroke, boolean isFill) {
            this.tvar = tvar;
            this.farba = farba;
            this.stroke = stroke;
            this.isFill = isFill;
        }

        public void draw(Graphics2D graphic) {
            graphic.setColor(this.farba);
            if (this.isFill) {
                graphic.fill(this.tvar);
            } else {
                graphic.setStroke(this.stroke);
                graphic.draw(this.tvar);
            }
        }
    }

    public Graphics2D getGraphics2D() {
        if (this.graphic == null) {
            Dimension size = this.canvas.getSize();
            this.canvasImage = this.canvas.createImage(size.width, size.height);
            this.graphic = (Graphics2D)this.canvasImage.getGraphics();
            this.graphic.setColor(this.pozadie);
            this.graphic.fillRect(0, 0, size.width, size.height);
            this.graphic.setColor(Color.black);
        }
        return this.graphic;
    }
}
