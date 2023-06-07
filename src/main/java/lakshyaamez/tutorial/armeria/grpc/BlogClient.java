package lakshyaamez.tutorial.armeria.grpc;

import com.linecorp.armeria.client.grpc.GrpcClients;

public class BlogClient {
    static BlogServiceGrpc.BlogServiceBlockingStub client;

    public static void main(String[] args) {
        client = GrpcClients.newClient("http://127.0.0.1:8080/", BlogServiceGrpc.BlogServiceBlockingStub.class);

        CreateBlogPostRequest request = CreateBlogPostRequest.newBuilder()
                .setTitle("My First Blog")
                .setContent("Yay")
                .build();

        BlogPost blogPost = client.createBlogPost(request);

        System.out.println(blogPost);
    }
}
