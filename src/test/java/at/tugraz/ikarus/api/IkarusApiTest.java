package at.tugraz.ikarus.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class IkarusApiTest {

private final String url = "http://localhost:8080";
//final String url = "http://muffin-ti.me:8084";
//final String url = "http://coronet2.iicm.tugraz.at:8082/";
private IkarusApi client = new IkarusApi(url);

@Test
public void example() throws IOException {

    assertEquals("Hello User !", client.hello("User"));

    String valid_json = "[{\"some\":\"valid\", \"json\":\"for\"}, \"test\"]";

    String id = client.store(valid_json, true);

    assertNotNull("Id is null", id);
    assertTrue("Wrong type of id", id.matches("\\d{6}"));

    String get = client.get(id);

    assertEquals("Got not the same data that was sent", valid_json, get);

    String collName1 = "TestColl1";
    String collName2 = "TestColl2";

    String head_id = client.store("Head");
    assertTrue("Wrong type of id",
            head_id != null && head_id.matches("\\d{6}"));

    String sid1 = client.makeColl(head_id, collName1);
    assertNotNull("Error while making collection 1", sid1);

    String sid2 = client.makeColl(id, collName2);
    assertNotNull("Error while making collection 2", sid2);

    assertTrue("Error while inserting an object into collection.",
            client.insertColl(sid1, id));

    String[] getCollBySid = client.getCollBySid(sid1);
    assertTrue(head_id.equals(getCollBySid[0]) && id.equals(getCollBySid[1]));

    assertTrue("Error while removing object from collection.",
            client.removeColl(sid1, id));

    String[] getCollBySid2 = client.getCollBySid(sid1);

    assertTrue(client.deleteCollBySid(sid1));
    assertTrue(client.deleteCollBySid(sid2));

    // should be null, as coll was deleted
    String[] getCollBySid1_deleted = client.getCollBySid(sid1);
    assertNull(getCollBySid1_deleted);

    assertTrue(client.delete(id));
    assertTrue(client.delete(head_id));

    assertNull("Data was not deleted", client.get(id));
    assertNull("Head was not deleted", client.get(head_id));
}

/** Sending JSONs with validation enabled */
@Test
public void testJsonValidation() throws IOException {
    String valid_json = "[{\"some\":\"valid\", \"json\":\"for\"}, \"test\"]";

    assertNotNull("Not approved valid JSON", client.store(valid_json, true));

    String[] invalid_jsons = new String[]{
            "some invalid jsons for tests",
            "[some invalid jsons for tests]",
            "{some invalid jsons for tests}",
            "[{\"some\":\"invalid\", \"json\":\"for\"} \"test\"]",
            "[{\"some\":\"invalid\", \"json\"\"for\"}, \"test\"]",
            "[{\"some\":\"invalid\", \"json\":\"for\"}, \"test\"]a"
    };
    for (String s : invalid_jsons) {
        assertNull("Invalid json wasn't detected.", client.store(s, true));
    }
}

@Test
public void testSearchColl() throws IOException {
    String id = "-";
    String[] sids = client.searchColl(id);
    assertNotNull("Null instead of empty array!", sids);

    id = "000001";
    sids = client.searchColl(id);
    assertTrue("Zero ids returned for id = " + id, sids.length > 0);

}

@Test
public void testSearchObj() throws IOException {
    String text = "some text that would never occur in DB: гроб гроб кладбище пельмешка";
    String[] ids = client.searchObj(text);
    assertNotNull("Null instead of empty array!", ids);
    assertEquals("Zero ids returned for text = " + text, 0, ids.length);

    text = "Enter"; // part of "Enter text to search here..."
    ids = client.searchObj(text);
    assertTrue("Zero ids returned for text = " + text, ids.length > 0);
}

@Test
public void testChange() throws IOException {
    String id = client.store("Initial");
    assertEquals("Got not what stored.", "Initial", client.get(id));
    assertTrue("Changed returned false.", client.change(id, "Changed"));
    assertEquals("Not changed, actually.", "Changed", client.get(id));
}

@Test
public void testStat() throws IOException {
    IkarusApi.StatResult res = client.stat();
    assertNotNull("Result of Stat is null", res);
    assertNotEquals("No objects found in DB.", 0, res.ids.length);
    assertNotEquals("No s-collections found in DB.", 0, res.cols.size());
}

}
