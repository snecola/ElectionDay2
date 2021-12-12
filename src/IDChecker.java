public class IDChecker extends Thread{

    public static long time = System.currentTimeMillis();
    int id;

    public IDChecker(int id) {
        this.id = id;
        setName("IDChecker"+id);
    }
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }

    @Override
    public void run () {
        try {
            while (ElectionDay.votersStillHere){
                ElectionDay.idCheckersBusy.acquire();
                msg("Checked Voter ID");
                ElectionDay.idCheckersAvailable.release();
            }
            msg("Goes home");
        } catch (InterruptedException e){
            msg("Goes home");
        }
    }
}
