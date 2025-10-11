import java.util.Random;
import java.util.ArrayList;

/**
 * Trieda Maze predstavuje bludisko a obsahuje metódy na jeho generovanie, resetovanie a 
 * implementáciu algoritmov ako Dijkstra, A* a BFS. Bludisko je vytvárané pomocou algoritmu Depth First Search (DFS).
 * 
 * @autor Denis Úradník
 * @verzia 0.1.6
 */
public class Maze {
    // Atributy pre jednoduché vlastnosti kocky
    private int lineWidth;
    private int rows;
    private int columns;
    private int cellSize;
    private int tikLength;

    // Atributy pre generáciu bludiska
    private Block[][] wholeMazeMap;
    private ArrayList<Block> stack;

    // Atributy pre algoritmy
    private ArrayList<Block> visitedBlocks;
    private ArrayList<Block> unvisitedBlocks;

    private Block startingBlock;
    private Block endingBlock;

    /**
     * Konštruktor vytvára bludisko na základe zadaných parametrov.
     * Inicializuje 2D pole blokov a nastavuje ich rozmery a pozície.
     * 
     * @param rows Počet riadkov.
     * @param columns Počet stĺpcov.
     * @param sizeOfPlatnoX Šírka plátna.
     * @param sizeOfPlatnoY Výška plátna.
     */
    public Maze(int rows, int columns, int sizeOfPlatnoX, int sizeOfPlatnoY) {
        Random random = new Random();

        this.lineWidth = 3;
        this.rows = rows;
        this.columns = columns;
        this.tikLength = 200;

        /*
         * Výpočet veľkosti kocky na základe veľkosti plátna, počtu riadkov (Rows) a stĺpcov (Columns). 
         * Použije sa menšia hodnota (šírka alebo výška plátna), aby sa zabezpečilo, že bludisko neprekročí 
         * hranice plátna, ak je veľkosť kocky nesprávne vypočítaná.
         */
        if ((sizeOfPlatnoX / rows) <= (sizeOfPlatnoY / columns)) {
            this.cellSize = (int)((sizeOfPlatnoX / rows) * 0.8);
        } else {           
            this.cellSize = (int)((sizeOfPlatnoY / columns) * 0.8);
        }

        // Soft lock pre hrúbku hrany kocky
        if (this.rows >= 200) {
            this.lineWidth = 1;
        } else if (this.rows >= 100) {
            this.lineWidth = 2;
        }

        // Výpočet štartovacej pozície pre bludisko, aby bolo vycentrované vzhľadom na veľkosť plátna.
        int startingX = (sizeOfPlatnoX / 2) - ((this.columns * this.cellSize) / 2);
        int startingY = (sizeOfPlatnoY / 2) - ((this.rows * this.cellSize) / 2);

        /*
         * Generácia individuálnych blokov bludiska a uloženie 
         * jednotlivých blokov do 2D poľa
         */
        this.wholeMazeMap = new Block[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.wholeMazeMap[i][j] = new Block(i, j, startingX, startingY, this.cellSize, this.lineWidth, sizeOfPlatnoX, sizeOfPlatnoY);
            }
        }

        // Nastavenie štartovacieho a cieľového bloku
        this.startingBlock = this.wholeMazeMap[0][0];
        this.endingBlock = this.wholeMazeMap[this.rows - 1][this.columns - 1];
    }

    /**
     * Generuje bludisko pomocou Depth First Search algoritmu.
     * 
     * @return true, keď je bludisko vygenerované.
     */
    public boolean mazePathGenerator() {
        this.stack = new ArrayList<>();
        this.stack.add(this.startingBlock);

        // Hlavný cyklus generácie bludiska
        while (!this.stack.isEmpty()) {
            int stackLastIndex = this.stack.size() - 1;
            Block currentBlock = this.stack.get(stackLastIndex);

            if (!currentBlock.getVisited()) {
                if (this.stack.size() > 1) {
                    // Vyfarbenie cestičky medzi predchádzajúcim a aktuálnym blokom
                    Block previousBlock = this.stack.get(stackLastIndex - 1);
                    this.updateBorders(previousBlock, currentBlock);
                    previousBlock.setNewColor(Farby.MODRA); 
                }

                // Vyfarbenie aktuálneho bloku na červeno
                currentBlock.setNewColor(Farby.CERVENA); 
                currentBlock.setVisited(true); 
            }

            // Získanie zoznamu platných pohybov z aktuálneho bloku
            ArrayList<Block> validMoves = this.nextValidMoves(currentBlock.getRowPosition(), currentBlock.getColumnPosition());

            if (!validMoves.isEmpty()) {
                // Vyber náhodný platný pohyb
                currentBlock.setNewColor(Farby.MODRA); 
                Block nextBlock = validMoves.get(new Random().nextInt(validMoves.size()));
                nextBlock.setNewColor(Farby.CERVENA);
                this.stack.add(nextBlock);
                currentBlock.setNextNeighbour(nextBlock);

                // Nastavenie pôvodu origin pre spätnú rekonštrukciu cesty
                if (nextBlock.getOrigin() == null) {
                    nextBlock.setOrigin(currentBlock);
                }
            } else {
                // Ak sa nemáme kam pohnúť, vraciame sa späť
                currentBlock.setNewColor(Farby.CIERNA); 
                this.stack.remove(stackLastIndex);

                // Ak je v stacku ešte nejaký blok, nastavíme jeho farbu na červenú
                if (!this.stack.isEmpty()) {
                    Block previousBlock = this.stack.get(this.stack.size() - 1);
                    previousBlock.setNewColor(Farby.CERVENA); 
                }
            }

            try {
                Thread.sleep(this.tikLength);
            } catch (InterruptedException e) { }
        }

        this.stack = null;
        return true;
    }

    /**
     * Spúšťa Dijkstrov algoritmus na hľadanie najkratšej cesty v bludisku.
     */
    public void dijkstraAlg() {
        // Resetovanie hodnôt bludiska
        this.resetColors();
        this.resetCostOfBlocks();

        // Vytvorenie nových ArrayListov pre algoritmus
        this.visitedBlocks = new ArrayList<>();
        this.unvisitedBlocks = new ArrayList<>();

        // Inicializácia unvisitedBlocks s všetkými blokmi
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.unvisitedBlocks.add(this.wholeMazeMap[i][j]);
            }
        }

        // Nastavenie ceny začiatočného bloku
        Block currentBlock = this.startingBlock;
        currentBlock.setCostOfBlock(0);
        currentBlock.setNewColor(Farby.CERVENA);

        while (true) {
            if (this.unvisitedBlocks.isEmpty() || currentBlock == null) {
                this.resetColors();

                // Rekonštrukcia cesty späť od koncového bloku
                if (this.endingBlock.getCostOfBlock() != Integer.MAX_VALUE) {
                    Block pathBlock = this.endingBlock;
                    while (pathBlock != null) {
                        pathBlock.setNewColor(Farby.MODRA); 
                        pathBlock = pathBlock.getOrigin(); 
                    }
                }

                // Koniec algoritmu ak list s nenavštívenými blokmi je prázdny
                break;
            }

            currentBlock.setNewColor(Farby.MODRA);

            // Aktualizácia susedov
            if (currentBlock.getNextNeighbourBlock() != null) {
                for (Block neighbor : currentBlock.getNextNeighbourBlock()) {
                    if (!this.visitedBlocks.contains(neighbor)) {
                        int newCost = currentBlock.getCostOfBlock() + 1; 
                        if (newCost < neighbor.getCostOfBlock()) {
                            neighbor.setCostOfBlock(newCost);
                            neighbor.setOrigin(currentBlock);
                        }
                        neighbor.setNewColor(Farby.ZELENA);
                    }
                }
            }

            // Premiestniť blok do zoznamu navštívených
            this.visitedBlocks.add(currentBlock);
            this.unvisitedBlocks.remove(currentBlock);

            // Nájsť blok s najmenšou cenou v nenavštívených blokoch
            currentBlock = null;
            for (Block b : this.unvisitedBlocks) {
                if (currentBlock == null || b.getCostOfBlock() < currentBlock.getCostOfBlock()) {
                    currentBlock = b;
                }
            }

            // Pauza pre vizualizáciu
            try {
                Thread.sleep(this.tikLength);
            } catch (InterruptedException e) { }
        }

        // Nastavenie listov na NULL
        this.unvisitedBlocks = null;
        this.visitedBlocks = null;
    }

    /**
     * Spustí algoritmus A*, aby našiel najkratšiu cestu v bludisku
     */
    public void aStarAlg() {
        // Resetovanie ceny a farby blokov
        this.resetColors();
        this.resetCostOfBlocks();

        this.startingBlock.setCostOfBlock(0);

        // visitedBlocks obsahuje bloky na skumanie
        this.unvisitedBlocks = new ArrayList<>();
        // ClosedSet obsahuje bloky ktore už boli preskúmané
        this.visitedBlocks = new ArrayList<>();

        this.unvisitedBlocks.add(this.startingBlock);
        this.startingBlock.setNewColor(Farby.CERVENA);

        while (!this.unvisitedBlocks.isEmpty()) {
            // Nájsť blok s najnižším f = g + h v unvisitedBlocks
            Block current = null;
            int bestF = Integer.MAX_VALUE;
            for (Block b : this.unvisitedBlocks) {
                int g = b.getCostOfBlock();
                int h = this.heuristic(b);
                int f = g + h;
                if (f < bestF) {
                    bestF = f;
                    current = b;
                }
            }

            if (current == null) {
                // Žiadna cesta nenájdená alebo unvisitedBlocks je prázdny
                return;
            }

            // Ak sme dosiahli cieľový blok
            if (current == this.endingBlock) {
                // Rekonštrukcia cesty späť cez origin
                this.resetColors();
                Block pathBlock = current;
                while (pathBlock != null) {
                    pathBlock.setNewColor(Farby.MODRA);
                    pathBlock = pathBlock.getOrigin();
                }
                return;
            }

            // Presun current z unvisitedBlocks do visitedBlocks
            this.unvisitedBlocks.remove(current);
            this.visitedBlocks.add(current);
            current.setNewColor(Farby.MODRA);

            // Získanie susedov
            ArrayList<Block> neighbors = new ArrayList<>();
            if (current.getNextNeighbourBlock() != null) {
                for (Block b : current.getNextNeighbourBlock()) {
                    if (b != null) {
                        neighbors.add(b);
                    }
                }
            }

            for (Block neighbor : neighbors) {
                if (this.visitedBlocks.contains(neighbor)) {
                    // Tento sused už bol spracovaný
                    continue;
                }

                int tentativeG = current.getCostOfBlock() + 1; 
                if (!this.unvisitedBlocks.contains(neighbor)) {
                    this.unvisitedBlocks.add(neighbor);
                    neighbor.setOrigin(current);
                    neighbor.setCostOfBlock(tentativeG);
                    neighbor.setNewColor(Farby.ZELENA);
                } else if (tentativeG < neighbor.getCostOfBlock()) {
                    // Nájdeme lacnejšiu cestu k tomuto susedovi
                    neighbor.setOrigin(current);
                    neighbor.setCostOfBlock(tentativeG);
                    neighbor.setNewColor(Farby.ZELENA);
                }
            }

            // Pauza pre vizualizáciu
            try {
                Thread.sleep(this.tikLength);
            } catch (InterruptedException e) { }
        }
        
        // Nastavenie listov na NULL
        this.unvisitedBlocks = null;
        this.visitedBlocks = null;
    }

    /**
     * Spustí jednoduchý Breadth-First Search (BFS) algoritmus na hľadanie 
     * cesty v bludisku
     */
    public void bfsAlg() {
        // Resetovanie bludiska
        this.resetColors();
        this.resetCostOfBlocks();

        // Nastavenie začiatočného bloku
        this.startingBlock.setCostOfBlock(0);
        this.startingBlock.setOrigin(null); 
        this.startingBlock.setNewColor(Farby.CERVENA);

        // Vytvoríme stack na BFS pomocou ArrayList a vložíme do nej štartovací blok
        this.stack = new ArrayList<Block>();
        this.stack.add(this.startingBlock);

        // Kým máme prvky v stacku, spracovávame ich postupne
        while (!this.stack.isEmpty()) {
            // Odstránime prvý prvok zo staku
            Block current = this.stack.remove(0);

            //Ak je to cieľový blok, rekonštruujeme cestu a skončíme
            if (current == this.endingBlock) {
                // Rekonštrukujeme cestu späť cez origin
                this.resetColors();
                Block pathBlock = current;
                while (pathBlock != null) {
                    pathBlock.setNewColor(Farby.MODRA);
                    pathBlock = pathBlock.getOrigin();
                }
                return; 
            }

            // Získame susedov pomocou getNextNeighbourBlock()
            Block[] neighborsArray = current.getNextNeighbourBlock();

            // Vytvoríme nový ArrayList pre susedov
            ArrayList<Block> neighbors = new ArrayList<>();
            if (neighborsArray != null) {
                for (int i = 0; i < neighborsArray.length; i++) {
                    neighbors.add(neighborsArray[i]);
                }
            }

            // Prejdeme všetkých susedov: ak ešte neboli spracovaní, nastavíme ich parametre.
            for (Block neighbor : neighbors) {
                // Ak má neighbor cenu == MAX_VALUE, znamená to, že ho ešte BFS nenavštívil
                if (neighbor.getCostOfBlock() == Integer.MAX_VALUE) {
                    // Zvýšime cost o 1
                    neighbor.setCostOfBlock(current.getCostOfBlock() + 1);
                    // Zapamätáme si pôvod, kvôli spätnej rekonštrukcii cesty
                    neighbor.setOrigin(current);
                    // Nastavíme susedovi farbu
                    neighbor.setNewColor(Farby.ZELENA);
                    // Zaradíme ho do BFS stacku
                    this.stack.add(neighbor);
                }
            }

            // Samotný current považujeme za spracovaný a môžeme ho označiť inou farbou
            current.setNewColor(Farby.MODRA);

            try {
                Thread.sleep(this.tikLength);
            } catch (InterruptedException e) { }
        }
        this.stack = null;
    }

    /**
     * Aktualizuje hrany medzi dvoma susednými blokmi na základe ich pozícií.
     * 
     * @param previousBlock Predchádzajúci blok.
     * @param currentBlock Aktuálny blok.
     */
    private void updateBorders(Block previousBlock, Block currentBlock) {
        int oldRow = previousBlock.getRowPosition();
        int oldColumn = previousBlock.getColumnPosition();

        int newRow = currentBlock.getRowPosition();
        int newColumn = currentBlock.getColumnPosition();

        // Nastavenie hraníc
        if (oldRow > newRow) { 
            currentBlock.setNewBorders(currentBlock.getUpBorder(), false, currentBlock.getLeftBorder(), currentBlock.getRightBorder());
            previousBlock.setNewBorders(false, previousBlock.getDownBorder(), previousBlock.getLeftBorder(), previousBlock.getRightBorder());
        } else if (oldRow < newRow) { 
            currentBlock.setNewBorders(false, currentBlock.getDownBorder(), currentBlock.getLeftBorder(), currentBlock.getRightBorder());
            previousBlock.setNewBorders(previousBlock.getUpBorder(), false, previousBlock.getLeftBorder(), previousBlock.getRightBorder());
        } else if (oldColumn > newColumn) { 
            currentBlock.setNewBorders(currentBlock.getUpBorder(), currentBlock.getDownBorder(), currentBlock.getLeftBorder(), false);
            previousBlock.setNewBorders(previousBlock.getUpBorder(), previousBlock.getDownBorder(), false, previousBlock.getRightBorder());
        } else if (oldColumn < newColumn) { 
            currentBlock.setNewBorders(currentBlock.getUpBorder(), currentBlock.getDownBorder(), false, currentBlock.getRightBorder());
            previousBlock.setNewBorders(previousBlock.getUpBorder(), previousBlock.getDownBorder(), previousBlock.getLeftBorder(), false);
        }
    }

    /**
     * Vráti zoznam možných pohybov pre daný blok na základe jeho pozície.
     * 
     * @param currentRow Aktuálny riadok.
     * @param currentColumn Aktuálny stĺpec.
     * 
     * @return Zoznam platných pohybov.
     */
    private ArrayList<Block> nextValidMoves(int currentRow, int currentColumn) {
        // Logika pre validný pohyb
        boolean canGoLeft = currentColumn > 0;                             
        boolean canGoRight = currentColumn < this.columns - 1;   
        boolean canGoUp = currentRow > 0;                                  
        boolean canGoDown = currentRow < this.rows - 1;          

        ArrayList<Block> possibleMovement = new ArrayList<>();

        // Kontrola pre korektný pohyb
        if (canGoUp && !this.wholeMazeMap[currentRow - 1][currentColumn].getVisited()) {
            possibleMovement.add(this.wholeMazeMap[currentRow - 1][currentColumn]);
        }
        if (canGoLeft && !this.wholeMazeMap[currentRow][currentColumn - 1].getVisited()) {
            possibleMovement.add(this.wholeMazeMap[currentRow][currentColumn - 1]);
        }
        if (canGoDown && !this.wholeMazeMap[currentRow + 1][currentColumn].getVisited()) {
            possibleMovement.add(this.wholeMazeMap[currentRow + 1][currentColumn]);
        }
        if (canGoRight && !this.wholeMazeMap[currentRow][currentColumn + 1].getVisited()) {
            possibleMovement.add(this.wholeMazeMap[currentRow][currentColumn + 1]);
        }    

        return possibleMovement;    
    }

    /**
     * Heuristická funkcia pre algoritmus A*: Manhattanská vzdialenosť k cieľu.
     */
    private int heuristic(Block b) {
        int r = b.getRowPosition();
        int c = b.getColumnPosition();
        int goalR = this.endingBlock.getRowPosition();
        int goalC = this.endingBlock.getColumnPosition();
        return (goalR - r) + (goalC - c);
    }

    /**
     * Metóda resetuje hodnotu bloku na maximálnu hodnotu Integeru, a nastaví origin na null.
     */
    private void resetCostOfBlocks() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.wholeMazeMap[i][j].setCostOfBlock(Integer.MAX_VALUE); 
                this.wholeMazeMap[i][j].setOrigin(null);
            }
        }
    }

    /**
     * Metóda kontroluje, či je pohyb na danú pozíciu platný.
     * 
     * @param currentRow Aktuálny riadok.
     * @param currentColumn Aktuálny stĺpec.
     * @param targetRow Cieľový riadok.
     * @param targetColumn Cieľový stĺpec.
     * 
     * @return true, ak je pohyb platný, inak false.
     */
    public boolean isValidMove(int currentRow, int currentColumn, int targetRow, int targetColumn) {
        // Kontrola hraníc
        if (targetRow < 0 || targetRow >= this.rows || targetColumn < 0 || targetColumn >= this.columns) {
            return false;
        }

        // Získanie aktuálneho a cieľového bloku
        Block currentBlock = this.wholeMazeMap[currentRow][currentColumn];
        Block targetBlock = this.wholeMazeMap[targetRow][targetColumn];

        // Kontrola na zablokované hrany
        if (currentRow > targetRow && currentBlock.getUpBorder()) {
            return false; // Pohyb nahor
        }


        if (currentRow < targetRow && currentBlock.getDownBorder()) {
            return false; // Pohyb nadol
        }

        if (currentColumn > targetColumn && currentBlock.getLeftBorder()) {
            return false; // Pohyb doľava
        }


        if (currentColumn < targetColumn && currentBlock.getRightBorder()) {
            return false; // Pohyb doprava
        }

        return true;
    }

    /**
     * Nastaví farbu pre daný blok.
     * 
     * @param row Riadiaci riadok.
     * @param column Riadiaci stĺpec.
     * @param farba Farba, ktorá sa nastaví.
     */
    public void colorBlock(int row, int column, Farby farba) {
        if (row >= 0 && row < rows && column >= 0 && column < columns) {
            this.wholeMazeMap[row][column].setNewColor(farba);
        }
    }

    /**
     * Ukáže alebo skryje začiatočný a konečný blok bludiska.
     * 
     * @param status true na zobrazenie, false na skrytie.
     */
    public void showTheMazeEntryAndExit(boolean status) {
        if (status) {
            this.startingBlock.setNewColor(Farby.CIERNA);
            this.endingBlock.setNewColor(Farby.CIERNA);
        } else {
            this.startingBlock.setNewColor(Farby.CERVENA);
            this.endingBlock.setNewColor(Farby.ZELENA);
        }
    }

    /**
     * Resetuje bludisko do pôvodného stavu.
     * Nastaví všetky bloky na predvolené hodnoty.
     */
    public void resetMaze() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.wholeMazeMap[i][j].resetToDefault();
            }
        }
    }

    /**
     * Nastaví každý blok na čiernu farbu.
     */
    private void resetColors() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.wholeMazeMap[i][j].setNewColor(Farby.CIERNA);
            }
        }
    }

    /**
     * Nastaví dĺžku jedného tiku pre generáciu a algoritmy.
     * 
     * @param newTikLength Nová dĺžka tiku v milisekundách.
     */
    public void setTikLength(int newTikLength) {
        this.tikLength = newTikLength;
    }

    /**
     * Získa aktuálnu dĺžku jedného tiku pre vizualizáciu.
     * 
     * @return Dĺžka tiku v milisekundách.
     */
    public int getTikLength() {
        return this.tikLength;
    }

    /**
     * Získa začiatočný blok bludiska.
     * 
     * @return Začiatočný blok.
     */
    public Block getStartingBlock() {
        return this.startingBlock;
    }

}