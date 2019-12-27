/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.kg;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FreebaseTest {
  @Test(expected = NoSuchElementException.class)
  public void test1() throws Exception {
    Freebase freebase = new Freebase(Paths.get("src/test/resources/freebase-rdf-head100.gz"));

    FreebaseNode node;
    Iterator<FreebaseNode> iter = freebase.iterator();
    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/american_football.football_player.footballdb_id>", node.uri());
    assertEquals(9, node.getPredicateValues().size());
    assertEquals("fb:american_football.football_player.footballdb_id",
        FreebaseNode.cleanUri("fb:american_football.football_player.footballdb_id"));
    assertEquals("fb:type.object.name",
        FreebaseNode.cleanUri(node.getPredicateValues().keySet().iterator().next()));

    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/astronomy.astronomical_observatory.discoveries>", node.uri());
    assertEquals(9, node.getPredicateValues().size());

    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/automotive.body_style.fuel_tank_capacity>", node.uri());
    assertEquals(9, node.getPredicateValues().size());

    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/automotive.engine.engine_type>", node.uri());
    assertEquals(10, node.getPredicateValues().size());

    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/automotive.trim_level.max_passengers>", node.uri());
    assertEquals(9, node.getPredicateValues().size());

    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/aviation.aircraft.first_flight>", node.uri());
    assertEquals(9, node.getPredicateValues().size());

    assertTrue(iter.hasNext());
    node = iter.next();
    assertEquals("<http://rdf.freebase.com/ns/award.award_winner>", node.uri());
    Map<String, List<String>> map = node.getPredicateValues();
    assertEquals(1, node.getPredicateValues().size());
    assertEquals(45, map.get("<http://rdf.freebase.com/ns/type.type.instance>").size());

    assertFalse(iter.hasNext());
    iter.next();
  }
}