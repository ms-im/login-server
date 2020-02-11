package ms.im.login;

import io.rsocket.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ms.utils.md5.MD5Utils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LoginSocketAcceptor implements SocketAcceptor {

    private static final String TAG = LoginSocketAcceptor.class.getSimpleName();

    @Getter
    @Setter
    private List<LoginController> loginControllers = new ArrayList<>();

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        String mimeType = connectionSetupPayload.metadataMimeType();
        String metadataUtf8 = connectionSetupPayload.getMetadataUtf8();
        String dataUtf8 = connectionSetupPayload.getDataUtf8();
        int flags = connectionSetupPayload.getFlags();

        String info = TAG + " : " + mimeType + "," + dataUtf8 + "," + metadataUtf8 + "," + flags;
        log.info(info);

        LoginController loginController = new LoginController();
        loginController.setInstanceId(MD5Utils.md5(info));

        return Mono.just(new AbstractRSocket() {
            @Override
            public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                Flux.from(payloads)
                        .subscribe(loginController::processPayload);
                Flux<Payload> channel = Flux.from(loginController);
                return channel;
            }
        });
    }
}