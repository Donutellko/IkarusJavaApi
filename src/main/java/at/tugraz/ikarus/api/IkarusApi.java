package at.tugraz.ikarus.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
@SuppressWarnings({"WeakerAccess", "JavaDoc"})
public class IkarusApi {

/** A url of an Utilities server */
private final String url;
private final HttpClient http;

/**  */
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
 * @param name Your name, as you want the greeting to be personalized. Or a null instead.
 * @return "Hello, [your name]" or "Hello, my friend", if [name] is null
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
 * @param data Content to store in the Engine
 * @return id of a stored data
 * @throws IOException
 */
public String store(String data) throws IOException {
    String s = URLEncoder.encode(data, "UTF-8");
    return http.post(url + "data", "content=" + s);
}

/**
 * Basic GET
 * @param id Identifier of an object to get from Engine
 * @return Content that is stored in the Engine
 * @throws IOException
 */
public String get(String id) throws IOException {
    return http.get(url + "data?id=" + id);
}

/**
 * Basic DELETE
 * @param id Identifier of an object to delete from Engine
 * @return true, if success (returned value contains "deleted")
 * @throws IOException
 */
public boolean delete(String id) throws IOException {
    return http.delete(url + "data?id=" + id).contains("deleted");
}

/**
 * Basic MAKECOLL
 * @param sid An id of a collection
 * @param name A new name for a collection
 * @return S-collection id ("s-000010")
 * @throws IOException
 */
public String makeColl(String sid, String name) throws IOException {
    String s = "sid=" + sid + "&name=" + name;
    String res = http.post(url + "coll", s);
    if (!res.matches("s-\\d{6}\\(.+\\)")) return null;
    return res.substring(0, res.indexOf("("));
}

/**
 * Basic GETCOLL by sid
 * @param sid An id of a collection
 * @return List of objects in Collection
 * @throws IOException
 */
public String[] getCollBySid(String sid) throws IOException {
    String s = http.get(url + "coll?sid=" + sid);
    return s.startsWith("ERR:") ? null : s.split(",");
}

/**
 * Basic DELETECOLL
 * @param sid Identifier of a collection to delete from Engine
 * @return true, if success (returned "deleted")
 * @throws IOException
 */
public boolean deleteCollBySid(String sid) throws IOException {
    return http.delete(url + "coll?sid=" + sid).contains("deleted");
}

/**
 * Basic INSERTCOLL
 * @param sid Identifier of a collection
 * @param id Identifier of an object to insert into collection
 * @return true, if success (returned "successfully")
 * @throws IOException
 */
public boolean insertColl(String sid, String id) throws IOException {
    String s = "sid=" + sid + "&id=" + id;
    return http.post(url + "incoll", s).contains("successfully inserted");
}

/**
 * Basic REMOVECOLL
 * @param sid Identifier of a collection
 * @param id Identifier of an object to remove from collection
 * @return true, if success (returned "successfully")
 * @throws IOException
 */
public boolean removeColl(String sid, String id) throws IOException {
    String s = "sid=" + sid + "&id=" + id;
    return http.delete(url + "incoll?" + s).contains("successfully");
}


}
