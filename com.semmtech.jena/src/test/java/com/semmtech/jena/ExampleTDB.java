package com.semmtech.jena;


import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.tdb.TDBFactory;


public class ExampleTDB {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String directory = "C:\\Temp\\TDB";
        @SuppressWarnings("unused")
        String benchmark = "C:\\Temp\\TDB\\dataset_1m.ttl";

        Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.READ);
        long start = Calendar.getInstance().getTimeInMillis();
        Model model = dataset.getDefaultModel();
        for (Iterator<?> iter = dataset.listNames(); iter.hasNext();) {
            System.out.println(String.format("Name = '%s'", iter.next()));
        }
        List<Statement> statements = model.listStatements().toList();
        System.out.println(String.format("Count is %s", statements.size()));
        System.out.println(String.format("Process took %s ms.", Calendar.getInstance()
                .getTimeInMillis() - start));
        dataset.end();

        // dataset.begin(ReadWrite.WRITE);
        // try {
        // long start = GregorianCalendar.getInstance().getTimeInMillis();
        // Model model = dataset.getDefaultModel();
        // model.read(new FileInputStream(new File(benchmark)), null,
        // FileUtils.langTurtle);
        //
        // System.out.println(String.format("Count is %s",
        // model.listStatements().toList().size()));
        // System.out.println(String.format("Read took %s ms.",
        // GregorianCalendar.getInstance().getTimeInMillis()
        // - start));
        // dataset.commit();
        // System.out.println(String.format("Process took %s ms.",
        // GregorianCalendar.getInstance().getTimeInMillis()
        // - start));
        // }
        // catch (Exception ex) {
        // ex.printStackTrace();
        // }
        // dataset.end();

        dataset.close();
    }
}
