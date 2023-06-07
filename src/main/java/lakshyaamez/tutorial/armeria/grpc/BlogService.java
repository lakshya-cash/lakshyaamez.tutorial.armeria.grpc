package lakshyaamez.tutorial.armeria.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BlogService extends BlogServiceGrpc.BlogServiceImplBase {
  private final AtomicInteger idGenetator = new AtomicInteger();
  private final Map<Integer, BlogPost> blogPosts = new ConcurrentHashMap<>();

  @Override
  public void createBlogPost(CreateBlogPostRequest request, StreamObserver<BlogPost> responseObserver) {
    final int id = idGenetator.getAndIncrement();
    final long timestamp = System.currentTimeMillis();
    final BlogPost blogPost = BlogPost.newBuilder()
            .setId(id)
            .setTitle(request.getTitle())
            .setContent(request.getContent())
            .setCreatedAt(timestamp)
            .setModifiedAt(timestamp)
            .build();
    blogPosts.put(id, blogPost);
    responseObserver.onNext(blogPost);
    responseObserver.onCompleted();
  }

  @Override
  public void getBlogPost(GetBlogPostRequest request, StreamObserver<BlogPost> responseObserver) {
    final BlogPost blogPost = blogPosts.get(request.getId());
    if (blogPost == null) {
      throw new BlogNotFoundException("The blog post does not exist. ID: " + request.getId());
    } else {
      responseObserver.onNext(blogPost);
      responseObserver.onCompleted();
    }
  }

  @Override
  public void listBlogPosts(ListBlogPostsRequest request, StreamObserver<ListBlogPostsResponse> responseObserver) {
    final Collection<BlogPost> blogPosts;
    if (request.getDescending()) {
      blogPosts = this.blogPosts
              .entrySet()
              .stream().
              sorted(Comparator.comparingInt(Map.Entry::getKey))
              .map(Map.Entry::getValue)
              .collect(Collectors.toList());
    } else {
      blogPosts = new ArrayList<>(this.blogPosts.values());
    }
    responseObserver.onNext(
            ListBlogPostsResponse.newBuilder()
                    .addAllBlogs(blogPosts)
                    .build());
    responseObserver.onCompleted();
  }

  @Override
  public void updateBlogPost(UpdateBlogPostRequest request, StreamObserver<BlogPost> responseObserver) {
    final BlogPost oldBlogPost = blogPosts.get(request.getId());
    if (oldBlogPost == null) {
      responseObserver.onError(
              Status.NOT_FOUND
                      .withDescription("The blog post does not exist. ID: " + request.getId())
                      .asRuntimeException());
    } else {
      final BlogPost newBlogPost = BlogPost.newBuilder()
              .setId(request.getId())
              .setTitle(request.getTitle())
              .setContent(request.getContent())
              .setCreatedAt(oldBlogPost.getCreatedAt())
              .setModifiedAt(System.currentTimeMillis())
              .build();
      blogPosts.put(request.getId(), newBlogPost);
      responseObserver.onNext(newBlogPost);
      responseObserver.onCompleted();
    }
  }

  @Override
  public void deleteBlogPost(DeleteBlogPostRequest request, StreamObserver<Empty> responseObserver) {
    final BlogPost removed = blogPosts.remove(request.getId());
    if (removed == null) {
      responseObserver.onError(Status.NOT_FOUND
              .withDescription("The blog post does not exist. ID: " + request.getId())
              .asRuntimeException());
    } else {
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    }
  }
}
