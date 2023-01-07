package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.event.dto.EndPointHitDto;
import ru.practicum.event.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class EventClient extends BaseClient {

    ObjectMapper om = new ObjectMapper();

    private final String APP_NAME = "main-Service";

    @Autowired
    public EventClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void add(HttpServletRequest request) {
        post("/hit", createEndPoint(request));
    }

    public List<ViewStatsDto> getViews(List<Long> ids) {
        StringBuilder path = new StringBuilder();
        for (long l : ids) {
            path.append("uris=/events/");
            path.append(l);
            if (l != ids.get(ids.size() - 1)) {
                path.append("&");
            }
        }
        ResponseEntity<List<ViewStatsDto>> getViews = get("/hits?" + path, null);
        List<ViewStatsDto> getList = getViews.getBody();
        if (getList.isEmpty()) {
            return List.of();
        }
        return getList;
    }

    private EndPointHitDto createEndPoint(HttpServletRequest request) {
        EndPointHitDto endPointHitDto = new EndPointHitDto();
        endPointHitDto.setIp(request.getRemoteAddr());
        endPointHitDto.setUri(request.getRequestURI());
        endPointHitDto.setApp(APP_NAME);
        return endPointHitDto;
    }
}
