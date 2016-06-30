package jp.niconico.api.method;

import jp.niconico.api.entity.RankingInfo;
import jp.niconico.api.exception.NiconicoException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class NicoGetRanking {
    private static final String methodUrl = "http://www.nicovideo.jp/ranking/";

    public enum PeriodType {
        HOURLY("hourly"), DAYLY("daily"), WEEKLY("weeky"), MONTHLY("monthly"), TOTAL("total");

        private final String str;

        PeriodType(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public enum RankingType {
        FAV("fav"), VIEW("view"), RES("res"), MYLIST("mylist");

        private final String str;

        RankingType(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public List<RankingInfo> execute(HttpClient client, PeriodType period, RankingType rankType) throws NiconicoException {
        List<RankingInfo> rankingList = null;
        try {
            StringBuilder url = new StringBuilder(methodUrl);
            if ("res".equals(rankType.toString()) || "mylist".equals(rankType.toString()) || "view".equals(rankType.toString()) || "fav".equals(rankType.toString())) {
                url.append(rankType.toString());
                url.append("/");
            }
            if ("hourly".equals(period.toString()) || "daily".equals(period.toString()) || "weekly".equals(period.toString()) || "monthly".equals(period.toString())
                    || "total".equals(period.toString())) {
                url.append(period.toString());
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
