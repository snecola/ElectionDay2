public class ScanningHelper extends Thread{

    public static long time = System.currentTimeMillis();
    int id;

    public ScanningHelper(int id) {
        this.id = id;
        setName("ScanningHelper");
    }
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }

    @Override
    public void run () {
        try {
            while(ElectionDay.votersStillHere){
                // Waits for all voters in the room to signal they're finished
                for (int i = 0; i < ElectionDay.num_sm; i++){
                    ElectionDay.smVoter.acquire();
                }
                // Signals the next group of voters to enter the scanning machine room
                for (int i = 0; i < ElectionDay.num_sm; i++){
                    ElectionDay.smHelper.release();
                }
                msg("Signaled next group of voters to enter");
            }
            msg("Goes home");
        } catch (Exception e) {
            msg("Goes home");
        }
    }
}
