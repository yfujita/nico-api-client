package jp.niconico.api.client;

import java.io.File;
import java.util.Date;
import java.util.List;

import jp.niconico.api.entity.*;
import jp.niconico.api.exception.NiconicoException;
import jp.niconico.api.http.HttpClientSetting;
import jp.niconico.api.method.*;

import org.apache.http.client.HttpClient;

public class NiconicoApiClient {
    protected final HttpClient client;

    boolean isLogin = false;

    public NiconicoApiClient() {
        client = HttpClientSetting.createHttpClient();
    }

    public NiconicoApiClient(HttpClient client) {
        this.client = client;
    }

    /**
     * niconicoのログイン情報を設定する。
     *
     * @param mail     niconicoアカウント
     * @param password niconicoパスワード
     * @throws NiconicoException
     */
    public void login(String mail, String password) throws NiconicoException {
        NicoLogin method = new NicoLogin();
        method.excute(client, mail, password);
        isLogin = true;
    }

    /**
     * ログイン状態の取得。
     *
     * @return true:ログインしている false:ログインしていない
     */
    public boolean isLogin() {
        return isLogin;
    }

    /**
     * 動画を検索。
     *
     * @param query     検索クエリ
     * @param sort      SortType n:コメント日時 v:再生数 m:マイリスト r:コメント数 f:投稿日時 l:再生時間
     * @param page      取得ページ
     * @param order     OrderType d:降順 a:昇順
     * @param tagSearch false:キーワード検索 true:タグ検索
     * @return 検索結果
     * @throws NiconicoException
     */
    public List<SearchResult> search(String query, NicoSearch.SortType sort, int page,
                                     NicoSearch.OrderType order, boolean tagSearch) throws NiconicoException {
        if (!isLogin) {
            return null;
        }

        NicoSearch method = new NicoSearch();
        return method.excute(client, query, sort, page, order, tagSearch);
    }

    /**
     * Flv情報取得。
     *
     * @param id 動画ID
     * @return Flv情報
     * @throws NiconicoException
     */
    public FlvInfo getFlv(String id) throws NiconicoException {
        NicoGetFlv method = new NicoGetFlv();
        return method.excute(client, id);
    }

    /**
     * コメント取得。動画上に表示されているコメントを取得する。
     *
     * @param id 動画ID
     * @return コメント
     * @throws NiconicoException
     */
    public List<CommentInfo> getComment(String id) throws NiconicoException {
        FlvInfo flvInfo = getFlv(id);

        NicoGetComment method = new NicoGetComment(flvInfo);
        return method.excute(client, id);
    }

    /**
     * コメント過去ログ取得。プレミアム会員アカウントでのログインが必要。
     *
     * @param id   動画ID
     * @param date 時刻。ここで指定した時点で表示されるコメントが取れる
     * @return コメント
     * @throws NiconicoException
     */
    public List<CommentInfo> getPastComment(String id, Date date) throws NiconicoException {
        FlvInfo flvInfo = getFlv(id);

        NicoGetComment method = new NicoGetComment(flvInfo);
        return method.excute(client, id, date);
    }

    /**
     * 動画情報取得。
     *
     * @param id 動画ID
     * @return 動画情報
     * @throws NiconicoException
     */
    public ThumbInfo getThumbInfo(String id) throws NiconicoException {
        NicoGetThumbInfo method = new NicoGetThumbInfo();
        return method.excute(client, id);
    }

    /**
     * ランキング取得
     *
     * @param period   PeriodType 期間 hourly:1時間 daily:1日 weeky:週間 monthly:月間 total:全ての期間
     * @param rankType RankingType fav:総合 view:再生数 res:コメント数 mylist:マイリスト数
     * @return ランキング情報
     * @throws NiconicoException
     */
    public List<RankingInfo> getRanking(NicoGetRanking.PeriodType period,
                                        NicoGetRanking.RankingType rankType) throws NiconicoException {
        NicoGetRanking method = new NicoGetRanking();
        return method.execute(client, period, rankType);
    }

    /**
     * ログインしているユーザのマイリストを取得。
     *
     * @return マイリスト
     * @throws NiconicoException
     */
    public List<Mylist> getOwnerMylists() throws NiconicoException {
        if(!isLogin) {
            return null;
        }

        NicoGetMylist method = new NicoGetMylist();
        return method.getOwnerMylists(client);
    }

    /**
     * マイリストに登録されているアイテムを取得。
     *
     * @param mylistId マイリストID
     * @return マイリストのアイテム
     * @throws NiconicoException
     */
    public List<MylistItem> getMylistItems(String mylistId) throws NiconicoException {
        NicoGetMylist method = new NicoGetMylist();
        return method.getMylistItems(client, mylistId);
    }

    /**
     * ログインしているユーザーのとりあえずマイリストを取得。
     *
     * @return とりあえずマイリストのアイテム
     * @throws NiconicoException
     */
    public List<MylistItem> getToriaezuMylist() throws NiconicoException {
        if(!isLogin) {
            return null;
        }

        NicoGetMylist method = new NicoGetMylist();
        return method.getToriaezuMylist(client);
    }

    /**
     * 動画をダウンロード。
     *
     * @param id      動画ID
     * @param destDir ダウンロード先ディレクトリ
     * @return ダウンロードされたファイル
     * @throws NiconicoException
     */
    public File downloadVideo(String id, String destDir) throws NiconicoException {
        FlvInfo flvInfo = getFlv(id);
        ThumbInfo thumbInfo = getThumbInfo(id);
        NicoDownloadVideo method = new NicoDownloadVideo(id, flvInfo, thumbInfo);
        return method.execute(client, destDir);
    }

    public void shutdown() {
        client.getConnectionManager().shutdown();
    }
}
