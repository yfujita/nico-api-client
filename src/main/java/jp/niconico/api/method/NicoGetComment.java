package jp.niconico.api.method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.niconico.api.entity.CommentInfo;
import jp.niconico.api.entity.FlvInfo;
import jp.niconico.api.exception.NiconicoException;

public class NicoGetComment {
    private Logger logger = LoggerFactory.getLogger(NicoGetComment.class);

    private String threadKeyUrl = "http://flapi.nicovideo.jp/api/getthreadkey?thread=";

    private String waybackKeyUrl = "http://flapi.nicovideo.jp/api/getwaybackkey?thread=";

    private FlvInfo flvInfo = null;

    public NicoGetComment(FlvInfo flvInfo) {
        this.flvInfo = flvInfo;
    }

    public List<CommentInfo> excute(HttpClient client, String id) throws NiconicoException {
        return excute(client, id, null);
    }

    public List<CommentInfo> excute(HttpClient client, String id, Date date) throws NiconicoException {
        List<CommentInfo> list = new ArrayList<CommentInfo>();

        try {
            HttpGet httpGet = new HttpGet(threadKeyUrl + flvInfo.threadId);
            HttpResponse response = client.execute(httpGet);

            String[] tmps = EntityUtils.toString(response.getEntity()).split(
                    "&");
            String threadKey = null;
            String force_184 = null;
            for (String tmp : tmps) {
                String[] pair = tmp.split("=");
                if ("threadkey".equals(pair[0])) {
                    if (pair.length < 2 || StringUtils.isBlank(pair[1])) {
                        threadKey = null;
                    } else {
                        threadKey = pair[1];
                    }
                } else if ("force_184".equals(pair[0])) {
                    if (pair.length < 2 || StringUtils.isBlank(pair[1])) {
                        force_184 = null;
                    } else {
                        force_184 = pair[1];
                    }

                }
            }

            String waybackKey = null;
            long when = 0;
            if (date != null) {
                httpGet = new HttpGet(waybackKeyUrl + flvInfo.threadId);
                response = client.execute(httpGet);
                tmps = EntityUtils.toString(response.getEntity()).split(
                        "&");
                for (String tmp : tmps) {
                    String[] pair = tmp.split("=");
                    if ("waybackkey".equals(pair[0])) {
                        if (pair.length < 2 || StringUtils.isBlank(pair[1])) {
                            waybackKey = null;
                        } else {
                            waybackKey = pair[1];
                        }
                    }
                }

                when = date.getTime() / 1000;
            }

            long length = (flvInfo.l / 60) + 1;
            StringBuilder xml = new StringBuilder();
            if (StringUtils.isBlank(threadKey)) {
                xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                xml.append("<packet>");
                xml.append("<thread ");
                xml.append("thread=\"" + flvInfo.threadId + "\" ");
                xml.append("version=\"20090904\" ");
                if (StringUtils.isNotBlank(waybackKey)) {
                    xml.append("waybackkey=\"" + waybackKey + "\" ");
                    xml.append("when=\"" + when + "\" ");
                }
                xml.append("user_id=\"" + flvInfo.userId + "\"");
                xml.append("/>");
                xml.append("<thread_leaves ");
                xml.append("thread=\"" + flvInfo.threadId + "\" ");
                if (StringUtils.isNotBlank(waybackKey)) {
                    xml.append("waybackkey=\"" + waybackKey + "\" ");
                    xml.append("when=\"" + when + "\" ");
                }
                xml.append("user_id=\"" + flvInfo.userId + "\"");
                xml.append(">");
                xml.append("0-" + length + ":100,1000");
                xml.append("</thread_leaves>");
                xml.append("</packet>");
            } else {
                // TODO 動作確認してない
                xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                xml.append("<packet>");
                xml.append("<thread ");
                xml.append("thread=\"" + flvInfo.threadId + "\" ");
                xml.append("version=\"20090904\" ");
                if (StringUtils.isNotBlank(waybackKey)) {
                    xml.append("waybackkey=\"" + waybackKey + "\" ");
                    xml.append("when=\"" + when + "\" ");
                }
                xml.append("user_id=\"" + flvInfo.userId + "\"　");
                xml.append("threadkey=\"" + threadKey + "\"　");
                if (StringUtils.isNotBlank(force_184)) {
                    xml.append("force_184=\"" + force_184 + "\"");
                }
                xml.append("/>");
                xml.append("<thread_leaves ");
                xml.append("thread=\"" + flvInfo.threadId + "\" ");
                if (StringUtils.isNotBlank(waybackKey)) {
                    xml.append("waybackkey=\"" + waybackKey + "\" ");
                    xml.append("when=\"" + when + "\" ");
                }
                xml.append("user_id=\"" + flvInfo.userId + "\" ");
                xml.append("threadkey=\"" + threadKey + "\"　");
                if (StringUtils.isNotBlank(force_184)) {
                    xml.append("force_184=\"" + force_184 + "\"");
                }
                xml.append(">");
                xml.append("0-" + length + ":100,1000");
                xml.append("</thread_leaves>");
                xml.append("</packet>");
            }

            HttpPost httpPost = new HttpPost(flvInfo.ms);
            httpPost.setEntity(new StringEntity(xml.toString()));
            response = client.execute(httpPost);

            String responseXml = EntityUtils.toString(response.getEntity(),
                    "UTF-8");
            list = CommentInfo.parse(id, responseXml);
        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }

        return list;
    }
}
