public class KioskHelper extends Thread{

    public static long time = System.currentTimeMillis();
    int id;

    public KioskHelper(int id) {
        this.id = id;
        setName("KioskHelper");
    }
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }

    @Override
    public void run () {
        try {
            while(ElectionDay.votersStillHere) {
                // Waits until a voter is done with their kiosk
                ElectionDay.kioskHelper.acquire();
                // Lets the next voter in that kiosk line know a kiosk is available
                for (int i = 0; i < ElectionDay.num_k; i++) {
                    ElectionDay.mutex.acquire();
                    if (ElectionDay.kioskAvailable[i]){
                        ElectionDay.kioskLines[i].release();
                        ElectionDay.kioskAvailable[i]=false;
                        msg("Kiosk " + i + " available");
                    }
                    ElectionDay.mutex.release();
                }
            }
            msg("Goes home");
        } catch (Exception e){
            msg("Goes home");
        }
    }
}
