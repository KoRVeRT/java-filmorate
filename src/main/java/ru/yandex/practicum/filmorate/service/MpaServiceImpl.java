package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> findAll() {
        log.info("Get all ratings.");
        return mpaStorage.findAll();
    }

    @Override
    public Mpa findById(long id) {
        log.info("Get rating with id: {}", id);
        return mpaStorage.findById(id);
    }
}