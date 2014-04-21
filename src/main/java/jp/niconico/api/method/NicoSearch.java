package jp.niconico.api.method;

import java.net.URLEncoder;
import java.util.List;

import jp.niconico.api.entity.LoginInfo;
import jp.niconico.api.entity.SearchResult;
import jp.niconico.api.exception.NiconicoException;

import jp.niconico.api.http.HttpClientSetting;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicoSearch {
    private Logger logger = LoggerFactory.getLogger(NicoSearch.class);

    private LoginInfo loginInfo = null;

    private final String methodUrl = "http://ext.nicovideo.jp/api/search/";

    public NicoSearch(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public List<SearchResult> excute(String query, String sort, int page,
                                     String order, boolean tagSearch) throws NiconicoException {

        if (loginInfo == null) {
            throw new NiconicoException("Need to login.");
        }

        List<SearchResult> results = null;
        DefaultHttpClient httpClient = null;
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

            httpClient = HttpClientSetting.createHttpClient();
            httpClient.setCookieStore(loginInfo.cookie);
            HttpGet httpGet = new HttpGet(url.toString());
            HttpResponse response = httpClient.execute(httpGet);

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
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        return results;
    }
}
