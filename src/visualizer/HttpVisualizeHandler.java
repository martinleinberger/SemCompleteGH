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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.io.*;
import java.io.BufferedOutputStream;
import java.net.URLEncoder;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.Filter;
import org.mortbay.http.*;


/**
 * Handles visual display requests received via HTTP. 
 * 
 * Incoming requests are expected to be in the form:
 * <pre>
 *  GET http://host/visualize lang= ....
 * </pre>
 * 
 * Parameters for the incoming request are:
 * <table>
 * <tr><td align=right> lang </td> <td> - Use "svg" to get an SVG file, or "visual" to get the SVG embedded in an HTML page with user controls.</td></tr>
 * <tr><td align=right> model </td> <td> - A specific model to visualize.</td></tr>
 * <tr><td align=right> r </td> <td> - A specific resource to visualize.</td></tr>
 * <tr><td align=right> search </td> <td> - A string for which to search literals.</td></tr>
 * <tr><td align=right> style </td> <td> - Use "list" for a list of matching nodes, or "arcs" to also see a list of matching predicates.</td></tr>
 * </table>
 * 
 * @author Craig Sayers
 * @version 1.0
 */
public class HttpVisualizeHandler implements HttpHandler {
	static public String PATH = "/visualize";
	protected HttpContext _context = null;
	protected boolean _started = false;
	protected HashMap _analyzedModels;

	/**
	 * An HTTP Handler for visualizing RDF Graphs.
	 * 
	 * @param directory
	 *            A directory containing rdf files to visualize. The system
	 *            will look for files ending in ".rdf", ".owl" or ".n3".
	 */
	public HttpVisualizeHandler(File directory) {
		super();
		_analyzedModels = new HashMap();
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].canRead()
						&& files[i].isFile()
						&& (files[i].getName().endsWith(".rdf")
								|| files[i].getName().endsWith(".owl") || files[i].getName()
								.endsWith(".n3"))) {
					Model model = ModelFactory.createDefaultModel();
					System.out.println("Preparing to visualize:" + files[i].getName());
					String type = null;
					if (files[i].getName().endsWith(".rdf") || files[i].getName().endsWith(".owl"))
						type = "RDF/XML";
					else if (files[i].getName().endsWith(".n3"))
						type = "N3";
					if (type != null) {
						System.out.print("\tLoading...");
						System.out.flush();
						long time = System.currentTimeMillis();
						model.read("file:" + files[i].getAbsolutePath(), type);
						System.out.println("done ("
								+ (int) ((System.currentTimeMillis() - time) / 1000 + 0.5)
								+ " seconds)");
						System.out.print("\tAnalyzing...");
						System.out.flush();
						time = System.currentTimeMillis();
						AnalyzedGraph aGraph = new AnalyzedGraph(model);
						System.out.println("done ("
								+ (int) ((System.currentTimeMillis() - time) / 1000 + 0.5)
								+ " seconds)");
						_analyzedModels.put(files[i].getName(), aGraph);
					}
				}
			}
		} else {
			throw new RuntimeException("Error, expected a directory.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.http.HttpHandler#getName()
	 */
	public String getName() {
		return "RDF Visualizer handler";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.http.HttpHandler#getHttpContext()
	 */
	public HttpContext getHttpContext() {
		return _context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.http.HttpHandler#initialize(org.mortbay.http.HttpContext)
	 */
	public void initialize(HttpContext arg0) {
		_context = arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.http.HttpHandler#handle(java.lang.String,
	 *      java.lang.String, org.mortbay.http.HttpRequest,
	 *      org.mortbay.http.HttpResponse)
	 */
	public void handle(String arg0, String arg1, HttpRequest arg2, HttpResponse arg3)
			throws HttpException, IOException {
		String method = arg2.getMethod();
		String lang = arg2.getParameter("lang");
		String render = arg2.getParameter("render");
		String model = arg2.getParameter("model");
		String searchString = arg2.getParameter("search");
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(array);
		if (method.equals(HttpRequest.__GET)
				&& (lang == null || (lang != null && lang.compareToIgnoreCase("visual") == 0))
				&& render == null) {
			String url = arg2.getURI().toString();
			if (url.indexOf("lang=") != -1)
				url = url.replaceFirst("=visual", "=svg");
			else
				url = null;
			StringBuffer pacString = new StringBuffer("<html>\n"
					+ "<HEAD><TITLE=\"RDF Search\"></HEAD>"
					+ "<BODY>"
					+ "<P>"
					+ "<table border=\"0\" cellpadding=\"3\" cellspacing=\"3\" width=\"100%\">"
					+ "<tr>"
					+ "<td align=\"left\" valign=\"top\" style=\"color:#7777ff;font-family: sans-serif\">"
					+ "<font size=\"+2\">Experimental RDF Visualizer</font>"
					+ "</td>"
					+ "<td align=\"right\" valign=\"bottom\" style=\"color:#333333;font-family: sans-serif\">"
					+ "<FORM ACTION=\"" + arg2.getRootURL().toString() + "/visualize\">"
					+ "Search <INPUT TYPE=\"hidden\" NAME=\"lang\" VALUE=\"visual\" />\n"
					+ "<SELECT NAME=model>");
			Iterator it = _analyzedModels.keySet().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				pacString.append("<OPTION value=\"" + name + "\"");
				if (model != null && model.equalsIgnoreCase(name))
					pacString.append(" SELECTED");
				pacString.append(">" + name + "</OPTION>");
			}
			pacString.append("</SELECT> for literals containing "
							+ "<INPUT TYPE=\"text\" NAME=\"search\" SIZE=\"20\" ");
			if( searchString != null)
				pacString.append("VALUE=\""+searchString+"\"");
			pacString.append("/>\n"
							+ "<INPUT NAME=\"submit\" TYPE=\"submit\" VALUE=\"search\" style=\"color:#444444;font-family: sans-serif\"/><BR>"
							+ "</FORM>" + "</td>" + "</tr>" + "</table>" + "</P>");
			if (url != null)
				pacString.append("<embed src=\""
								+ arg2.getURI().toString().replaceFirst(" type=", "").replaceFirst(
										"=visual", "=svg")
								+ "\" type=\"image/svg+xml\" pluginspace=\"http://www.adobe.com/svg/viewer/install/\" width=\"1200\" height=\"2400\" >");
			else
				pacString.append("<P align=\"center\">Welcome.  To begin, select a file from the drop-down list and hit [search].<br>"
								+ "To narrow your view, enter a search word or prefix ending in * into the text box above.<br></P>"
								+ "<P align=\"center\">Browse the resulting visualization by clicking on any node or arc.</P>"
								+ "<P align=\"center\">To view the visualization you'll need browser support for Scalable Vector Graphics<br>"
								+ "one example is the Adobe SVG plugin for Internet Explorer.</P>");
			pacString.append("</BODY> </html>");
			arg3.setStatus(HttpResponse.__200_OK);
			arg3.setField(HttpFields.__ContentType, "text/html");
			OutputStream os = arg3.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			bos.write(pacString.toString().getBytes());
			bos.flush();
			os.flush();
		} else if (method.equals(HttpRequest.__GET) && model != null && lang != null
				&& lang.compareToIgnoreCase("svg") == 0) {
			NodeToSVG visualizer = null;
			String resourceString = arg2.getParameter("r");
			String styleString = arg2.getParameter("style");
			AnalyzedGraph aModel = (AnalyzedGraph) _analyzedModels.get(model);
			if (resourceString == null && (searchString == null || searchString.length()==0)) {
				ModelToSVG modelVis = new ModelToSVG();
				NodeToSVG.PageInfo pageInfo = new NodeToSVG.PageInfo();
				pageInfo.resourceToHREF = new myResourceToHREF(model, searchString, "list");
				pageInfo.propertyToHREF = new myResourceToHREF(model, searchString, "arcs");
				pageInfo.maxBackArcs = 0;
				pageInfo.maxForwardArcs = 0;
				modelVis.visualizeStart(out, pageInfo);
				Filter nodeFilter = Filter.any;
				modelVis.visualizeModel(out, aModel, nodeFilter, pageInfo);
				modelVis.visualizeEnd(out, pageInfo);
				visualizer = modelVis;
			} else 	if (resourceString != null) {
				resourceString = resourceString.replaceAll(";hash;", "#").replaceAll(";dot;", ".");
				Resource toDisplay;
				Model tempModel = ModelFactory.createDefaultModel();
				if (resourceString.startsWith("_")) {
					resourceString = resourceString.substring(1); // remove leading "_"
					toDisplay = tempModel.createResource(new AnonId(resourceString));
				} else
					toDisplay = tempModel.createResource(resourceString);
				ArrayList modelList = new ArrayList(); // list of models from
				// which to display
				// results
				modelList.add(model);
				Iterator it = _analyzedModels.keySet().iterator();
				while (it.hasNext()) {
					String modelName = (String) it.next();
					AnalyzedGraph potentialModel = (AnalyzedGraph) _analyzedModels.get(modelName);
					if (!modelList.contains(modelName)
							&& potentialModel._nodes.containsKey(toDisplay)) {
						modelList.add(modelName);
					}
				}
				NodeToSVG.PageInfo pageInfo = new NodeToSVG.PageInfo();
				if (searchString != null) {
					pageInfo.textToHightlight = searchString;
				}
				for (int i = 0; i < modelList.size(); i++) {
					String modelName = (String) modelList.get(i);
					pageInfo.resourceToHREF = new myResourceToHREF(modelName, null, "list");
					pageInfo.propertyToHREF = new myResourceToHREF(modelName, null, "arcs");
					pageInfo.maxBackArcs = 1;
					pageInfo.maxForwardArcs = 2;
					AnalyzedGraph modelToVisualize = (AnalyzedGraph) _analyzedModels.get(modelName);
					if (modelToVisualize == null) {
						visualizer = null;
						break;
					}
					if (searchString != null) {
						pageInfo.textToHightlight = searchString;
						pageInfo.literalsToHighlight = modelToVisualize
								.findLiteralNodeInfos(searchString);
					}
					visualizer = new NodeToSVG();
					if (i == 0) {
						visualizer.visualizeStart(out, pageInfo);
					}
					visualizer.visualizeSubHeading(out, pageInfo, modelName);
					visualizer.advancePage(pageInfo);
					if (i == 0 && styleString != null && styleString.equalsIgnoreCase("arcs")) {
						// first look for and display any nodes with a matching
						// predicate
						SortedSet arcs = modelToVisualize.findArcInfos(toDisplay);
						if (arcs != null) {
							Iterator ait = arcs.iterator();
							pageInfo.maxBackArcs = 0;
							pageInfo.maxForwardArcs = 1;
							int previousMaxLiteralLines = pageInfo.maxLiteralLines;
							pageInfo.maxLiteralLines = 2;
							pageInfo.ySpacing *= 0.5;
							if (searchString != null)
								pageInfo.literalsToHighlight = modelToVisualize
										.findLiteralNodeInfos(searchString);
							int count = 0;
							while (ait.hasNext() && count <= 10) {
								AnalyzedGraph.ArcInfo ainfo = (AnalyzedGraph.ArcInfo) ait.next();
								if (count++ == 10 && arcs.size() > 11) {
									visualizer.visualizeVerticalContinuation(out, pageInfo);
									ainfo = (AnalyzedGraph.ArcInfo) arcs.last();
								}
								visualizer.visualizeNodeInfo(out, modelToVisualize, ainfo.start,
										Filter.any, new EqualityFilter(ainfo),
										pageInfo);
							}
							pageInfo.ySpacing *= 2.0;
							pageInfo.maxLiteralLines = previousMaxLiteralLines;
							if (arcs.size() > 0)
								visualizer.advancePage(pageInfo, 2.0);
							pageInfo.maxBackArcs = 2;
							pageInfo.maxForwardArcs = 2;
						}
					}
					visualizer.visualizeNode(out, modelToVisualize, toDisplay, Filter.any,
							Filter.any, pageInfo);
					if (i == modelList.size() - 1)
						visualizer.visualizeEnd(out, pageInfo);
				}
			} else if (resourceString == null && searchString != null) {
				// Find all literals with that text
				HashMap results;
				if (searchString == null || searchString.length() == 0)
					results = null;
				else
					results = aModel.findTypedSubjectNodeInfos(searchString);
				if (results == null || results.size() == 0) {
					visualizer = null;
				} else {
					visualizer = new NodeToSVG();
					NodeToSVG.PageInfo pageInfo = new NodeToSVG.PageInfo();
					pageInfo.resourceToHREF = new myResourceToHREF(model, searchString, "list");
					pageInfo.propertyToHREF = new myResourceToHREF(model, searchString, "arcs");
					pageInfo.maxBackArcs = 0;
					pageInfo.maxForwardArcs = 1;
					pageInfo.textToHightlight = searchString;
					pageInfo.literalsToHighlight = aModel.findLiteralNodeInfos(searchString);
					pageInfo.maxLiteralLines = 3;
					visualizer.visualizeStart(out, pageInfo);
					boolean endEarly = false;
					Iterator types = results.keySet().iterator();
					while (types.hasNext() && !endEarly) {
						AnalyzedGraph.NodeInfo typeNode = (AnalyzedGraph.NodeInfo) types.next();
						visualizer.visualizeSubHeading(out, pageInfo, typeNode.longLabel);
						Set set = (Set) results.get(typeNode);
						Iterator resultIt = set.iterator();
						while (resultIt.hasNext() && !endEarly) {
							AnalyzedGraph.NodeInfo nodeInfo = (AnalyzedGraph.NodeInfo) resultIt
									.next();
							visualizer.visualizeNodeInfo(out, aModel, nodeInfo, Filter.any,
									new ArcInfoDestinationNodeFilter(
											pageInfo.literalsToHighlight), pageInfo);
							if (pageInfo.yStart > 250 && resultIt.hasNext()) {
								visualizer.visualizeSubHeading(out, pageInfo,
										"(too many results to display)");
								endEarly = true;
							}
						}
						visualizer.advancePage(pageInfo);
					}
					visualizer.visualizeEnd(out, pageInfo);
				}
			} else {
				// error
			}
			if (visualizer == null) {
				visualizer = new NodeToSVG();
				NodeToSVG.PageInfo pageInfo = new NodeToSVG.PageInfo();
				pageInfo.resourceToHREF = new myResourceToHREF(null, searchString, null);
				pageInfo.propertyToHREF = new myResourceToHREF(null, searchString, null);
				visualizer.visualizeStart(out, pageInfo);
				if (searchString == null)
					visualizer.visualizeSubHeading(out, pageInfo,
							"Sorry, no matching resources found.");
				else
					visualizer.visualizeSubHeading(out, pageInfo,
							"Sorry, no matches found for search: \"" + searchString + "\"");
				visualizer.visualizeEnd(out, pageInfo);
			}
			String pacString = array.toString();
			arg3.setStatus(HttpResponse.__200_OK);
			//arg3.setField(HttpFields.__ContentEncoding, "image/svg+xml");
			arg3.setField(HttpFields.__ContentType, "image/svg+xml");
			OutputStream os = arg3.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			bos.write(pacString.getBytes("UTF-8"));
			bos.flush();
			os.flush();
			/* } */
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.util.LifeCycle#start()
	 */
	public void start() throws Exception {
		_started = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.util.LifeCycle#stop()
	 */
	public void stop() throws InterruptedException {
		_started = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mortbay.util.LifeCycle#isStarted()
	 */
	public boolean isStarted() {
		return _started;
	}
	
	private static class myResourceToHREF implements NodeToSVG.ResourceToString {
		String _search;
		String _modelName;
		String _style;

		public myResourceToHREF(String modelName, String search, String style) {
			_search = search;
			_modelName = modelName;
			_style = style;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.rdf.visualize.RDFNodeToHREF#convert(com.hp.hpl.jena.rdf.model.RDFNode)
		 */
		public String convert(Resource node) {
			try {
				String searchString;
				if (_search == null)
					searchString = "";
				else
					searchString = "&amp;search=" + URLEncoder.encode(_search, "UTF-8");
				String uri;
				if (node.isAnon())
					uri = URLEncoder.encode("_" + node.getId(), "UTF-8");
				else
					uri = URLEncoder.encode(node.getURI(), "UTF-8");
				uri = uri.replaceAll("#", ";hash;");//.replaceAll("\\.",";dot;");
				return PATH + "?lang=visual"
							+ ((_modelName != null) ? "&amp;model=" + _modelName : "")
							+ "&amp;r=" + uri
							+ searchString
							+ ((_style != null) ? "&amp;style=" + _style : "");
			} catch (Exception e) {
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.rdf.visualize.NodeVisualizer.ResourceToString#convert(com.hp.hpl.jena.rdf.model.Resource,
		 *      int)
		 */
		public String convert(Resource resource, int length) {
			return convert(resource);
		}
	}
	
	private static class ArcInfoDestinationNodeFilter implements Filter {
		
		Set _set;
		
		public ArcInfoDestinationNodeFilter( Set set ) {
			_set = set;
		}
		
		/* (non-Javadoc)
		 * @see com.hp.hpl.jena.util.iterator.Map1#map1(java.lang.Object)
		 */
		public boolean accept(Object o) {
			AnalyzedGraph.ArcInfo arcInfo = (AnalyzedGraph.ArcInfo)o;			
			AnalyzedGraph.NodeInfo end = arcInfo.end;			
			return _set.contains(end);
		}		
	}
	
	
	private static class EqualityFilter implements Filter {
		Object object;

		public EqualityFilter(Object o) {
			object = o;
		}

		public boolean accept(Object o) {
			return o.equals(object);
		}
	}


}
