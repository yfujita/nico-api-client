package jp.niconico.api.method;

import jp.niconico.api.exception.NiconicoException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicoLogin {
    private Logger logger = LoggerFactory.getLogger(NicoLogin.class);

    public void excute(HttpClient client, String mail, String password) throws NiconicoException {
        if (StringUtils.isBlank(mail) || StringUtils.isBlank(password)) {
            throw new NiconicoException("Login parameters was empty.");
        }

        try {
            HttpPost httpPost = new HttpPost(
                    "https://secure.nicovideo.jp/secure/login?site=niconico");
            Header[] headers = {new BasicHeader("Content-type",
                    "application/x-www-form-urlencoded")};
            httpPost.setHeaders(headers);
            httpPost.setEntity(new StringEntity("mail=" + mail + "&password="
                    + password, "UTF-8"));

            HttpResponse response = client.execute(httpPost);
            EntityUtils.consumeQuietly(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 302) {
                throw new NiconicoException("Login request error.");
            }

            //Check redirect URL
            Header[] resHeaders = response.getHeaders("Location");
            for (Header header : resHeaders) {
                if ("Location".equals(header.getName())) {
                    if (header.getValue().contains("https://secure.nicovideo.jp/secure/login")) {
                        throw new NiconicoException("Invalid mail or password");
                    }
                    break;
                }
            }

        } catch (Exception e) {
            logger.warn("Failed to login -> " + mail, e);
            throw new NiconicoException(e.getMessage());
        } finally {
            //ignore
        }
    }
}
