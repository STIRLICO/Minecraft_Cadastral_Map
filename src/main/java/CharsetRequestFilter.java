import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Provider
public class CharsetRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (request.getMediaType() != null) {
            request.setProperty("characterEncoding", "UTF-8");
            Map<String, String> params = new HashMap<>();
            params.put("charset", "UTF-8");
            MediaType type = new MediaType(
                    request.getMediaType().getType(),
                    request.getMediaType().getSubtype(),
                    params
            );
            request.getHeaders().putSingle("Content-Type", type.toString());
        }
    }
}