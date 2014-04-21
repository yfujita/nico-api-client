package jp.niconico.api.method;

import jp.niconico.api.entity.FlvInfo;
import jp.niconico.api.entity.ThumbInfo;
import jp.niconico.api.exception.NiconicoException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

public class NicoDownloadVideo {
    Logger logger = LoggerFactory.getLogger(NicoDownloadVideo.class);

    private String videoUrl = "http://www.nicovideo.jp/watch/";

    private FlvInfo flvInfo;

    private ThumbInfo thumbInfo;

    private String videoId;

    public NicoDownloadVideo(String videoId, FlvInfo flvInfo, ThumbInfo thumbInfo) {
        this.videoId = videoId;
        this.flvInfo = flvInfo;
        this.thumbInfo = thumbInfo;
    }

    public File execute(HttpClient client, String destDir) throws NiconicoException {
        File dir = new File(destDir);
        if (!dir.isDirectory()) {
            throw new NiconicoException(destDir + " isnt Directory.");
        }
        String fileName = thumbInfo.title;
        String fileType = thumbInfo.movieType;
        File destFile = null;

        //TODO mac（など）で日本語ファイル名文字化け問題暫定
        fileName = "nicovideo-" + thumbInfo.id;

        try {
            HttpHead httpHead = new HttpHead(videoUrl + videoId);
            HttpResponse response = client.execute(httpHead);
            //release entity
            EntityUtils.consumeQuietly(response.getEntity());

            HttpGet httpGet = new HttpGet(flvInfo.url);
            response = client.execute(httpGet);

            logger.info("download video:" + videoId + " title: " + thumbInfo.title + " filesize:"
                    + String.format("%1$,3d", thumbInfo.sizeHigh / 1024) + "KByte");
            logger.info("download start --> " + flvInfo.url);
            long startTime = (new Date()).getTime();
            byte[] buf = EntityUtils.toByteArray(response.getEntity());
            long execTime = (new Date()).getTime() - startTime;
            logger.info("download end.  exec time: " + execTime + "ms");

            destFile = new File(dir.getAbsolutePath() + "/" + fileName + "." + fileType);
            if (destFile.exists()) {
                Date date = new Date();
                long time = date.getTime();
                destFile = new File(dir.getAbsolutePath() + "/" + fileName + "-" + time + "." + fileType);
            }

            FileUtils.writeByteArrayToFile(destFile, buf);
            logger.info("Create video file --> " + destFile.getAbsolutePath());


        } catch (Exception e) {
            throw new NiconicoException(e);
        } finally {
            //ignore
        }


        return destFile;
    }
}
