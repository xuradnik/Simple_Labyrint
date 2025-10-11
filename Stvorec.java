import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class Stvorec {
    
    private int velkostPlatnaX;
    private int velkostPlatnaY;
    
    private int strana;
    private int lavyHornyX;
    private int lavyHornyY;    
    private int hrubkaObvodu;
    private Color farbaObvodu;
    private Color farbaVnutra;

    private boolean hornaHranica;
    private boolean dolnaHranica;
    private boolean lavaHranica;
    private boolean pravaHranica;
    
    private Farby farba;

    
    private Object fillObjekt = new Object();
    private Object borderObjekt = new Object();

    public Stvorec(
        int poziciaXp, int poziciaYp, int stranaP, int hrubkaObvoduP, boolean hornaHranicaP, boolean dolnaHranicaP, boolean lavaHranicaP, boolean pravaHranicaP, int velkostPlatnaXp, int velkostPlatnaYp) {
        this.velkostPlatnaX = velkostPlatnaXp;
        this.velkostPlatnaY = velkostPlatnaYp;
        this.lavyHornyX = poziciaXp;
        this.lavyHornyY = poziciaYp;
        this.strana = stranaP;
        this.hrubkaObvodu = hrubkaObvoduP;
        this.farbaObvodu = Color.white;
        this.farbaVnutra = Color.black; 
        this.hornaHranica = hornaHranicaP;
        this.dolnaHranica = dolnaHranicaP;
        this.lavaHranica = lavaHranicaP;
        this.pravaHranica = pravaHranicaP;

        this.nakresli();
    }

    public void zmenFarbuVnutra(Farby farba) {
        switch (farba) {
            case ZELENA:
                this.farbaVnutra = Color.green;
                break;
            case ZLTA:
                this.farbaVnutra = Color.yellow;
                break;
            case MODRA:
                this.farbaVnutra = Color.blue;
                break;
            case CERVENA:
                this.farbaVnutra = Color.red;
                break;
            case BIELA:
                this.farbaVnutra = Color.white;
                break;
            default:
                this.farbaVnutra = Color.black;
                break;
        }
        this.nakresli();
    }

    public void nastavHranicu(boolean horna, boolean dolna, boolean lava, boolean prava) {
        this.hornaHranica = horna;
        this.dolnaHranica = dolna;
        this.lavaHranica = lava;
        this.pravaHranica = prava;
        this.nakresli();
    }

    private void nakresli() {
        Platno canvas = Platno.dajPlatno("Labyrint", "Labyrint", this.velkostPlatnaX, this.velkostPlatnaY);

        canvas.erase(this.fillObjekt);
        canvas.erase(this.borderObjekt);

        
        Rectangle2D.Double rectangle = new Rectangle2D.Double(this.lavyHornyX, this.lavyHornyY, this.strana, this.strana);

        Path2D.Double path = new Path2D.Double();

        if (this.hornaHranica) {
            path.moveTo(this.lavyHornyX, this.lavyHornyY);
            path.lineTo(this.lavyHornyX + this.strana, this.lavyHornyY);
        }
        if (this.dolnaHranica) {
            path.moveTo(this.lavyHornyX, this.lavyHornyY + this.strana);
            path.lineTo(this.lavyHornyX + this.strana, this.lavyHornyY + this.strana);
        }
        if (this.lavaHranica) {
            path.moveTo(this.lavyHornyX, this.lavyHornyY);
            path.lineTo(this.lavyHornyX, this.lavyHornyY + this.strana);
        }
        if (this.pravaHranica) {
            path.moveTo(this.lavyHornyX + this.strana, this.lavyHornyY);
            path.lineTo(this.lavyHornyX + this.strana, this.lavyHornyY + this.strana);
        }

        BasicStroke stroke = new BasicStroke(this.hrubkaObvodu);

        if (this.farbaVnutra != null) {
            canvas.fill(this.fillObjekt, this.farbaVnutra, rectangle);
        }

        canvas.draw(this.borderObjekt, this.farbaObvodu, path, stroke);
    }
}