import java.util.concurrent.Semaphore;

public class ElectionDay {

    /** Steven Necola **/
    /* Project 2 - Java Implementation */

    // Initial Variables
    public static int num_voters = 20;
    public static int num_ID_checkers = 3;
    public static int num_k = 2;
    public static int num_sm = 4;

    // Binary sem Mutex = 1;
    public static Semaphore mutex = new Semaphore(1, true);

    // Threads for IDCheckers
    static IDChecker[] idCheckers = new IDChecker[num_ID_checkers];
    // Counting sem idCheckersAvailable = num_ID_checkers;
    public static Semaphore idCheckersAvailable = new Semaphore(num_ID_checkers, true);
    // Counting sem idCheckersBusy = 0;
    public static Semaphore idCheckersBusy = new Semaphore(0, true);

    // Binary sem[] kioskLine = {1, 1, 1 ...} (initialize all to 1)
    public static Semaphore[] kioskLines = new Semaphore[num_k];
    // Counting sem kioskHelper = num_k
    public static Semaphore kioskHelper = new Semaphore(num_k, true);
    // Boolean[] kioskAvailable = new Boolean[num_k] (initialize all to 0)
    public static Boolean[] kioskAvailable = new Boolean[num_k];

    // int numVoters = 0;
    public static int numVotersGrouped = 0;
    // Binary sem group = 0;
    public static Semaphore group = new Semaphore(0,true);
    // Counting sem smHelper = num_sm;
    public static Semaphore smHelper = new Semaphore(num_sm, true);
    // Counting sem smVoter = 0;
    public static Semaphore smVoter = new Semaphore(0, true);

    // int finishedVoters = 0;
    public static int finishedVoters = 0;
    // Boolean votersStillHere = true;
    public static volatile Boolean votersStillHere = true;


    public static void main (String[] args) {
        // Find num_voters if there is an argument passed in
        if (args.length>0){
            try{
                num_voters = Integer.parseInt(args[0]);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        // Initialize Kiosk Variables
        for (int i=0; i<num_k;i++){
            kioskLines[i] = new Semaphore(1, true);
            kioskAvailable[i] = false;
        }

        // Start the threads
        // Helper Threads
        for (int i = 0; i < num_ID_checkers;i++){
            idCheckers[i] = new IDChecker(i);
            idCheckers[i].start();
        }
        KioskHelper kioskHelper = new KioskHelper(0);
        ScanningHelper scanningHelper = new ScanningHelper(0);
        kioskHelper.start();
        scanningHelper.start();
        // Voter Threads
        for (int i = 0; i < num_voters;i++){
            Voter voter = new Voter(i);
            voter.start();
        }

        // Wait for voters to leave
        while (votersStillHere) {}

        // Interrupt the KioskHelper, IDCheckers, and ScanningHelpers if they're alive.
        if (kioskHelper.isAlive()){
            kioskHelper.interrupt();
        }
        for (int i = 0; i < num_ID_checkers;i++){
            if (idCheckers[i].isAlive()){
                idCheckers[i].interrupt();
            }
        }
        if (scanningHelper.isAlive()){
            scanningHelper.interrupt();
        }


    }
}
