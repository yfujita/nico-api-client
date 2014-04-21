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

    public static enum PeriodType {
        HOURLY("hourly"), DAYLY("daily"), WEEKLY("weeky"), MONTHLY("monthly"), TOTAL("total");

        private final String str;

        private PeriodType(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public static enum RankingType {
        FAV("fav"), VIEW("view"), RES("res"), MYLIST("mylist");

        private final String str;

        private RankingType(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public List<RankingInfo> execute(HttpClient client, PeriodType period, RankingType rankType) throws NiconicoException {
        List<RankingInfo> results = null;
        List<RankingInfo> rankingList = null;
        try {
            StringBuilder url = new StringBuilder(methodUrl);
            if ("res".equals(rankType) || "mylist".equals(rankType) || "view".equals(rankType) || "fav".equals(rankType)) {
                url.append(rankType);
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
            rankingList = RankingInfo.parse(period, rankType, xml);


        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }


        return rankingList;
    }
}
