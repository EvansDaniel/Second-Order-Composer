public class Edge {
	private double chance;
	private int pause;
	private int octave;
	private int velocity;
	private int keyNote;

	public Edge(double chance, int pause, int velocity, int keyNote, int octave) {
		this.chance = chance;
		this.pause = pause;
		this.velocity = velocity;
		this.octave = octave; // wanted to use octave to generate better music, never go around to it
		this.keyNote = keyNote;
	}

	public int getOctave() {
		return octave;
	}

	public double getChance() {
		return chance;
	}

	public int getKeyNote() {
		return keyNote;
	}
	
	public void probability(int sum) {
		chance /= sum;
	}

	public int getDuration() {
		return pause;
	}

	public int getVelocity() {
		return velocity;
	}

	public void incChance() {
		this.chance++;
	}
	
	public void addVelocity(int velocity) {
		this.velocity += velocity;
	}
	
	public void addDuration(int pause) {
		this.pause += pause;
	}

	public void averageVelocity() {
		this.velocity = (int) Math.ceil(this.velocity/this.chance) % 1000;
	}
	public void averageDuration() {
		this.pause = (int) Math.ceil(this.pause/this.chance) % 1000;
	}
}
