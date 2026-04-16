package com.semmtech.semantics.tdb;


public class ExampleTDB {

    /**
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        String tdbDirectory = "C:\\TDBLoadGeoCoordinatesAndLabels";
        String dbdump0 = "C:\\Users\\Public\\Documents\\TDB\\dbpedia_3.8\\dbpedia_3.8.owl";
        String dbdump1 = "C:\\Users\\Public\\Documents\\TDB\\geo_coordinates_en\\geo_coordinates_en.nt";

        // Model tdbModel = TDBFactory.createModel(tdbDirectory);
        // /* Incrementally read data to the Model, once per run , RAM > 6 GB */
        // FileManager.get().readModel(tdbModel, dbdump0);
        // FileManager.get().readModel(tdbModel, dbdump1, "N-TRIPLES");
        //
        // tdbModel.close();
    }
}
