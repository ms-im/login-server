package ms.im.login;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.ms.utils.md5.MD5Utils;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

@Slf4j
public class LoginController implements Publisher<Payload> {
    private static final String TAG = LoginController.class.getSimpleName();

    @Getter
    @Setter
    private String instanceId;

    public void processPayload(Payload payload) {
        String dataUtf8 = payload.getDataUtf8();
        String metadataUtf8 = payload.getMetadataUtf8();

        log.info(TAG + " : " + dataUtf8 + "," + metadataUtf8);

        JSONObject data = new JSONObject(dataUtf8);

        if (!data.isNull("type")) {
            String type = data.getString("type");
            if ("login-app".equals(type)) {
            }
            if ("login-username".equals(type)) {
                JSONObject metadata = new JSONObject(metadataUtf8);
                String username = metadata.getString("username");
                String password = metadata.getString("password");
                if ("maohuawei".equals(username) && "maohuawei".equals(password)) {
                    JSONObject resData = new JSONObject();
                    resData.put("res", "success");
                    JSONObject resMetaData = new JSONObject();
                    resMetaData.put("access_token", this.instanceId + MD5Utils.md5(username + password + instanceId));
                    if (mSubscriber != null)
                        mSubscriber.onNext(DefaultPayload.create(resData.toString(), resMetaData.toString()));
                } else {
                    JSONObject resData = new JSONObject();
                    resData.put("res", "failure");
                    JSONObject resMetaData = new JSONObject();
                    resMetaData.put("error", "username or password error");
                    if (mSubscriber != null)
                        mSubscriber.onNext(DefaultPayload.create(resData.toString(), resMetaData.toString()));
                }
            }
        }
    }

    @Getter
    private Subscriber<? super Payload> mSubscriber;

    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
        mSubscriber = subscriber;
    }
}