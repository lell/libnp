package programs;

import statistics.Generator;
import static util.Operation.range;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class derive_gem {
	public static void main(String[] args) {
		double alpha = 1.0;
		double beta = 1.0;
		Generator gen = new Generator();
		int n = 6;
		int iters = 100;

		CommandLine cmdline = null;
		{
			CommandLineParser parser = new PosixParser();
			final Options options = new Options();
			options.addOption("iters", true, "#iters (default: " + iters + ")");
			options.addOption("n", true, "n (default: " + n + ")");
			options.addOption(
					"alpha", true, "value of alpha (default: " + alpha + ")");
			
			options.addOption(
					"beta", true, "value of beta (default: " + beta + ")");
			
			options.addOption("method", true, "crp/gem");

			try {
				cmdline = parser.parse(options, args);
			} catch (ParseException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}
		
		if (!cmdline.hasOption("method")) {
			System.err.println("Must specify '-method crp' or '-metod gem'");
			System.exit(-1);
		}
		
		if (cmdline.hasOption("iters")) {
			iters = Integer.parseInt(cmdline.getOptionValue("iters"));
		}
		
		if (cmdline.hasOption("n")) {
			n = Integer.parseInt(cmdline.getOptionValue("n"));
		}
		
		if (cmdline.hasOption("alpha")) {
			alpha = Double.parseDouble(cmdline.getOptionValue("alpha"));
		}
		
		if (cmdline.hasOption("beta")) {
			beta = Double.parseDouble(cmdline.getOptionValue("beta"));
		}
		
		if (cmdline.getOptionValue("method").equals("crp")) {
			for(int iter = 0; iter < iters; iter++) {
				Set<Set<Double>> bot = new HashSet();
				for(Set<Set<Double>> union :
					gen.drawCRP(gen.drawCRP(range(n), alpha), beta)) {
					
					Set<Double> element = new HashSet();
					for (Set<Double> block : union) {
						element.addAll(block);
					}
					bot.add(element);
				}
		
				for (Set<Double> element :bot) {
					System.out.print(element.size() + " ");
				}
				System.out.println();
			}
		} else if (cmdline.getOptionValue("method").equals("gem")) {
	
			for(int iter = 0; iter < iters; iter++) {
				Map<Integer, Set<Double>> classes = new HashMap();
				List<Double> omegas = new ArrayList();
				List<Double> rhos = new ArrayList();
				double omega_pr = 1.0;
				double omega_sum = 0.0;
				double rho_sum = 0.0;
				for (double i = 1; i <= n; i++) {
					double U = gen.nextUniform(0, 1);
					boolean found = false;
					int k = 0;
					double cdf = 0;
					while (!found) {
						if (k >= rhos.size()) {	
							double xi = gen.nextBeta(1.0, alpha);
							omegas.add(xi  * omega_pr);
							omega_pr *= (1.0 - xi);

							omega_sum += omegas.get(k);
							double zeta = gen.nextBeta(beta * omegas.get(k),
									beta * (1.0 - omega_sum));
							
							double rho = zeta * (1.0 - rho_sum);
							rhos.add(rho);
							
							rho_sum += rho;
						}
						cdf += rhos.get(k);
						if (cdf >= U) {
							if (!classes.containsKey(k)) {
								classes.put(k, new HashSet());
							}
							classes.get(k).add(i);
							found = true;
						}
						k += 1;
					}
				}
				for (Integer k : classes.keySet()) {
					System.out.print(classes.get(k).size() + " ");
				}
				System.out.println();
			}
		} else {
			System.err.println("Error: -method must be 'crp' or 'gem'");
		}
	}
}
