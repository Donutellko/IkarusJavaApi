package at.tugraz.ikarus.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "JavaDoc", "SpellCheckingInspection"})
public class IkarusApi {

/** A url of an Utilities server. */
private final String url;
/** Custom client for talking to Utilities. */
private final HttpClient http;

/**
 * @param url A URL of Utilities server.
 *
 * @throws RuntimeException, if URL is malformed.
 */
public IkarusApi(String url) {
    http = new HttpClient(2000);
    this.url = url.endsWith("/") ? url : url + "/";
    try {
        new URL(this.url);
    } catch (MalformedURLException e) {
        throw new RuntimeException("The provided url (" + this.url + ") is incorrect.");
    }
}


/**
 * Check connectivity with an Engine
 *
 * @param name Your name, as you want the greeting to be personalized. Or a null instead.
 *
 * @return "Hello, [your name]" or "Hello, my friend", if [name] is null
 *
 * @throws IOException
 */
public String hello(String name) throws IOException {
    if (name == null)
        return http.get("");
    else
        return http.get(url + "hello?name=" + name);
}

/**
 * Basic STORE
 *
 * @param data Content to store in the Engine
 *
 * @return id of a stored data
 *
 * @throws IOException
 */
public String store(String data) throws IOException {
    return store(data, false);
}

/**
 * STORE with validation
 *
 * @param data     Content to store in the Engine
 * @param validate If need Utilities server to validate passed JSON
 *
 * @return id of a stored data, or null, if JSON is invalid
 *
 * @throws IOException
 */
public String store(String data, boolean validate) throws IOException {
    String s = URLEncoder.encode(data, "UTF-8");
    String result = http.post(url + "data", "validate=" + validate + "&content=" + s);

    if (result != null && result.matches("\\d{6}")) {
        return result;
    } else {
        System.out.println("STORE: Got invalid id: " + result);
        return null;
    }
}

/**
 * Basic GET
 *
 * @param id Identifier of an object to get from Engine
 *
 * @return Content that is stored in the Engine
 *
 * @throws IOException
 */
public String get(String id) throws IOException {
    return http.get(url + "data?id=" + id);
}


/**
 * Basic CHANGE
 * <p>
 * Change value that is stored in object with specified id,
 * without validating new value as JSON.
 *
 * @param id   Identifier of object to update.
 * @param data New data to store.
 *
 * @return true, if changed successfully, false otherwise
 *
 * @throws IOException
 */
public boolean change(String id, String data) throws IOException {
    return change(id, data, false);
}

/**
 * CHANGE with validation
 * <p>
 * Change value that is stored in object with specified id,
 * without validating new value as JSON.
 *
 * @param id       Identifier of object to update.
 * @param data     New data to store.
 * @param validate true, if need to validate JSON before storing.
 *
 * @return true, if changed successfully, false otherwise
 *
 * @throws IOException
 */
public boolean change(String id, String data, boolean validate) throws IOException {
    String s = URLEncoder.encode(data, "UTF-8");
    String result = http.post(url + "data", "id=" + id + "&validate=" + validate + "&content=" + s);

    return result != null && result.matches("\\d{6} changed");
}

/**
 * Basic DELETE
 *
 * @param id Identifier of an object to delete from Engine
 *
 * @return true, if success (returned value contains "deleted")
 *
 * @throws IOException
 */
public boolean delete(String id) throws IOException {
    return http.delete(url + "data?id=" + id).contains("deleted");
}

/**
 * Basic SEARCHOBJ
 *
 * @param data String to be searched for.
 *
 * @return Array of ids.
 */
public String[] searchObj(String data) throws IOException {
    String s = URLEncoder.encode(data, "UTF-8");
    String ids = http.post(url + "data/search", "text=" + s);
    return ids == null || ids.equals("null") ? new String[0] : ids.split(", ");
}

/**
 * Basic MAKECOLL
 *
 * @param sid  An id of a collection
 * @param name A new name for a collection
 *
 * @return S-collection id ("s-000010")
 *
 * @throws IOException
 */
public String makeColl(String sid, String name) throws IOException {
    String s = "sid=" + sid + "&name=" + name;
    String res = http.post(url + "coll", s);
    if (res.matches("s-\\d{6}\\(.+\\)")) {
        return res.substring(0, res.indexOf("("));
    } else {
        System.out.println("MAKECOLL: Got invalid id: " + res);
        return null;
    }
}

/**
 * Basic GETCOLL by sid
 *
 * @param sid An id of a collection
 *
 * @return List of objects in Collection
 *
 * @throws IOException
 */
public String[] getCollBySid(String sid) throws IOException {
    String s = http.get(url + "coll?sid=" + sid);
    return s.startsWith("ERR:") ? null : s.split(",");
}

/**
 * Basic DELETECOLL
 *
 * @param sid Identifier of a collection to delete from Engine
 *
 * @return true, if success (returned "deleted")
 *
 * @throws IOException
 */
public boolean deleteCollBySid(String sid) throws IOException {
    return http.delete(url + "coll?sid=" + sid).contains("deleted");
}


/**
 * Basic SEARCHCOLL
 *
 * @param id Identifier of an object to search for.
 *
 * @return Array of S-Collection ids.
 *
 * @throws IOException
 */
public String[] searchColl(String id) throws IOException {
    String ids = http.get(url + "coll/search?id=" + id);
    return ids == null || "null".equals(ids) ? new String[0] : ids.split(", ");
}


/**
 * Basic INSERTCOLL
 *
 * @param sid Identifier of a collection
 * @param id  Identifier of an object to insert into collection
 *
 * @return true, if success (returned "successfully")
 *
 * @throws IOException
 */
public boolean insertColl(String sid, String id) throws IOException {
    String s = "sid=" + sid + "&id=" + id;
    return http.post(url + "incoll", s).contains("successfully inserted");
}

/**
 * Basic REMOVECOLL
 *
 * @param sid Identifier of a collection
 * @param id  Identifier of an object to remove from collection
 *
 * @return true, if success (returned "successfully")
 *
 * @throws IOException
 */
public boolean removeColl(String sid, String id) throws IOException {
    String s = "sid=" + sid + "&id=" + id;
    return http.delete(url + "incoll?" + s).contains("successfully");
}


public StatResult stat() throws IOException {
    String res = http.get(url + "stat");
    String ids = res.substring(1, res.indexOf("]["));
    String cols = res.substring(res.indexOf("][") + 2, res.length() - 1);
    StatResult result = new StatResult();
    result.ids = ids.split(", ");
    String[] tmp = cols.split(", ");
    for (String s : tmp) {
        int i = s.indexOf("=");
        result.cols.put(s.substring(0, i), s.substring(i + 1));
    }
    return result;
}


/**
 * A class to represent a result of STAT request.
 * It contains a list of object ids and a map of pairs: S-collection id to its name.
 */
public class StatResult {
    String[] ids;
    Map<String, String> cols = new HashMap<>();

    public String[] getIds() {
        return ids;
    }

    public Map<String, String> getCols() {
        return cols;
    }
}
}
