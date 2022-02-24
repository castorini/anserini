package io.anserini.index.generator;

import io.anserini.collection.JsonCollection;
import io.anserini.index.IndexArgs;
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

  protected IndexArgs args;

  public GeoGenerator(IndexArgs args) {
    this.args = args;
  }

  @Override
  public Document createDocument(JsonCollection.Document geoDoc) {
    Document doc = new Document();

    // Store the raw JSON
    if (args.storeRaw) {
      doc.add(new StoredField(IndexArgs.RAW, geoDoc.raw()));
    }

    geoDoc.fields().forEach((k, v) -> {
      if ("geometry".equals(k)) {
        // parse the geometry fields using SimpleWKTParser and index them
        try {
          Object shape = SimpleWKTShapeParser.parse(v);

          Field[] fields = new Field[0];
          if (shape instanceof Line) {
            fields = LatLonShape.createIndexableFields("geometry", (Line) shape);
          } else if (shape instanceof Polygon) {
            fields = LatLonShape.createIndexableFields("geometry", (Polygon) shape);
          } else if (shape instanceof Line[]) {
            for (Line line: (Line[]) shape) {
              fields = LatLonShape.createIndexableFields("geometry", line);
            }
          } else if (shape instanceof Polygon[]) {
            for (Polygon polygon: (Polygon[]) shape) {
              fields = LatLonShape.createIndexableFields("geometry", polygon);
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
