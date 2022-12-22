package io.anserini.index.generator;

import io.anserini.collection.JsonCollection;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.*;
import org.apache.lucene.geo.Line;
import org.apache.lucene.geo.Polygon;
import org.apache.lucene.geo.SimpleWKTShapeParser;

import java.io.IOException;
import java.text.ParseException;

public class GeoGenerator implements LuceneDocumentGenerator<JsonCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(GeoGenerator.class);

  protected IndexCollection.Args args;

  public GeoGenerator(IndexCollection.Args args) {
    this.args = args;
  }

  @Override
  public Document createDocument(JsonCollection.Document geoDoc) {
    Document doc = new Document();

    // Add ID field
    String id = geoDoc.id();
    doc.add(new StringField(Constants.ID, id, Field.Store.YES));

    // Store the raw JSON
    if (args.storeRaw) {
      doc.add(new StoredField(Constants.RAW, geoDoc.raw()));
    }

    geoDoc.fields().forEach((k, v) -> {
      if ("geometry".equals(k)) {
        // parse the geometry fields using SimpleWKTParser and index them
        try {
          Object shape = SimpleWKTShapeParser.parse(v);

          Field[] fields = new Field[0];
          if (shape instanceof Line) {
            Line line = (Line) shape;
            fields = LatLonShape.createIndexableFields("geometry", line);

            for (int i = 0; i < line.numPoints(); i++) {
              doc.add(new LatLonDocValuesField("point", line.getLat(i), line.getLon(i)));
            }

          } else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon) shape;
            fields = LatLonShape.createIndexableFields("geometry", polygon);

            for (int i = 0; i < polygon.numPoints(); i++) {
              doc.add(new LatLonDocValuesField("point", polygon.getPolyLat(i), polygon.getPolyLon(i)));
            }

          } else if (shape instanceof Line[]) {
            for (Line line: (Line[]) shape) {
              fields = LatLonShape.createIndexableFields("geometry", line);

              for (int i = 0; i < line.numPoints(); i++) {
                doc.add(new LatLonDocValuesField("point", line.getLat(i), line.getLon(i)));
              }
            }

          } else if (shape instanceof Polygon[]) {
            for (Polygon polygon: (Polygon[]) shape) {
              fields = LatLonShape.createIndexableFields("geometry", polygon);

              for (int i = 0; i < polygon.numPoints(); i++) {
                doc.add(new LatLonDocValuesField("point", polygon.getPolyLat(i), polygon.getPolyLon(i)));
              }
            }

          } else {
            throw new IllegalArgumentException("unknown shape");
          }

          for (Field f: fields) {
            doc.add(f);
          }
        } catch (ParseException | IOException e) {
          LOG.error("Error parsing unknown shape using SimpleWKTShapeParser: " + v);
        } catch (IllegalArgumentException e) {
          LOG.error("Error casting shape to any of the types Line, Line[], Polygon, Polygon[]: " + v);
        }

      } else {
        // go through all the non-geometry fields and try to index them as int or long if possible
        try {
          long vLong = Long.parseLong(v);
          doc.add(new LongPoint(k, vLong));
          doc.add(new StoredField(k, v));
        } catch (NumberFormatException e1) {
          try {
            double vDouble = Double.parseDouble(v);
            doc.add(new DoublePoint(k, vDouble));
            doc.add(new StoredField(k, v));
          } catch (NumberFormatException e2) {
            doc.add(new StringField(k, v, Field.Store.YES));
          }
        }
      }
    });

    return doc;
  }
}
