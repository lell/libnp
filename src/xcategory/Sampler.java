package xcategory;

public class Sampler {
	Sampleable state;
	int burnin;
	int iters;
	int num_thin;
	
	public Sampler(Sampleable state) {
		this.state = state;
	}
	
	public void run() {
		for (int burn = 0; burn < burnin; burn++) {
			state.resample(false);
		}
		for (int iter = 0; iter < iters; iter++) {
			for (int thin = 0; thin < num_thin; thin++) {
				state.resample(false);
			}
			state.resample(true);
		}
	}
}
