package myGameV2;

public class Enviorment {//it no worky rn :(
	
	public double px;
	public double py;
	
	public Enviorment(double px, double py) {
		this.px=Player.get(px);
		this.py=Player.get(py);
	}
	public void update() {
		System.out.println(px+ " " + py);
	}
}
