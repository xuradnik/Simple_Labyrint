import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 * Trieda GameEngine spravuje hernú logiku labyrintu, vrátane generovania bludiska,
 * ovládania hráča a spúšťania rôznych algoritmov na prechod bludiskom.
 * 
 * @autor Denis Úradník
 * @verzia 0.4.3
 */
public class GameEngine {

    // Herné okno
    private Platno gameWindow;
    // Bludisko
    private Maze maze;
    // Ovládací panel
    private ControlPanel controlPanel;
    // Hráč
    private Player player;
    private Pohyb pohyb;
    // Štartovací blok bludiska
    private Block startingBlock;

    // Stav generovania bludiska
    private boolean isMazeGenerated;
    // Zobrazenie vstupu a výstupu bludiska
    private boolean showMazeEntryAndExit;
    // Indikátor spúšťania algoritmu
    private boolean isRunningAlgorithm;

    /**
     * Konštruktor inicializuje herný engine s danými parametrami,
     * nastaví herné okno, bludisko, kontrolný panel a globálne klávesové ovládanie.
     * 
     * @param velkostPlatnaX Šírka plátna v pixeloch
     * @param velkostPlatnaY Výška plátna v pixeloch
     * @param rows Počet riadkov bludiska
     * @param columns Počet stĺpcov bludiska
     */
    public GameEngine(int velkostPlatnaX, int velkostPlatnaY, int rows, int columns) {
        this.isMazeGenerated = false;
        this.showMazeEntryAndExit = false;
        this.isRunningAlgorithm = false;

        // Inicializácia herného okna a bludiska
        this.gameWindow = Platno.dajPlatno("Labyrint", "Labyrint", velkostPlatnaX, velkostPlatnaY);
        this.maze = new Maze(rows, columns, velkostPlatnaX, velkostPlatnaY);
        this.setTikSpeed(200);
        this.startingBlock = this.maze.getStartingBlock();

        // Pripojenie ovládacieho panelu
        this.controlPanel = new ControlPanel(this);
        this.player = null;
        this.pohyb = null;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                this.handleKeyPress(e);
            }
            return false; 
        });
    }

    /**
     * Spracuje stlačenie klávesi a vykoná príslušný pohyb hráča.
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.movePlayer(Pohyb.UP);
                break;
            case KeyEvent.VK_DOWN:
                this.movePlayer(Pohyb.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                this.movePlayer(Pohyb.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                this.movePlayer(Pohyb.RIGHT);
                break;
            default:
            // Ignorovať ostatné klávesy
                break;
        }
    }

    /**
     * Vytvorí hráča na počiatočnej pozícii v bludisku, ak je bludisko vygenerované
     * a momentálne neběží žiadny algoritmus.
     */
    public void spawnPlayer() {
        if (this.isMazeGenerated && !this.isRunningAlgorithm && this.player == null) {

            // Nastavenie začiatočnej pozície pre hráča
            int row = this.startingBlock.getRowPosition();
            int column = this.startingBlock.getColumnPosition();

            this.player = new Player(row, column, this.maze);
        }
    }

    /**
     * Odstráni hráča zo hry, ak existuje.
     */
    private void despawnPlayer() {
        if (this.player != null) {
            this.player.changeToBlack();
            this.player = null;
        }
    }

    /**
     * Presunie hráča v danom smere, ak hráč existuje.
     * 
     * @param movement Smer pohybu hráča
     */
    public void movePlayer(Pohyb movement) {
        if (this.player != null) {
            this.player.move(movement);
        }
    }


    /**
     * Resetuje bludisko na pôvodný stav, ak bolo bludisko bolo vygenerované.
     */
    public void resetMaze() {
        if (this.isMazeGenerated) {
            this.isMazeGenerated = false;
            this.showMazeEntryAndExit = false;
            this.player = null;
            this.maze.resetMaze();
        }
    }

    /**
     * Generuje nové bludisko, ak ešte nebolo vygenerované.
     */
    public void generateMaze() {
        if (!this.isMazeGenerated) {
            this.isMazeGenerated = this.maze.mazePathGenerator();
        }
    }

    /**
     * Spustí Dijkstraov algoritmus na nájdenie cesty v bludisku, ak je bludisko vygenerované
     * a momentálne neběží žiadny algoritmus.
     */
    public void dijkstraAlgorithm() {
        if (this.isMazeGenerated && !this.isRunningAlgorithm) {
            this.isRunningAlgorithm = true;
            this.despawnPlayer();
            this.maze.dijkstraAlg();
        }
        this.isRunningAlgorithm = false;
    }

    /**
     * Spustí A* algoritmus na nájdenie cesty v bludisku, ak je bludisko vygenerované
     * a momentálne neběží žiadny algoritmus.
     */
    public void aStarAlgorithm() {
        if (this.isMazeGenerated && !this.isRunningAlgorithm) {
            this.isRunningAlgorithm = true;
            this.despawnPlayer();
            this.maze.aStarAlg();
        }
        this.isRunningAlgorithm = false;
    }

    /**
     * Spustí BFS algoritmus na nájdenie cesty v bludisku, ak je bludisko vygenerované
     * a momentálne neběží žiadny algoritmus.
     */
    public void bfsAlgorithm() {
        if (this.isMazeGenerated && !this.isRunningAlgorithm) {
            this.isRunningAlgorithm = true;
            this.despawnPlayer();
            this.maze.bfsAlg();
        }
        this.isRunningAlgorithm = false;
    }

    /**
     * Zobrazí alebo skryje vstup a výstup bludiska.
     */
    public void toggleMazeEntryAndExitDisplay() {
        this.maze.showTheMazeEntryAndExit(this.showMazeEntryAndExit);
        this.showMazeEntryAndExit = !this.showMazeEntryAndExit;
    }

    /**
     * Nastaví rýchlosť tikov pre bludisko.
     * 
     * @param tikLength Dĺžka tiku v milisekundách
     */
    public void setTikSpeed(int tikLength) {
        this.maze.setTikLength(tikLength);
    }

    /**
     * Získa aktuálnu rýchlosť tikov pre bludisko.
     * 
     * @return Dĺžka tiku v milisekundách
     */
    public int getTikSpeed() {
        return this.maze.getTikLength();
    }
}