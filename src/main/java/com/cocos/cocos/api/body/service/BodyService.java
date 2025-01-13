package com.cocos.cocos.api.body.service;

import com.cocos.cocos.api.body.dto.response.BodiesResponse;
import com.cocos.cocos.api.body.dto.response.BodyResponse;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.external.AppDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;
    private final AppDataS3Client appDataS3Client;

    @Transactional(readOnly = true)
    public BodiesResponse getBodies() {
        final List<Body> bodies = bodyRepository.findAll();
        return BodiesResponse.of(
                bodies.stream()
                        .map(body -> BodyResponse.of(body.getId(), body.getName(), appDataS3Client.getPresignedUrl(body.getImage())))
                        .toList()
        );
    }
}
