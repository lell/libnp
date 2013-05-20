/*
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 * All rights reserved.
 */

package libnp.mcmc.collectors;

import java.io.*;

public class Filer implements Collector {
	Collectable client;
	String[] properties;
	Object[] args;
	PrintStream output;

	public Filer(Collectable client_, PrintStream stream) {
		client = client_;
		output = stream;
	}
	public Filer(Collectable client_, String filename) {
		client = client_;
		try {
			output = new PrintStream(filename);
		} catch (IOException ee) {
			System.out.println("Unable to open "+filename+": "+ee.getMessage());
			if ( output != null ) output.close();
		}
	}
	public Filer(Collectable client_, PrintStream stream,
			String[] properties_, Object[] args_) {
		this(client_,stream);
		properties = properties_;
		args = args_;

		for (String property : properties) {
			output.print(property + " ");
		}
		output.println();
	}
	public Filer(Collectable client_, String filename,
			String[] properties_, Object[] args_) {
		this(client_,filename);
		properties = properties_;
		args = args_;

		String first = "";
		for (String property : properties) {
			assert output != null;
			output.print(first + property);
			first = " ";
		}
		output.println();
	}
	public Filer(Collectable client_, String[] properties_, Object[] args_) {
		output = System.out;
		client = client_;
		properties = properties_;
		args = args_;
	}	
	public Filer(Collectable client_, PrintStream stream, String[] properties_) {
		this(client_,stream,properties_,null);
		args = new Object[properties.length];
	}
	public Filer(Collectable client_, String filename,String[] properties_) {
		this(client_,filename,properties_,null);
		args = new Object[properties.length];
	}
	public Filer(Collectable client_, String[] properties_) {
		this(client_,properties_,null);
		args = new Object[properties.length];
	}
	public void setProperties(String[] properties_, Object[] args_) {
		properties = properties_;
		args = args_;
		assert properties.length == args.length;
	}

	public void collect() {
		int len = properties.length;
		for ( int ii = 0 ; ii < len ; ii ++ ) {
			Object sample;
			if ( args[ii] == null ) {
				sample = client.get(properties[ii]);
			} else {
				sample = client.get(properties[ii],args[ii]);
			}
			assert sample != null : properties[ii];
			assert output != null;
			output.print(sample.toString()+" ");
		}
		output.println();
		output.flush();
	}

	public void close() {
		output.close();
	}
}
