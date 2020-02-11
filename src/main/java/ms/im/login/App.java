package ms.im.login;

import io.netty.buffer.ByteBuf;
import io.rsocket.RSocketFactory;
import io.rsocket.resume.InMemoryResumableFramesStore;
import io.rsocket.resume.ResumableFramesStore;
import io.rsocket.transport.netty.server.TcpServerTransport;

import java.time.Duration;
import java.util.function.Function;

public class App {

    private static final String HOST = "192.168.1.8";
    private static final int PORT = 9006;

    public static void main(String[] args) {

        LoginSocketAcceptor loginSocketAcceptor = new LoginSocketAcceptor();

        final Function<? super ByteBuf, ? extends ResumableFramesStore> resumeStoreFactory =
                token -> new InMemoryResumableFramesStore("login-server", 100_000);

        Duration resumeSessionDuration = Duration.ofSeconds(60 * 60 * 24);
        Duration resumeStreamTimeout = Duration.ofSeconds(60 * 60 * 24);

        RSocketFactory.receive()
                .resume()
                .resumeStore(resumeStoreFactory)
                .resumeSessionDuration(resumeSessionDuration)
                .resumeStreamTimeout(resumeStreamTimeout)
                .acceptor(loginSocketAcceptor)
                .transport(TcpServerTransport.create(HOST, PORT))
                .start()
                .subscribe();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
