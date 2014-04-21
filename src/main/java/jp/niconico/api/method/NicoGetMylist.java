package jp.niconico.api.method;

import jp.niconico.api.entity.Mylist;
import jp.niconico.api.entity.MylistItem;
import jp.niconico.api.exception.NiconicoException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class NicoGetMylist {
    private String deflistUrl = "http://nicovideo.jp/api/deflist/list";

    private String mylistgroupUrl = "http://nicovideo.jp/api/mylistgroup/list";

    private String mylistUrl = "http://nicovideo.jp/api/mylist/list?group_id=";

    public List<MylistItem> getToriaezuMylist(HttpClient client) throws NiconicoException {
        List<MylistItem> list = null;
        try {
            HttpGet httpGet = new HttpGet(deflistUrl);

            HttpResponse response = client.execute(httpGet);

            String json = EntityUtils.toString(response.getEntity());
            list = MylistItem.parse(json);
        } catch (Exception e) {
            throw new NiconicoException(e.getMessage());
        } finally {
            //ignore
        }

        return list;
    }

    public List<Mylist> getOwnerMylists(HttpClient client) throws NiconicoException {
        List<Mylist> list = null;
        try {
            HttpGet httpGet = new HttpGet(mylistgroupUrl);
            HttpResponse response = client.execute(httpGet);

            String json = EntityUtils.toString(response.getEntity());
            list = Mylist.parse(json);
        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }

        return list;
    }

    public List<MylistItem> getMylistItems(HttpClient client, String mylistId) throws NiconicoException {
        List<MylistItem> list = null;
        try {
            HttpGet httpGet = new HttpGet(mylistUrl + mylistId);

            HttpResponse response = client.execute(httpGet);

            String json = EntityUtils.toString(response.getEntity());
            list = MylistItem.parse(json);
        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }

        return list;
    }

}
