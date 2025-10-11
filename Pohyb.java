/**
 * Enum Pohyb reprezentuje možné smery pohybu hráča v bludisku.
 * Každý smer je definovaný zmenou v riadku dy a stĺpci dx.
 * 
 * @autor Denis Úradník
 * @verzia 0.0.3
 */
public enum Pohyb {
    
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    
    private final int dx;
    private final int dy;

    /**
     * Konštruktor enumu Pohyb nastavuje zmenu v riadku a stĺpci pre každý smer pohybu.
     * 
     * @param dx Zmena v stĺpci.
     * @param dy Zmena v riadku.
     */
    Pohyb(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Vracia zmenu v stĺpci.
     * 
     * @return Zmena v stĺpci.
     */
    public int getDx() {
        return this.dx;
    }

    /**
     * Vracia zmenu v riadku.
     * 
     * @return Zmena v riadku.
     */
    public int getDy() {
        return this.dy;
    }
}
