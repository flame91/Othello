package othello;

public class LoginThreadNotifier extends Thread{
	Object objPermission;
	WinLogin wl;

	public LoginThreadNotifier(Object objPermission, WinLogin wl) {
		super();
		this.objPermission = objPermission;
		this.wl = wl;
	}
	
	@Override
	public void run() {
	
		if(objPermission.toString().equals("true")) {
			synchronized(wl) {
				wl.notify();
				System.out.println("º¸³Â´Ù?");
			}
		}
	
	}
	
	
	
}
