package jp.niconico.api.method;

import java.net.URLEncoder;
import java.util.List;

import jp.niconico.api.entity.SearchResult;
import jp.niconico.api.exception.NiconicoException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicoSearch {
    private Logger logger = LoggerFactory.getLogger(NicoSearch.class);

    private final String methodUrl = "http://ext.nicovideo.jp/api/search/";

    public List<SearchResult> excute(HttpClient client, String query, String sort, int page,
                                     String order, boolean tagSearch) throws NiconicoException {
        List<SearchResult> results = null;
        try {
            StringBuilder url = new StringBuilder(methodUrl);
            if (tagSearch) {
                url.append("tag");
            } else {
                url.append("search");
            }
            url.append("/" + URLEncoder.encode(query, "UTF-8"));

            url.append("?mode=watch&");
            url.append("order=" + order);
            url.append("&");
            url.append("page=" + page);
            url.append("&");
            url.append("sort=" + sort);

            HttpGet httpGet = new HttpGet(url.toString());
            HttpResponse response = client.execute(httpGet);

            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);

            if (logger.isDebugEnabled()) {
                logger.info("response: " + json);
            }
            results = SearchResult.parse(json);

        } catch (Exception e) {
            throw new NiconicoException("Failed to search. query=" + query + " page=" + page
                    + " sort=" + sort + " order=" + order + " tagSearch="
                    + tagSearch, e);
        } finally {
            //ignore
        }
        return results;
    }
}
