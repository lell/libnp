/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.mcmc.collectors;

import java.io.*;

public class Filer implements Collector {
	Collectable[] clients;
	String[] properties;
	Object[] args;
	PrintStream stream;

	public Filer(Collectable client, PrintStream stream) {
		this.clients = new Collectable[] { client };
		this.stream = stream;
	}

	public Filer(Collectable client, String filename) {
		this.clients = new Collectable[] { client };
		try {
			this.stream = new PrintStream(filename);
		} catch (IOException ee) {
			System.out.println("Unable to open " + filename + ": "
					+ ee.getMessage());
			if (stream != null) {
				stream.close();
			}
		}
	}

	public Filer(Collectable client, PrintStream stream, String[] properties, Object[] args) {
		
		this(client, stream);
		this.properties = properties;
		this.args = args;

		for (String property : properties) {
			stream.print(property + " ");
		}
		stream.println();
	}

	public Filer(Collectable client, String filename, String[] properties, Object[] args) {
		this(client, filename);
		this.properties = properties;
		this.args = args;

		String first = "";
		for (String property : properties) {
			assert stream != null;
			stream.print(first + property);
			first = " ";
		}
		stream.println();
	}

	public Filer(Collectable client, String[] properties, Object[] args) {
		this(client, System.out, properties, args);
	}

	public Filer(Collectable client, PrintStream stream, String[] properties) {
		this(client, stream, properties, null);
	}

	public Filer(Collectable client, String filename, String[] properties) {
		this(client, filename, properties, null);
	}

	public Filer(Collectable client, String[] properties) {
		this(client, properties, null);
	}
	
	public Filer(Collectable[] clients, PrintStream stream) {
		this.clients = clients;
		this.stream = stream;
	}

	public Filer(Collectable[] clients, String filename) {
		this.clients = clients;
		try {
			this.stream = new PrintStream(filename);
		} catch (IOException ee) {
			System.out.println("Unable to open " + filename + ": "
					+ ee.getMessage());
			if (stream != null) {
				stream.close();
			}
		}
	}

	public Filer(Collectable[] clients, PrintStream stream, String[] properties, Object[] args) {
		
		this(clients, stream);
		this.properties = properties;
		this.args = args;

		for (String property : properties) {
			stream.print(property + " ");
		}
		stream.println();
	}

	public Filer(Collectable[] clients, String filename, String[] properties, Object[] args) {
		this(clients, filename);
		this.properties = properties;
		this.args = args;

		String first = "";
		for (String property : properties) {
			assert stream != null;
			stream.print(first + property);
			first = " ";
		}
		stream.println();
	}

	public Filer(Collectable[] clients, String[] properties, Object[] args) {
		this(clients, System.out, properties, args);
	}

	public Filer(Collectable[] clients, PrintStream stream, String[] properties) {
		this(clients, stream, properties, null);
	}

	public Filer(Collectable[] clients, String filename, String[] properties) {
		this(clients, filename, properties, null);
	}

	public Filer(Collectable[] clients, String[] properties) {
		this(clients, properties, null);
	}

	public void setProperties(String[] properties, Object[] args) {
		this.properties = properties;
		this.args = args;
		assert properties.length == args.length;
	}

	@Override
	public void collect() {
		int len = properties.length;
		for (int ii = 0; ii < len; ii++) {
			Collectable client;
			if (clients.length == 1) { 
				client = clients[0];
			} else {
				client = clients[ii];
			}
			
			Object sample;
			if (args == null || args[ii] == null) {
				sample = client.get(properties[ii]);
			} else {
				sample = client.get(properties[ii], args[ii]);
			}
			assert sample != null : properties[ii];
			assert stream != null;
			stream.print(sample.toString() + " ");
		}
		stream.println();
		stream.flush();
	}

	@Override
	public void close() {
		stream.close();
	}
}
