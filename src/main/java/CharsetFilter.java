import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CharsetFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        MediaType type = response.getMediaType();
        if (type != null) {
            response.getHeaders().putSingle(
                    "Content-Type",
                    new MediaType(
                            type.getType(),
                            type.getSubtype(),
                            "UTF-8"
                    ).toString()
            );
        } else {
            response.getHeaders().putSingle(
                    "Content-Type",
                    "text/html; charset=UTF-8"
            );
        }
    }
}