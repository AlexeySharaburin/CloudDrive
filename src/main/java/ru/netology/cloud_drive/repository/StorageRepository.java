package ru.netology.cloud_drive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_drive.model.Storage;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    List<Storage> findByUserIdAndIsExist(long userId, boolean isExist);

    @Query("select s from Storage s where s.filename = :filename and s.userId = :userId and s.isExist=true")
    Storage findByFilenameAndUserId(@Param("filename") String filename, @Param("userId") long currentUserId);
}
