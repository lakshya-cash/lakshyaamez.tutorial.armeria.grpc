package lakshyaamez.tutorial.armeria.grpc;

import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.grpc.GrpcStatusFunction;
import io.grpc.Metadata;
import io.grpc.Status;

public class GrpcExceptionHandler implements GrpcStatusFunction {

  @Override
  public @Nullable Status apply(RequestContext ctx, Throwable cause, Metadata metadata) {
    if (cause instanceof IllegalArgumentException) {
      return Status.INVALID_ARGUMENT.withCause(cause);
    }
    if (cause instanceof BlogNotFoundException) {
      return Status.NOT_FOUND.withCause(cause).withDescription(cause.getMessage());
    }
    return null;
  }
}
