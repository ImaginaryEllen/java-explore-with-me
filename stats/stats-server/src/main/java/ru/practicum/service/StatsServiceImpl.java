package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Hit;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final AppRepository appRepository;
    private final HitRepository hitRepository;

    @Transactional
    @Override
    public StatsDtoResponse postHit(StatsDtoRequest request) {
        App app = appRepository.save(StatsMapper.toApp(request.getApp()));
        return StatsMapper.toStatsDtoResponse(hitRepository.save(StatsMapper.toHit(app, request)), 0L);
    }

    @Override
    public List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<StatsDtoResponse> statsList = new ArrayList<>();
        List<String> uriList = new ArrayList<>();
        if (uris != null) {
            uriList = Arrays.stream(uris).collect(Collectors.toList());
        }
        if (uriList.isEmpty()) {
            uriList = hitRepository.findAllByTimestampBetween(start, end).stream()
                    .map(Hit::getUri)
                    .distinct()
                    .collect(Collectors.toList());
        }
        for (String uri : uriList) {
            List<Hit> hits = hitRepository.findAllByUriAndTimestampBetween(uri, start, end);
            if (unique) {
                hits = getByUnique(hits, start, end);
            }
            if (!hits.isEmpty()) {
                statsList.add(new StatsDtoResponse(hits.get(0).getApp().getName(), uri, (long) hits.size()));
            }
        }
        return statsList.stream()
                .sorted(Comparator.comparing(StatsDtoResponse::getHits).reversed())
                .collect(Collectors.toList());
    }

    private List<Hit> getByUnique(List<Hit> hits, LocalDateTime start, LocalDateTime end) {
        Set<String> ips = hits.stream().map(Hit::getIp).collect(Collectors.toSet());
        List<Hit> uniqueList = new ArrayList<>();
        if (ips.size() == 1) {
            Hit hit = hits.stream()
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Not found hit"));
            uniqueList.add(hit);
        } else {
            for (String ip : ips) {
                uniqueList.add(hitRepository.findByIpAndByTimestampBetween(ip, start, end)
                        .stream()
                        .findFirst().orElseThrow(() -> new NotFoundException("Not found hit by ip:" + ip)));
            }
        }
        return uniqueList;
    }
}
