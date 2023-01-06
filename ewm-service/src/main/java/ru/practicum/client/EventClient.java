package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.event.dto.EndPointHitDto;

import java.util.List;

@Service
public class EventClient extends BaseClient {

    @Autowired
    public EventClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void add(EndPointHitDto endPointHitDto) {
        post("/hit", endPointHitDto);
    }

    public ResponseEntity<Object> getViews(List<Long> ids) {
        StringBuilder path = new StringBuilder();
        for (long l : ids) {
            path.append("&uris=/events/" + l + "&");
        }
        return get("/stats?start=1000-10-10 10:10:10&end=9000-10-10 10:10" + path + "unique=false", null);
    }
}
