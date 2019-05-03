package at.tugraz.ikarus.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class IkarusApiTest {

final String url = "http://localhost:8080";
//final String url = "http://coronet2.iicm.tugraz.at:8082/";
IkarusApi client = null;

@Test
public void example() throws IOException {
    client = new IkarusApi(url);

    assertEquals("Hello User !", client.hello("User"));

    String data = "Hello! How are you?";

    String id = client.store(data);

    assertTrue("Wrong type of id",
            id != null && id.matches("\\d{6}"));

    String get = client.get(id);

    assertEquals("Got not the same data that was sent", data, get);

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
}