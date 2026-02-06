/**
 * Táto trieda slúži ako štartovací bod pre celú hru.
 * Implementuje jednoduchý Singleton vzor na zabezpečenie jedinečnej inštancie.
 * 
 * @autor Denis Úradník
 * @verzia 0.0.4
 */
public class Main {
    
    // Jediná inštancia triedy Main
    private static Main instancia;     
    
    /**
     * Privátny konštruktor, ktorý inicializuje herný engine s nastavenými parametrami.
     * Šírka a výška plátna sú nastavené na 800 pixelov a bludisko má 10 riadkov a 10 stĺpcov.
     */
    private Main() {
        new GameEngine(800, 800, 10, 10);
    }
    
    /**
     * Metóda na získanie jedinečnej inštancie triedy Main.
     * Ak inštancia ešte neexistuje, vytvorí sa nová.
     * 
     * @return Jediná inštancia triedy Main.
     */
    public static void main(String[] args) {
        if (instancia == null) {
            instancia = new Main();
        }
        //return instancia;
    }

}
