package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
}
