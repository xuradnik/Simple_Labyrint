/**
 * Trieda Player reprezentuje hráča v bludisku.
 * Umožňuje hráčovi pohybovať sa po bludisku a meniť farbu aktuálneho bloku.
 * 
 * @autor Denis Úradník
 * @verzia 0.0.5
 */

public class Player {
    
    // Aktuálna pozícia hráča v bludisku
    private int row; 
    private int column; 
    
    private Maze maze;
    private Pohyb pohyb;

    /**
     * Konštruktor inicializuje hráča na počiatočnej pozícii v bludisku.
     * 
     * @param startRow Počiatočný riadok.
     * @param startColumn Počiatočný stĺpec.
     * @param maze Odkaz na bludisko.
     */
    public Player(int startRow, int startColumn, Maze maze) {
        this.row = startRow;
        this.column = startColumn;
        this.maze = maze;
        this.pohyb = null;

        // Označenie počiatočnej pozície hráča
        this.maze.colorBlock(this.row, this.column, Farby.BIELA);
    }

    /**
     * Metóda umožňuje hráčovi pohybovať sa v zadanom smere.
     * Skontroluje platnosť pohybu a aktualizuje pozíciu hráča.
     * 
     * @param pohyb Objekt predstavujúci smer pohybu (napr. hore, dole, doľava, doprava).
     */
    public void move(Pohyb pohyb) {
        // Vypočítanie novej pozície
        int newRow = this.row + pohyb.getDy();
        int newColumn = this.column + pohyb.getDx();

        // Overenie, či je pohyb platný
        if (this.maze.isValidMove(this.row, this.column, newRow, newColumn)) {
            // Obnovenie farby aktuálneho bloku na čiernu
            this.maze.colorBlock(this.row, this.column, Farby.CIERNA);

            // Aktualizácia pozície hráča
            this.row = newRow;
            this.column = newColumn;

            // Označenie nového bloku farbou hráča (biela)
            this.maze.colorBlock(this.row, this.column, Farby.BIELA);
        }
    }

    /**
     * Metóda mení farbu aktuálneho bloku hráča na čiernu.
     */
    public void changeToBlack() {
        this.maze.colorBlock(this.row, this.column, Farby.CIERNA);
    }
    
}
