package com.cocos.cocos.pet;

import com.cocos.cocos.config.JpaAuditingConfig;
import com.cocos.cocos.config.QuerydslConfig;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.enums.pet.Gender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaAuditingConfig.class, QuerydslConfig.class})
class PetConcurrencyTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("동일 반려동물 동시 수정 시 하나만 성공하고 하나는 낙관락 충돌이 발생한다")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updatePetOptimisticLockConflict() throws Exception {
        // given
        Pet savedPet = petRepository.saveAndFlush(
                Pet.builder()
                        .name("포리")
                        .gender(Gender.M)
                        .age(5)
                        .birthDate(LocalDate.of(2020, 1, 1))
                        .memberId(999L)
                        .breedId(1L)
                        .image("member/basePetImage.png")
                        .build()
        );

        final Long petId = savedPet.getId();
        final CountDownLatch loadedLatch = new CountDownLatch(2);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Boolean> updaterA = createUpdater(petId, "포리-A", loadedLatch, startLatch);
        Callable<Boolean> updaterB = createUpdater(petId, "포리-B", loadedLatch, startLatch);

        // when
        Future<Boolean> f1 = executor.submit(updaterA);
        Future<Boolean> f2 = executor.submit(updaterB);

        assertThat(loadedLatch.await(5, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();

        boolean firstResult = f1.get(5, TimeUnit.SECONDS);
        boolean secondResult = f2.get(5, TimeUnit.SECONDS);

        executor.shutdown();

        // then
        long successCount = List.of(firstResult, secondResult).stream().filter(Boolean::booleanValue).count();
        assertThat(successCount).isEqualTo(1);

        Pet reloaded = petRepository.findById(petId).orElseThrow();
        assertThat(reloaded.getName()).isIn("포리-A", "포리-B");
        assertThat(reloaded.getVersion()).isEqualTo(1L);
    }

    private Callable<Boolean> createUpdater(
            Long petId,
            String nextName,
            CountDownLatch loadedLatch,
            CountDownLatch startLatch
    ) {
        return () -> {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                Pet pet = entityManager.find(Pet.class, petId);
                loadedLatch.countDown();
                startLatch.await(5, TimeUnit.SECONDS);

                pet.updateFields(nextName, null, null, null, null);
                entityManager.flush();
                transaction.commit();
                return true;
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                if (isOptimisticLockFailure(e)) {
                    return false;
                }
                throw e;
            } finally {
                entityManager.close();
            }
        };
    }

    private boolean isOptimisticLockFailure(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof OptimisticLockException || current instanceof PersistenceException) {
                String message = current.getMessage();
                if (message != null && message.toLowerCase().contains("optimistic")) {
                    return true;
                }
                if (current instanceof OptimisticLockException) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }
}
