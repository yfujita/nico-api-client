package jp.niconico.api.method;

import jp.niconico.api.entity.RankingInfo;
import jp.niconico.api.exception.NiconicoException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class NicoGetRanking {
    private String methodUrl = "http://www.nicovideo.jp/ranking/";

    public List<RankingInfo> execute(HttpClient client, String period, String rankKind) throws NiconicoException {
        List<RankingInfo> results = null;
        List<RankingInfo> rankingList = null;
        try {
            StringBuilder url = new StringBuilder(methodUrl);
            if ("res".equals(rankKind) || "mylist".equals(rankKind) || "view".equals(rankKind) || "fav".equals(rankKind)) {
                url.append(rankKind);
                url.append("/");
            }
            if ("hourly".equals(period) || "daily".equals(period) || "weekly".equals(period) || "monthly".equals(period)
                    || "total".equals(period)) {
                url.append(period);
                url.append("/");
            }
            url.append("all?rss=2.0");

            HttpGet httpGet = new HttpGet(url.toString());
            HttpResponse response = client.execute(httpGet);

            String xml = EntityUtils.toString(response.getEntity());
            rankingList = RankingInfo.parse(period, rankKind, xml);


        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }


        return rankingList;
    }
}
