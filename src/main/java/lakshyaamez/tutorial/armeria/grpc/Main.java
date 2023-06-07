package lakshyaamez.tutorial.armeria.grpc;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Server server = newServer(8080);
        server.closeOnJvmShutdown();
        server.start().join();

        logger.info("Server has been started. Serving DocService at http://127.0.0.1:{}/docs",
                server.activeLocalPort());
    }

    private static Server newServer(int port) {
        final GrpcService grpcService = GrpcService.builder()
                .addService(new BlogService())
                .enableUnframedRequests(true)
                .exceptionMapping(new GrpcExceptionHandler())
                .build();

        final BlogPost exampleRequest = BlogPost.newBuilder()
                .setTitle("My First Blog")
                .setContent("Hello Armeria!")
                .build();

        final DocService docService = DocService.builder()
                .exampleRequests(BlogService.class, "CreateBlogPost", exampleRequest)
                .build();

        return Server.builder()
                .http(port)
                .service(grpcService)
                .serviceUnder("/docs", docService)
                .build();
    }


}
