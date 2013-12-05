package rdfmaker;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Martin
 * Date: 05.12.13
 * Time: 00:26
 * To change this template use File | Settings | File Templates.
 */
public class ReverseWay {

    public static void main(String args[]) throws FileNotFoundException {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream("data/outputs/log4j-1.2.15/rdf/1.rdf"), "RDF");


        String queryString = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX sem:<http://www.semcomplete.com#>" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
                "SELECT DISTINCT ?s ?p ?o WHERE {" +
                    "?s rdf:type \"http://www.semcomplete.com#POINT_IN_TIME\". ?s sem:pit_index ?index. ?s ?p ?o. } ORDER BY ASC(xsd:integer(?index))";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            String sequenceSubject = null;
            String sequencePredicate= null;
            String sequenceObject =  null;

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                String predicate = solution.get("p").toString();
                if (predicate.equals("http://www.semcomplete.com#code"))
                    sequenceSubject = solution.getLiteral("o").toString();
                if (!predicate.equals("http://www.semcomplete.com#pit_index") && !predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") && !predicate.equals("http://www.semcomplete.com#code")) {
                    sequencePredicate = predicate;
                    sequenceObject = solution.get("o").toString();
                }

                if (sequenceSubject != null && sequencePredicate != null && sequenceObject != null) {
                    System.out.println(sequenceSubject + " " + sequencePredicate + " " + sequenceObject + ".");
                    sequenceSubject = null;
                    sequencePredicate = null;
                    sequenceObject = null;
                }
            }
        } finally {qexec.close(); }

    }
}
