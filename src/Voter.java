import java.security.spec.ECField;
import java.util.Random;

public class Voter extends Thread{

    public static long time = System.currentTimeMillis();
    Random random;
    int id;

    public Voter(int id) {
        this.id = id;
        setName("Voter"+id);
        random = new Random(System.currentTimeMillis()*id);
    }

    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }

    @Override
    public void run () {
        // Random seed for each Voter
        msg("Thread created!");
        try {
            // Voters Arrive at the Voting Place
            arrive();
            // Voters get their id checked
            checkId();
            // Voters use Kiosk
            useKiosk();
            // Voters scan ballot
            scanBallot();
            // Voters leave
            leave();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void arrive() {
        try {
            sleep(Math.abs(random.nextInt(4000)));
            msg("Arrived at voting place");
        } catch (InterruptedException e) {
            msg("Voter took too long to arrive");
        }
    }

    void checkId() {
        try{
            ElectionDay.idCheckersAvailable.acquire();
            msg("Finished ID Check");
            ElectionDay.idCheckersBusy.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void useKiosk() {
        try{
            int i = findShortestLine();
            msg("Waits in line for Kiosk"+i);
            ElectionDay.kioskLines[i].acquire();
            // Voters Enter Information
            enterInformation(i);
            // Let go of the kiosk and let KioskHelper know
            ElectionDay.mutex.acquire();
            ElectionDay.kioskAvailable[i]=true;
            ElectionDay.mutex.release();
            ElectionDay.kioskHelper.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int findShortestLine () {
        int shortestLine=0;
        for (int i=0;i<ElectionDay.num_k;i++){
            if (ElectionDay.kioskLines[shortestLine].getQueueLength()>ElectionDay.kioskLines[i].getQueueLength()) {
                shortestLine=i;
            }
        }
        return shortestLine;
    }

    void enterInformation (int i) {
        try {
            msg("Voter enters information at Kiosk" + i);
            sleep(Math.abs(random.nextInt(3000)));
        } catch (InterruptedException e) {
            msg("Interrupted while entering information");
        }
    }

    void scanBallot() {
        try{
            ElectionDay.mutex.acquire();
            ElectionDay.numVotersGrouped++;
            if (ElectionDay.numVotersGrouped==ElectionDay.num_sm){
                ElectionDay.numVotersGrouped=0;
                ElectionDay.mutex.release();
                for (int i=0;i<ElectionDay.num_sm-1;i++){
                    ElectionDay.group.release();
                }
            } else {
                ElectionDay.mutex.release();
                ElectionDay.group.acquire();
            }
            ElectionDay.smHelper.acquire();
            scanTheBallot();
            ElectionDay.smVoter.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void scanTheBallot () {
        try{
            msg("Scanning Ballot");
            sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void leave () {
        try {
            msg("Leaves the building");
            ElectionDay.mutex.acquire();
            ElectionDay.finishedVoters++;
            if (ElectionDay.finishedVoters == ElectionDay.num_voters){
                msg("Last voter is gone, helpers can leave now.");
                ElectionDay.votersStillHere = false;
            }
            ElectionDay.mutex.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
