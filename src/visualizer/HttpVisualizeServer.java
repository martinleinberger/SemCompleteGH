/*
 * Copyright (c) 2004 Hewlett-Packard Development Company, L.P.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

/*
 * This is the part of the source code for an experimental RDF Graph Visualizer 
 * developed at HP Labs, Palo Alto. Please note that this was never intended to 
 * be of production quality. It was written quickly because we needed an internal
 * solution and is made available in the hope that others might find it useful.
 * We are unfortunately not able to offer any support for this experimental software.
 * 
 * For further information please see:
 *    http://www.hpl.hp.com/personal/Craig_Sayers/rdf/visual
 *  
 */
package visualizer;

import org.mortbay.util.MultiException;
import org.mortbay.http.*;
import java.io.File;

/**
 * Acts as an HTTP server publishing a search and visualization handler.
 * 
 * @author Craig Sayers
 * @version 1.0
 */
public class HttpVisualizeServer {
	static int DEFAULT_PORT = 8181;
	protected HttpServer _server = null;
	protected HttpContext _context = null;
	protected int _port = -1;
	protected File _directory = null;

	/**
	 * Creates a server.
	 * 
	 * @param directory
	 *            Location from which to generate visualization.  
	 *            This is  a directory to be searched for any rdf files.
	 * @param port
	 *            the number of the port on which to service incoming requests
	 * @throws Exception 
	 */
	public HttpVisualizeServer(File directory, int port) throws Exception {
		_directory = directory;
		_port = port;
		open();
	}

	/**
	 * Creates a server using the default port
	 * @param directory Location from which to generate visualization.  
	 *        This is  a directory to be searched for any rdf files.
	 * @throws Exception 
	 */
	public HttpVisualizeServer(File directory) throws Exception {
		this(directory, DEFAULT_PORT);
	}

	/**
	 * Opens the server for business.
	 * @throws Exception 
	 */
	protected synchronized void open() throws Exception {
		// Create the server
		_server = new HttpServer();
		
		// Create a port listener
		SocketListener listener = new SocketListener();
		listener.setPort(_port);
		_server.addListener(listener);
		
		// Create a context and add it
		_context = new HttpContext();
		_context.setContextPath(HttpVisualizeHandler.PATH);
		_server.addContext(_context);
		_context.addHandler(new HttpVisualizeHandler(_directory));
		try {
			_server.start();
		} catch (MultiException e) {
			throw new RuntimeException("Error, unable to start server", e);
		}
	}

	/**
	 * Close the server
	 */
	public synchronized void close() {
		if (_server != null) {
			_server.removeContext(_context);
			_context = null;
			try {
				_server.stop(true); // try to stop gracefully
			} catch (Exception e) {
				try {
					_server.stop(false); // force a stop
				} catch (Exception e2) {
					; // nothing we can do
				}
			}
			try {
				_server.destroy();
			} catch (Exception e) {
				; // nothing we can do
			}
			_server = null;
		}
	}

	/**
	 * Main routine for an RDF visualizer that responds to HTTP requests.
	 * The first argument is a directory name.  Every file in the directory 
	 * with a plausable rdf extension (.rdf, .owl, .n3) is loaded for visualization.
	 * @param argv starting arguments.  
	 * @throws Exception 
	 */
	public static void main(String[] argv) throws Exception {
		if( argv.length != 1) {
			System.err.println("There must be exactly one argument and it should specify a directory from which to load files for visualization.");
			return;
		}
		File directory = new File(argv[0]);
		if( !directory.isDirectory()) {
			System.err.println("The first argument is not a directory - unable to load files for visualization.");
			return;
		}
		new HttpVisualizeServer(directory);
		System.out.println("Server is running - to view the visualization visit: http://localhost:"+DEFAULT_PORT+HttpVisualizeHandler.PATH);
		while(true) { // run forever
			try {
				Thread.sleep(360000); // 1 hour
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
