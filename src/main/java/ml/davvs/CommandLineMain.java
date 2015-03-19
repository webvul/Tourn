package ml.davvs;

public class CommandLineMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting command line main!");
		Tournament t;
		System.out.println("Creating tournament!");
		t = new Tournament();
		t.setName("DvOfflineTournament");
		System.out.println("Tournament " + t.getName() + " created!");
	}

}
