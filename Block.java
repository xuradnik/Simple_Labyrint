import java.util.ArrayList;

/**
 * Trieda Block predstavuje jednotlivý blok v bludisku.
 * Ukladá informácie o svojej polohe, hraniciach, stave a susedných blokoch.
 *
 * @autor Denis Úradník
 * @verzia 0.0.9
 */
public class Block {
    // Objekt reprezentujúci vizuálnu časť bloku
    private Stvorec generovanyBlok;

    // Lokačné atribúty
    private int rowPozition;      // Riadková pozícia v bludisku
    private int columnPozition;   // Stĺpcová pozícia v bludisku
    private int xCord;            // X-ová súradnica na plátne
    private int yCord;            // Y-ová súradnica na plátne

    // Atribúty pre generáciu bloku
    private int cellSize;         // Veľkosť bloku
    private int lineWidth;        // Hrúbka hranice bloku
    private boolean upBorder;     // Horná hranica bloku
    private boolean leftBorder;   // Ľavá hranica bloku
    private boolean downBorder;   // Dolná hranica bloku
    private boolean rightBorder;  // Pravá hranica bloku

    // Atribúty pre algoritmy
    private boolean visited;              // Indikátor, či bol blok navštívený
    private int costOfBlock;              // Cena bloku pre algoritmy
    private ArrayList<Block> nextBlock;    // Susedné bloky
    private Block origin;                  // Pôvodný blok (pre rekonstrukciu cesty)

    /**
     * Konštruktor vytvára nový blok, nastavuje jeho vlastnosti a vykresľuje ho na plátne.
     * 
     * @param rowPozitionP Riadková pozícia bloku.
     * @param columnPozitionP Stĺpcová pozícia bloku.
     * @param startingXp Počiatočná X-ová pozícia na plátne.
     * @param startingYp Počiatočná Y-ová pozícia na plátne.
     * @param cellSizeP Veľkosť bloku.
     * @param lineWidthP Hrúbka hranice bloku.
     * @param sizeOfPlatnoXp Šírka plátna.
     * @param sizeOfPlatnoYp Výška plátna.
     */
    public Block(int rowPozitionP, int columnPozitionP, int startingXp, int startingYp, int cellSizeP, int lineWidthP, int sizeOfPlatnoXp, int sizeOfPlatnoYp) {
        
        // Inicializácia lokálnych atribútov
        this.rowPozition = rowPozitionP;
        this.columnPozition = columnPozitionP;
        this.upBorder = true;
        this.downBorder = true;
        this.leftBorder = true;
        this.rightBorder = true;
        this.visited = false;
        this.costOfBlock = Integer.MAX_VALUE;
        this.nextBlock = new ArrayList<>(); // Inicializácia susedných blokov
        this.origin = null;

        // Výpočet súradníc bloku na plátne
        this.xCord = startingXp + (columnPozitionP * cellSizeP);
        this.yCord = startingYp + (rowPozitionP * cellSizeP);

        // Vytvorenie vizuálneho bloku na plátne
        this.generovanyBlok = new Stvorec(
            this.xCord,
            this.yCord,
            cellSizeP,
            lineWidthP,
            this.upBorder,
            this.downBorder,
            this.leftBorder,
            this.rightBorder,
            sizeOfPlatnoXp, 
            sizeOfPlatnoYp
        );
    }


    /**
     * Nastavuje všetky hranice bloku.
     * 
     * @param upBorderP Horná hranica.
     * @param downBorderP Dolná hranica.
     * @param leftBorderP Ľavá hranica.
     * @param rightBorderP Pravá hranica.
     */
    public void setNewBorders(boolean upBorderP, boolean downBorderP, boolean leftBorderP, boolean rightBorderP) {
        this.upBorder = upBorderP;
        this.downBorder = downBorderP;
        this.leftBorder = leftBorderP;
        this.rightBorder = rightBorderP;

        // Aktualizácia vizuálneho bloku na plátne
        this.generovanyBlok.nastavHranicu(upBorderP, downBorderP, leftBorderP, rightBorderP);
    }

    // Gettery a Settery pre hranice bloku

    /**
     * Vracia hornú hranicu bloku.
     * 
     * @return true ak má blok hornú hranicu, inak false.
     */
    public boolean getUpBorder() {
        return this.upBorder;
    }

    /**
     * Vracia dolnú hranicu bloku.
     * 
     * @return true ak má blok dolnú hranicu, inak false.
     */    
    public boolean getDownBorder() {
        return this.downBorder;
    }

    /**
     * Vracia pravú hranicu bloku.
     * 
     * @return true ak má blok pravú hranicu, inak false.
     */
    public boolean getRightBorder() {
        return this.rightBorder;
    }

    /**
     * Vracia ľavú hranicu bloku.
     * 
     * @return true ak má blok ľavú hranicu, inak false.
     */
    public boolean getLeftBorder() {
        return this.leftBorder;
    }

    /**
     * Vracia pôvodnú pozíciu bloku pre rekonstrukciu cesty.
     * 
     * @return Blok, z ktorého sme prišli, alebo null ak neexistuje.
     */
    public Block getOrigin() {
        return this.origin;
    }

    /**
     * Nastavuje pôvodnú pozíciu bloku.
     * 
     * @param origin Blok, z ktorého sme prišli.
     */
    public void setOrigin(Block origin) {
        this.origin = origin;
    }

    /**
     * Pridáva susedný blok do zoznamu susedov.
     * 
     * @param block Susedný blok, ktorý sa pridáva.
     */
    public void setNextNeighbour(Block block) {
        if (this.nextBlock == null) {
            this.nextBlock = new ArrayList<Block>(3);
        }
        this.nextBlock.add(block);
    }

    /**
     * Vracia pole susedných blokov.
     * 
     * @return Pole susedných blokov, alebo null ak žiadne susedy neexistujú.
     */ 
    public Block[] getNextNeighbourBlock() {
        if (this.nextBlock == null || this.nextBlock.isEmpty()) {
            return null;  
        }
        
        Block[] returnList = new Block[this.nextBlock.size()];
        
        for (int i = 0 ; i < this.nextBlock.size(); i++) {
            returnList[i] = this.nextBlock.get(i);
        }
        return returnList;
    }

    /**
     * Vracia cenu bloku pre algoritmy.
     * 
     * @return Cena bloku.
     */
    public int getCostOfBlock() {
        return this.costOfBlock;
    }

    /**
     * Nastavuje cenu bloku pre algoritmy.
     * 
     * @param costOfBlock Nová cena bloku.
     */
    public void setCostOfBlock(int costOfBlock) {
        this.costOfBlock = costOfBlock;
    }

    /**
     * Nastavuje farbu bloku.
     * 
     * @param farba Nová farba bloku.
     */
    public void setNewColor(Farby farba) {
        this.generovanyBlok.zmenFarbuVnutra(farba);
    }

    /**
     * Resetuje blok na pôvodné hodnoty.
     * Nastaví všetky hranice na true, obnoví stav na nevybavený a nastaví farbu na čiernu.
     */
    public void resetToDefault() {
        this.setNewBorders(true, true, true, true);
        this.setVisited(false);
        this.setOrigin(null);
        this.setCostOfBlock(Integer.MAX_VALUE);
        this.setNewColor(Farby.CIERNA);
        this.nextBlock.clear(); // Vyčistí zoznam susedov
    }

    /**
     * Nastavuje stav návštevy bloku.
     * 
     * @param newVisited true ak bol blok navštívený, inak false.
     */
    public void setVisited(boolean newVisited) {
        this.visited = newVisited;
    }

    /**
     * Vracia stav návštevy bloku.
     * 
     * @return true ak bol blok navštívený, inak false.
     */
    public boolean getVisited() {
        return this.visited;
    }

    /**
     * Vracia riadok (Row) bloku v bludisku.
     * 
     * @return Riadok bloku.
     */
    public int getRowPosition() {
        return this.rowPozition;
    }

    /**
     * Vracia stĺpec (Column) bloku v bludisku.
     * 
     * @return Stĺpec bloku.
     */
    public int getColumnPosition() {
        return this.columnPozition;
    }


}
