package jp.niconico.api.method;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import jp.niconico.api.entity.ThumbInfo;
import jp.niconico.api.exception.NiconicoException;

public class NicoGetThumbInfo {
    private String methodUrl = "http://ext.nicovideo.jp/api/getthumbinfo/";

    public ThumbInfo excute(HttpClient client, String id) throws NiconicoException {
        ThumbInfo info = null;
        try {
            HttpGet httpGet = new HttpGet(methodUrl + id);

            HttpResponse response = client.execute(httpGet);

            String xml = EntityUtils.toString(response.getEntity());
            info = ThumbInfo.parse(xml);

        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }

        return info;
    }
}
